import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';
import { TPPApp } from '../../core/models/models';

@Component({
  selector: 'app-keys-upload',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './keys-upload.component.html',
  styleUrl: './keys-upload.component.css'
})
export class KeysUploadComponent implements OnInit {
  app: TPPApp | null = null;
  jwkSet = '';
  redirectURIs = '';
  isLoading = true;
  isSaving = false;
  saved = false;
  errorMessage = '';
  appId = 0;

  constructor(private route: ActivatedRoute, private tppService: TppService) {}

  ngOnInit(): void {
    this.appId = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.appId) {
      this.isLoading = false;
      this.errorMessage = 'Invalid app id.';
      return;
    }

    this.tppService.getAppById(this.appId).subscribe({
      next: (d) => {
        this.app = d;
        this.jwkSet = d?.publicKeysJWKSet || '';
        this.redirectURIs = d?.redirectURIs || '';
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.errorMessage = 'Unable to load app details.';
      }
    });
  }

  saveKeys(): void {
    if (this.isSaving) return;
    this.errorMessage = '';

    if (!this.jwkSet || !this.jwkSet.trim()) {
      this.errorMessage = 'JWK Set is required.';
      return;
    }
    try { JSON.parse(this.jwkSet); }
    catch {
      this.errorMessage = 'JWK Set must be valid JSON.';
      return;
    }
    if (!this.redirectURIs || !this.redirectURIs.trim()) {
      this.errorMessage = 'Redirect URIs are required.';
      return;
    }
    try {
      const parsed = JSON.parse(this.redirectURIs);
      if (!Array.isArray(parsed) || parsed.length === 0) {
        this.errorMessage = 'Redirect URIs must be a non-empty JSON array.';
        return;
      }
      if (!parsed.every((u: any) => typeof u === 'string' && /^https?:\/\//.test(u))) {
        this.errorMessage = 'Every redirect URI must be a string starting with http:// or https://.';
        return;
      }
    } catch {
      this.errorMessage = 'Redirect URIs must be valid JSON.';
      return;
    }

    this.isSaving = true;
    this.saved = false;

    this.tppService.updateApp(this.appId, {
      publicKeysJWKSet: this.jwkSet,
      redirectURIs: this.redirectURIs
    }).subscribe({
      next: () => {
        this.isSaving = false;
        this.saved = true;
        setTimeout(() => (this.saved = false), 3500);
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = 'Failed to update keys.';
      }
    });
  }
}
