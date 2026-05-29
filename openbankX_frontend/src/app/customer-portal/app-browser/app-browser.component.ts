import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { ConsentService } from '../../core/services/consent.service';
import { Consent, TPPApp, TPPSubscription } from '../../core/models/models';

@Component({
  selector: 'app-browser',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app-browser.component.html',
  styleUrl: './app-browser.component.css',
})
export class AppBrowserComponent implements OnInit {

  availableApps: TPPApp[] = [];
  isLoading = true;
  errorMessage = '';

  
  consentsByApp = new Map<number, Consent>();

  
  selectedScopesByApp = new Map<number, string[]>();
  appErrors: { [appId: number]: string } = {};
  pendingAppId: number | null = null;

  constructor(
    private tppService: TppService,
    private productService: ProductService,
    private authService: AuthService,
    private consentService: ConsentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAvailableApps();
    this.loadExistingConsents();
  }

  
  private loadAvailableApps(): void {
    this.isLoading = true;
    Promise.all([
      this.tppService.getApps().toPromise(),
      this.productService.getSubscriptions().toPromise()
    ]).then(([apps, subs]) => {
      const activeSubs = (subs || []).filter(s => s.status === 'ACTIVE');
      const subscribedAppIds = new Set<number>(
        activeSubs
          .map((s: TPPSubscription) => s.tppApp?.tppAppId)
          .filter((id): id is number => typeof id === 'number')
      );

      this.availableApps = (apps || []).filter(a =>
        a.status === 'APPROVED' && subscribedAppIds.has(a.tppAppId)
      );
      this.isLoading = false;
    }).catch(() => {
      this.isLoading = false;
      this.errorMessage = 'Unable to load apps. Please ensure the backend is running.';
    });
  }

  private loadExistingConsents(): void {
    const userId = this.authService.getUserId();
    if (!userId) return;
    this.consentService.getConsentsByUser(userId).subscribe({
      next: (consents: Consent[]) => {
        consents
          .filter(c => c.status === 'ACTIVE' && c.tppApp?.tppAppId)
          .forEach(c => this.consentsByApp.set(c.tppApp.tppAppId, c));
      },
      error: () => {
        this.errorMessage = 'Unable to load your existing consents. Already-granted apps may show as ungranted.';
      }
    });
  }



  hasActiveConsent(appId: number): boolean {
    return this.consentsByApp.has(appId);
  }

  activeConsentId(appId: number): number | null {
    return this.consentsByApp.get(appId)?.consentId ?? null;
  }

  consentExpiry(appId: number): string | null {
    return this.consentsByApp.get(appId)?.expiryDate ?? null;
  }

  grantedScopes(appId: number): string[] {
    const consent = this.consentsByApp.get(appId);
    if (!consent) return [];
    try { return JSON.parse(consent.scopeJSON || '[]'); } catch { return []; }
  }

  hasScopeFor(appId: number, scope: string): boolean {
    return this.grantedScopes(appId).includes(scope);
  }

  

  parseScopes(s: string): string[] {
    try { return JSON.parse(s) || []; } catch { return []; }
  }

  isScopeSelected(appId: number, scope: string): boolean {
    return (this.selectedScopesByApp.get(appId) || []).includes(scope);
  }

  toggleScope(appId: number, scope: string): void {
    const current = this.selectedScopesByApp.get(appId) || [];
    const next = current.includes(scope)
      ? current.filter(s => s !== scope)
      : [...current, scope];
    this.selectedScopesByApp.set(appId, next);
    if (this.appErrors[appId]) this.appErrors[appId] = '';
  }

  getScopeLabel(scope: string): string {
    const map: { [k: string]: string } = {
      'accounts':            'View accounts',
      'balances':            'View balances',
      'payments':            'Initiate payments',
      'funds-confirmations': 'Confirm funds availability'
    };
    return map[scope] || scope;
  }

  

  grantAccess(app: TPPApp): void {
    const scopes = this.selectedScopesByApp.get(app.tppAppId) || [];
    if (scopes.length === 0) {
      this.appErrors[app.tppAppId] = 'Pick at least one permission before granting access.';
      return;
    }

    this.appErrors[app.tppAppId] = '';
    this.pendingAppId = app.tppAppId;

    const payload = {
      user: { userId: this.authService.getUserId() },
      tppApp: { tppAppId: app.tppAppId },
      scopeJSON: JSON.stringify(scopes)
    };

    this.consentService.createConsent(payload).subscribe({
      next: (consent: Partial<Consent>) => {
        this.pendingAppId = null;
        this.router.navigate(['/customer/sca'], {
          queryParams: { consentId: consent.consentId, method: 'OTP' }
        });
      },
      error: (err) => {
        this.pendingAppId = null;
        this.appErrors[app.tppAppId] =
          err?.error?.message || 'Could not create the consent — please try again.';
      }
    });
  }


  openAccounts(app: TPPApp): void {
    this.router.navigate(['/customer/accounts'], {
      queryParams: { tppAppId: app.tppAppId }
    });
  }

  openPayments(app: TPPApp): void {
    this.router.navigate(['/customer/payment-initiate'], {
      queryParams: { tppAppId: app.tppAppId }
    });
  }

  openFunds(app: TPPApp): void {
    this.router.navigate(['/customer/funds-check'], {
      queryParams: { tppAppId: app.tppAppId }
    });
  }

  manageConsent(): void {
    this.router.navigate(['/customer/consents']);
  }
}
