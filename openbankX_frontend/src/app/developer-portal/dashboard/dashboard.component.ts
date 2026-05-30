import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { TPP, TPPApp, AppStats, TPPSubscription } from '../../core/models/models';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  apps: TPPApp[] = [];
  tpps: TPP[] = [];
  subscriptionsByApp: { [appId: number]: TPPSubscription[] } = {};
  userName = '';
  isLoading = true;
  errorMessage = '';

  stats = [
    { label: 'Total Apps',  value: '0',   icon: 'fas fa-cube',          gradient: '#0a2540' },
    { label: 'API Calls',   value: '0',   icon: 'fas fa-exchange-alt',  gradient: '#1e40af' },
    { label: 'Error Rate',  value: '0%',  icon: 'fas fa-bug',           gradient: '#dc2626' },
    { label: 'Avg Latency', value: '0ms', icon: 'fas fa-clock',         gradient: '#c79a2a' }
  ];

  constructor(
    private tppService: TppService,
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getName();
    this.loadApps();
    this.loadTpps();
  }

  loadApps(): void {
    this.tppService.getMyApps(this.authService.getEmail()).subscribe({
      next: (data) => {
        this.apps = data || [];
        this.stats[0].value = this.apps.length.toString();

        if (this.apps.length > 0) {
          this.tppService.getAppStats(this.apps[0].tppAppId).subscribe({
            next: (statsData) => {
              this.stats[1].value = statsData.totalApiCalls.toLocaleString();
              this.stats[2].value = statsData.errorRate;
              this.stats[3].value = statsData.avgLatencyMs + 'ms';
            },
            error: () => {
              this.errorMessage = 'Unable to load API usage statistics for your first app.';
            }
          });

          for (const app of this.apps) {
            this.productService.getSubscriptionsByApp(app.tppAppId).subscribe({
              next: (subs) => { this.subscriptionsByApp[app.tppAppId] = subs || []; },
              error: () => { this.subscriptionsByApp[app.tppAppId] = []; }
            });
          }
        }
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load apps. Please ensure the backend is running.';
        this.isLoading = false;
      }
    });
  }

  getSubscriptionsFor(appId: number): TPPSubscription[] {
    return this.subscriptionsByApp[appId] || [];
  }

  loadTpps(): void {
    this.tppService.getMyTpps(this.authService.getEmail()).subscribe({
      next: (data) => {
        this.tpps = data || [];
      },
      error: () => {
       
      }
    });
  }

  parseScopes(scopeStr: string): string[] {
    try {
      return JSON.parse(scopeStr);
    } catch { return []; }
  }
}
