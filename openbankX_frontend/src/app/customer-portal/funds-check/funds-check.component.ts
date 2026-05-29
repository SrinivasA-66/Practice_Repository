import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../core/services/account.service';
import { AuthService } from '../../core/services/auth.service';
import { ConsentService } from '../../core/services/consent.service';
import { FundsService } from '../../core/services/funds.service';
import { AccountRef, Consent } from '../../core/models/models';

@Component({
  selector: 'app-funds-check',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './funds-check.component.html',
  styleUrl: './funds-check.component.css'
})
export class FundsCheckComponent implements OnInit {

  accounts: AccountRef[] = [];
  selectedAccount: AccountRef | null = null;
  amount: number | null = null;

  appName = '';
  tppAppId: number | null = null;

  isCheckingPermission = true;
  hasFundsAccess = false;

  
  eligibleApps: Consent[] = [];
  
  noConsentsAtAll = false;

  errorMessage = '';
  isChecking = false;
  result: { sufficient: boolean; balance: number; shortage?: number; recordId?: number } | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private authService: AuthService,
    private consentService: ConsentService,
    private fundsService: FundsService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    const qpId = Number(this.route.snapshot.queryParamMap.get('tppAppId'));
    this.tppAppId = qpId > 0 ? qpId : null;

    this.consentService.getConsentsByUser(userId).subscribe({
      next: (consents: Consent[]) => {
        const active = (consents || []).filter(c => c.status === 'ACTIVE');
        this.noConsentsAtAll = active.length === 0;
        this.eligibleApps = active.filter(c => this.hasScope(c, 'funds-confirmations'));

        if (this.tppAppId) {
          const consent = active.find(c => c.tppApp?.tppAppId === this.tppAppId);
          if (consent) {
            this.appName = consent.tppApp.appName;
            this.hasFundsAccess = this.hasScope(consent, 'funds-confirmations');
            if (this.hasFundsAccess) {
              this.accountService.getAccounts(userId, this.tppAppId).subscribe({
                next: accs => { this.accounts = accs || []; this.isCheckingPermission = false; },
                error: () => {
                  this.errorMessage = 'Unable to load accounts for funds check.';
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

  
  onAccountChange(): void {
    this.result = null;
    this.errorMessage = '';
  }

  openThroughApp(consent: Consent): void {
    this.router.navigate(['/customer/funds-check'], {
      queryParams: { tppAppId: consent.tppApp?.tppAppId }
    }).then(() => window.location.reload());
  }

  goToBrowseApps(): void {
    this.router.navigate(['/customer/apps']);
  }

  checkFunds(): void {
    this.result = null;
    this.errorMessage = '';

    if (!this.selectedAccount || !this.amount || this.amount <= 0) {
      this.errorMessage = 'Please select an account and enter a valid amount.';
      return;
    }
    if (!this.tppAppId) {
      this.errorMessage = 'Open this page through an app to run a funds check.';
      return;
    }

    this.isChecking = true;
    this.fundsService.checkFunds({
      tppAppId: this.tppAppId,
      accountId: this.selectedAccount.accountId,
      amount: this.amount,
      currency: this.selectedAccount.currency
    }).subscribe({
      next: (saved: any) => {
        this.isChecking = false;
        const balance = this.selectedAccount!.balance;
        const sufficient = (saved?.result ?? 'SUFFICIENT') === 'SUFFICIENT';
        this.result = sufficient
          ? { sufficient: true, balance, recordId: saved?.fundsCheckId }
          : { sufficient: false, balance, shortage: this.amount! - balance, recordId: saved?.fundsCheckId };
      },
      error: (err) => {
        this.isChecking = false;
        if (err?.name === 'TimeoutError') {
          this.errorMessage = 'The bank did not respond within 20 seconds. Please try again.';
        } else if (err?.status === 0) {
          this.errorMessage = 'Cannot reach the backend. Make sure it is running on port 8081.';
        } else if (err?.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Funds check failed.';
        }
      }
    });
  }
}
