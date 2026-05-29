import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../core/services/account.service';
import { AuthService } from '../../core/services/auth.service';
import { ConsentService } from '../../core/services/consent.service';
import { AccountRef, TransactionRef, Consent } from '../../core/models/models';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-my-accounts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-accounts.component.html',
  styleUrl: './my-accounts.component.css'
})
export class MyAccountsComponent implements OnInit {

  accounts: AccountRef[] = [];
  transactions: TransactionRef[] = [];
  selectedAccount: AccountRef | null = null;

  isLoading = true;
  txnLoading = false;
  errorMessage = '';

  hasAccountAccess = false;
  tppAppName = '';
  tppAppId: number | null = null;

 
  appsWithAccountAccess: Consent[] = [];

  noConsentsAtAll = false;

  constructor(
    private accountService: AccountService,
    private authService: AuthService,
    private consentService: ConsentService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    const qpId = Number(this.route.snapshot.queryParamMap.get('tppAppId'));
    this.tppAppId = qpId > 0 ? qpId : null;

    this.consentService.getConsentsByUser(userId).subscribe({
      next: (consents: Consent[]) => {
        const active = (consents || []).filter(c => c.status === 'ACTIVE');
        this.noConsentsAtAll = active.length === 0;
        this.appsWithAccountAccess = active.filter(c => this.hasScope(c, 'accounts'));

        if (this.tppAppId) {
          
          const consent = active.find(c => c.tppApp?.tppAppId === this.tppAppId);
          if (consent) {
            this.tppAppName = consent.tppApp.appName;
            this.hasAccountAccess = this.hasScope(consent, 'accounts');
            if (this.hasAccountAccess) {
              this.loadAccounts(userId);
              return;
            }
          }
        }

        
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to check consent permissions.';
        this.isLoading = false;
      }
    });
  }

  private hasScope(consent: Consent, scope: string): boolean {
    try {
      const scopes: string[] = JSON.parse(consent.scopeJSON);
      return scopes.includes(scope);
    } catch { return false; }
  }

  private loadAccounts(userId: number): void {
    this.accountService.getAccounts(userId, this.tppAppId).subscribe({
      next: accs => {
        this.accounts = accs || [];
        this.isLoading = false;

   
        const focusId = Number(this.route.snapshot.queryParamMap.get('focus'));
        const focused = focusId > 0 ? this.accounts.find(a => a.accountId === focusId) : undefined;
        const target = focused || this.accounts[0];
        if (target) {
          this.loadTransactions(target);
        } else {
          this.cdr.detectChanges();
        }
      },
      error: () => {
        this.errorMessage = 'Unable to load accounts.';
        this.isLoading = false;
      }
    });
  }

  refreshTransactions(): void {
    if (this.selectedAccount) this.loadTransactions(this.selectedAccount);
  }

  loadTransactions(acc: AccountRef): void {
    this.selectedAccount = acc;
    this.txnLoading = true;
    this.accountService.getTransactions(acc.accountId, this.tppAppId).subscribe({
      next: txns => { this.transactions = txns || []; this.txnLoading = false; this.cdr.detectChanges(); },
      error: () => { this.txnLoading = false; }
    });
  }

  openThroughApp(consent: Consent): void {
    this.router.navigate(['/customer/accounts'], {
      queryParams: { tppAppId: consent.tppApp?.tppAppId }
    }).then(() => window.location.reload());
  }

  goToBrowseApps(): void {
    this.router.navigate(['/customer/apps']);
  }
}
