import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';
import { AuthService } from '../../core/services/auth.service';
import { TPP } from '../../core/models/models';

@Component({
  selector: 'app-app-registration',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './app-registration.component.html',
  styleUrl: './app-registration.component.css'
})
export class AppRegistrationComponent implements OnInit {

  appName: string = '';
  tppId: number | null = null;
  redirectURIs: string = '';
  publicKeys: string = '';
  selectedScopes: string[] = [];

  myTpps: TPP[] = [];
  isLoadingTpps = true;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  availableScopes = ['accounts', 'payments', 'funds-confirmations'];

  constructor(
    private tppService: TppService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.tppService.getMyTpps(this.authService.getEmail()).subscribe({
      next: (tpps) => {
        this.myTpps = tpps || [];
        if (this.myTpps.length > 0) {
          this.tppId = this.myTpps[0].tppId;
        }
        this.isLoadingTpps = false;
      },
      error: () => {
        this.isLoadingTpps = false;
        this.errorMessage = 'Unable to load your TPP companies.';
      }
    });
  }

  toggleScope(scope: string): void {
    const index = this.selectedScopes.indexOf(scope);
    if (index > -1) {
      this.selectedScopes.splice(index, 1);
    } else {
      this.selectedScopes.push(scope);
    }
  }

  submitApp(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.appName || this.appName.trim().length < 3) {
      this.errorMessage = 'Application name must be at least 3 characters.';
      return;
    }
    if (!this.tppId) {
      this.errorMessage = 'Please select a parent TPP company.';
      return;
    }
    if (!this.redirectURIs || !this.redirectURIs.trim()) {
      this.errorMessage = 'Redirect URIs are required.';
      return;
    }
    try {
      const parsed = JSON.parse(this.redirectURIs);
      if (!Array.isArray(parsed) || parsed.length === 0) {
        this.errorMessage = 'Redirect URIs must be a non-empty JSON array, e.g. ["https://myapp.com/callback"].';
        return;
      }
      const httpsOk = parsed.every((u: any) => typeof u === 'string' && /^https?:\/\//.test(u));
      if (!httpsOk) {
        this.errorMessage = 'Every redirect URI must be a string starting with http:// or https://.';
        return;
      }
    } catch {
      this.errorMessage = 'Redirect URIs must be valid JSON.';
      return;
    }
    if (!this.publicKeys || !this.publicKeys.trim()) {
      this.errorMessage = 'Public keys (JWK Set) are required.';
      return;
    }
    try {
      JSON.parse(this.publicKeys);
    } catch {
      this.errorMessage = 'Public keys must be valid JSON.';
      return;
    }
    if (this.selectedScopes.length === 0) {
      this.errorMessage = 'Select at least one requested scope.';
      return;
    }

    if (this.isSubmitting) return;
    this.isSubmitting = true;

    const appData = {
      tpp: { tppId: this.tppId },
      appName: this.appName.trim(),
      redirectURIs: this.redirectURIs,
      publicKeysJWKSet: this.publicKeys,
      scopesRequested: JSON.stringify(this.selectedScopes),
      status: 'PENDING'
    };

    this.tppService.createApp(appData as any).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = 'Application registered! Awaiting admin approval. Redirecting...';
        this.resetForm();
        setTimeout(() => this.router.navigate(['/developer/apps']), 1200);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err?.error?.message || 'Failed to register application.';
      }
    });
  }

  resetForm(): void {
    this.appName = '';
    this.redirectURIs = '';
    this.publicKeys = '';
    this.selectedScopes = [];
  }
}
