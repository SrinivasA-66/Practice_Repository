import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ConsentService } from '../../core/services/consent.service';
import { AuthService } from '../../core/services/auth.service';
import { Consent } from '../../core/models/models';

@Component({
  selector: 'app-my-consents',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './my-consents.component.html',
  styleUrl: './my-consents.component.css'
})
export class MyConsentsComponent implements OnInit {

  consents: Consent[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';

  
  editingId: number | null = null;
  editingSelected: string[] = [];
  
  editingAllScopes: string[] = [];
  isSavingEdit = false;

  
  private readonly KNOWN_SCOPES = ['accounts', 'balances', 'payments', 'funds-confirmations'];

  constructor(
    private consentService: ConsentService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadConsents();
  }

  loadConsents(): void {
    const userId = this.authService.getUserId();
    this.isLoading = true;
    this.consentService.getConsentsByUser(userId).subscribe({
      next: (d) => {
        this.consents = d || [];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Unable to load consents.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  get pendingConsents(): Consent[] {
    return this.consents.filter(c => c.status === 'AWAITING_SCA');
  }

  get otherConsents(): Consent[] {
    return this.consents.filter(c => c.status !== 'AWAITING_SCA');
  }

  parseJson(str: string): string[] {
    try { return JSON.parse(str) || []; } catch { return []; }
  }

  getScopeLabel(scope: string): string {
    const map: { [k: string]: string } = {
      'accounts':            'View accounts',
      'balances':            'View balances',
      'payments':            'Initiate payments',
      'funds-confirmations': 'Confirm funds availability',
      'accounts:read':       'View account information',
      'balances:read':       'View account balances',
      'payments:write':      'Initiate payments',
      'funds:read':          'Check funds availability'
    };
    return map[scope] || scope;
  }

  goToBrowseApps(): void {
    this.router.navigate(['/customer/apps']);
  }

  goToSca(consent: Consent): void {
    this.router.navigate(['/customer/sca'], {
      queryParams: { consentId: consent.consentId, method: 'OTP' }
    });
  }


  startEdit(consent: Consent): void {
    this.editingId = consent.consentId;
    this.editingSelected = [...this.parseJson(consent.scopeJSON)];

   
    const requested = this.parseJson(consent.tppApp?.scopesRequested || '[]');
    const combined = new Set<string>([
      ...this.editingSelected,
      ...requested,
      ...this.KNOWN_SCOPES.filter(s => this.editingSelected.includes(s))
    ]);
    this.editingAllScopes = Array.from(combined);

    this.errorMessage = '';
    this.successMessage = '';
  }

  editableScopes(_consent: Consent): string[] {
    return this.editingAllScopes;
  }

  toggleEditScope(scope: string): void {
    this.editingSelected = this.editingSelected.includes(scope)
      ? this.editingSelected.filter(s => s !== scope)
      : [...this.editingSelected, scope];
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editingSelected = [];
    this.editingAllScopes = [];
  }

  saveEdit(consent: Consent): void {
    this.errorMessage = '';
    this.isSavingEdit = true;

    this.consentService.updateScopes(consent.consentId, this.editingSelected).subscribe({
      next: (updated) => {
        this.isSavingEdit = false;
   
        consent.scopeJSON = updated.scopeJSON;
        if (updated.status) consent.status = updated.status;

        this.successMessage = this.editingSelected.length === 0
          ? `All permissions withdrawn — consent #${consent.consentId} revoked.`
          : `Permissions updated for consent #${consent.consentId}.`;
        setTimeout(() => (this.successMessage = ''), 3500);

        this.cancelEdit();
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.isSavingEdit = false;
        this.errorMessage = err?.error?.message || 'Failed to update permissions.';
      }
    });
  }

  

  deny(consent: Consent): void {
    if (!confirm(`Deny the consent request from "${consent.tppApp?.appName || 'this app'}"?`)) return;
    this.consentService.revokeConsent(consent.consentId).subscribe({
      next: () => {
        consent.status = 'REVOKED';
        this.successMessage = `Consent request denied.`;
        setTimeout(() => (this.successMessage = ''), 3000);
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Failed to deny consent. Please try again.';
      }
    });
  }

  revoke(consent: Consent): void {
    if (!confirm('Revoke this consent? The app will lose access immediately.')) return;
    this.consentService.revokeConsent(consent.consentId).subscribe({
      next: () => {
        consent.status = 'REVOKED';
        this.successMessage = `Consent #${consent.consentId} revoked.`;
        setTimeout(() => (this.successMessage = ''), 3000);
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = `Failed to revoke consent #${consent.consentId}. Please try again.`;
      }
    });
  }
}
