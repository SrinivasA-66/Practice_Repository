import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ConsentService } from '../../core/services/consent.service';
import { Consent } from '../../core/models/models';

@Component({
  selector: 'app-consent-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './consent-detail.component.html',
  styleUrl: './consent-detail.component.css'
})
export class ConsentDetailComponent implements OnInit {
  consent: Consent | null = null;
  isLoading = true;
  errorMessage = '';
  constructor(private route: ActivatedRoute, private consentService: ConsentService) {}
  
  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.consentService.getConsentById(id).subscribe({
      next: (d) => { this.consent = d; this.isLoading = false; },
      error: () => {
        this.errorMessage = 'Unable to load consent details.';
        this.isLoading = false;
      }
    });
  }
  parseJson(s: string): string[] { try { return JSON.parse(s); } catch { return []; } }
  revoke(): void {
    if (this.consent && confirm('Revoke this consent?')) {
      this.consentService.revokeConsent(this.consent.consentId).subscribe({ next: () => { this.consent!.status = 'REVOKED'; } });
    }
  }
}
