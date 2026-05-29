import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConsentService } from '../../core/services/consent.service';
import { Consent } from '../../core/models/models';

@Component({
  selector: 'app-consent-registry',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './consent-registry.component.html',
  styleUrl: './consent-registry.component.css'
})
export class ConsentRegistryComponent implements OnInit {
  consents: Consent[] = [];
  searchTerm = '';
  statusFilter = '';
  isLoading = true;
  errorMessage = '';
  statuses = ['ACTIVE', 'REVOKED', 'EXPIRED'];

  constructor(private consentService: ConsentService) {}

  ngOnInit(): void {
    this.consentService.getConsents().subscribe({
      next: (d) => { this.consents = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load consents.'; this.isLoading = false; }
    });
  }

  get filteredConsents(): Consent[] {
    return this.consents.filter(c => {
      const matchSearch = !this.searchTerm ||
        (c.tppApp?.appName || '').toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        (c.user?.name || '').toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.statusFilter || c.status === this.statusFilter;
      return matchSearch && matchStatus;
    });
  }

  parseJson(s: string): string[] { try { return JSON.parse(s); } catch { return []; } }
}
