import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../core/services/account.service';
import { PaymentService } from '../../core/services/payment.service';
import { AuthService } from '../../core/services/auth.service';
import { ConsentService } from '../../core/services/consent.service';
import { AccountRef, Consent, PaymentInitiation } from '../../core/models/models';

type PayStep = 'idle' | 'initiating' | 'verifying' | 'executing' | 'done' | 'failed';

@Component({
  selector: 'app-initiate-payment',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './initiate-payment.component.html',
  styleUrl: './initiate-payment.component.css'
})
export class InitiatePaymentComponent implements OnInit {

  accounts: AccountRef[] = [];

  selectedAccountId: number | null = null;
  payeeAccount = '';
  amount: number | null = null;
  narrative = '';

  successMessage = '';
  errorMessage = '';

  isCheckingPermission = true;
  hasPaymentAccess = false;

  eligibleApps: Consent[] = [];
  noConsentsAtAll = false;

  tppAppId: number | null = null;
  appName = '';

  
  step: PayStep = 'idle';
  
  paymentRecord: PaymentInitiation | null = null;

  preBalance: number | null = null;
  postBalance: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private paymentService: PaymentService,
    private authService: AuthService,
    private consentService: ConsentService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    const qpId = Number(this.route.snapshot.queryParamMap.get('tppAppId'));
    this.tppAppId = qpId > 0 ? qpId : null;

    this.consentService.getConsentsByUser(userId).subscribe({
      next: (consents: Consent[]) => {
        const active = (consents || []).filter(c => c.status === 'ACTIVE');
        this.noConsentsAtAll = active.length === 0;
        this.eligibleApps = active.filter(c => this.hasScope(c, 'payments'));

        if (this.tppAppId) {
          const consent = active.find(c => c.tppApp?.tppAppId === this.tppAppId);
          if (consent) {
            this.appName = consent.tppApp.appName;
            this.hasPaymentAccess = this.hasScope(consent, 'payments');
            if (this.hasPaymentAccess) {
              this.accountService.getAccounts(userId, this.tppAppId).subscribe({
                next: accs => { this.accounts = accs || []; this.isCheckingPermission = false; },
                error: () => {
                  this.errorMessage = 'Unable to load accounts.';
                  this.isCheckingPermission = false;
                }
              });
              return;
            }
          }
        }

        this.isCheckingPermission = false;
      },
      error: () => {
        this.errorMessage = 'Unable to validate consent.';
        this.isCheckingPermission = false;
      }
    });
  }

  private hasScope(consent: Consent, scope: string): boolean {
    try {
      const scopes: string[] = JSON.parse(consent.scopeJSON);
      return scopes.includes(scope);
    } catch { return false; }
  }

  openThroughApp(consent: Consent): void {
    this.router.navigate(['/customer/payment-initiate'], {
      queryParams: { tppAppId: consent.tppApp?.tppAppId }
    }).then(() => window.location.reload());
  }

  goToBrowseApps(): void {
    this.router.navigate(['/customer/apps']);
  }

 

  get selectedAccount(): AccountRef | undefined {
    return this.accounts.find(a => a.accountId === this.selectedAccountId!);
  }

  get currency(): string {
    return this.selectedAccount?.currency || 'GBP';
  }

 

  submitPayment(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedAccountId || !this.payeeAccount || !this.amount || this.amount <= 0) {
      this.errorMessage = 'Please fill all required fields correctly.';
      return;
    }
    const trimmedPayee = this.payeeAccount.trim();
    if (trimmedPayee.length < 4 || !/^[A-Za-z0-9\-\* ]+$/.test(trimmedPayee)) {
      this.errorMessage = 'Recipient account is not in a valid format.';
      return;
    }
    if (this.selectedAccount && trimmedPayee === this.selectedAccount.accountNumberMasked) {
      this.errorMessage = 'Source and recipient accounts cannot be the same.';
      return;
    }
    if (!this.tppAppId) {
      this.errorMessage = 'Open this page through an app to initiate a payment.';
      return;
    }
    if (this.selectedAccount && this.amount > this.selectedAccount.balance) {
      this.errorMessage = `Insufficient funds. Available balance is ${this.currency} ${this.selectedAccount.balance.toFixed(2)}.`;
      return;
    }

    this.preBalance = this.selectedAccount ? this.selectedAccount.balance : null;
    this.step = 'initiating';

    this.paymentService.initiatePayment({
      tppAppId: this.tppAppId,
      debtorAccountRef: this.selectedAccountId.toString(),
      creditorAccountRef: this.payeeAccount,
      amount: this.amount,
      currency: this.currency
    }).subscribe({
      next: (payment) => {
        this.paymentRecord = payment;
        this.step = 'verifying';
        
        setTimeout(() => this.executeStep(payment.paymentId), 700);
      },
      error: (err) => {
        this.step = 'failed';
        this.errorMessage = this.describeError(err, 'Could not create the payment instruction');
      }
    });
  }


  private executeStep(paymentId: number): void {
    this.step = 'executing';

    this.paymentService.executePayment(paymentId, this.tppAppId).subscribe({
      next: (payment) => {
        this.paymentRecord = payment;
        if ((payment.status as any) === 'REJECTED') {
          this.step = 'failed';
          this.errorMessage =
            'Payment rejected by the bank — most likely insufficient funds on the source account.';
          return;
        }
        
        const userId = this.authService.getUserId();
        this.accountService.getAccounts(userId, this.tppAppId).subscribe({
          next: accs => {
            this.accounts = accs || [];
            this.postBalance = this.accounts.find(a => a.accountId === this.selectedAccountId!)?.balance ?? null;
            this.step = 'done';
            this.successMessage = `Payment of ${this.currency} ${this.amount?.toFixed(2)} executed successfully.`;
          },
          error: () => {
            this.step = 'done';
            this.successMessage = `Payment of ${this.currency} ${this.amount?.toFixed(2)} executed successfully.`;
          }
        });
      },
      error: (err) => {
        this.step = 'failed';
        this.errorMessage = this.describeError(err, 'The bank could not execute the transfer');
      }
    });
  }


  private describeError(err: any, fallback: string): string {
    if (err?.name === 'TimeoutError') {
      return 'The bank did not respond within 20 seconds. Check your network connection and try again — or contact support if it keeps failing.';
    }
    if (err?.status === 0) return 'Cannot reach the bank service. Make sure the backend is running on port 8081.';
    if (err?.status === 401) return 'Your session has expired. Please sign in again.';
    if (err?.status === 403) return 'You do not have permission to initiate this payment.';
    const serverMsg = err?.error?.message || err?.error?.error;
    return serverMsg || `${fallback} (HTTP ${err?.status || '?'}).`;
  }



  viewTransactions(): void {
    this.router.navigate(['/customer/accounts'], {
      queryParams: { tppAppId: this.tppAppId, focus: this.selectedAccountId }
    });
  }

  newPayment(): void {
    this.payeeAccount = '';
    this.amount = null;
    this.narrative = '';
    this.paymentRecord = null;
    this.preBalance = null;
    this.postBalance = null;
    this.successMessage = '';
    this.errorMessage = '';
    this.step = 'idle';
  }

  cancel(): void {
    this.router.navigate(['/customer/consents']);
  }
}
