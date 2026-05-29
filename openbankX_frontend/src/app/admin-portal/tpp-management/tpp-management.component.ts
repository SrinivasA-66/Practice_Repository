import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TppService } from '../../core/services/tpp.service';
import { TPPApp } from '../../core/models/models';

@Component({
  selector: 'app-tpp-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tpp-management.component.html',
  styleUrl: './tpp-management.component.css'
})
export class TppManagementComponent implements OnInit {
  apps: TPPApp[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  pendingId: number | null = null;

  constructor(private tppService: TppService) {}

  ngOnInit(): void {
    this.loadApps();
  }

  loadApps(): void {
    this.tppService.getApps().subscribe({
      next: (d) => { this.apps = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load TPP applications.'; this.isLoading = false; }
    });
  }

  setStatus(app: TPPApp, status: 'APPROVED' | 'REJECTED'): void {
    this.pendingId = app.tppAppId;
    this.errorMessage = '';

    const request$ = status === 'APPROVED'
      ? this.tppService.approveApp(app.tppAppId)
      : this.tppService.rejectApp(app.tppAppId);

    request$.subscribe({
      next: () => {
        app.status = status as any;
        this.pendingId = null;
        this.successMessage = status === 'APPROVED'
          ? `"${app.appName}" approved and published to API Products.`
          : `"${app.appName}" → REJECTED`;
        setTimeout(() => (this.successMessage = ''), 4000);
      },
      error: () => {
        this.pendingId = null;
        this.errorMessage = 'Failed to update app status.';
      }
    });
  }

  parseScopes(scopeStr: string): string[] {
    try { return JSON.parse(scopeStr); } catch { return []; }
  }
}
