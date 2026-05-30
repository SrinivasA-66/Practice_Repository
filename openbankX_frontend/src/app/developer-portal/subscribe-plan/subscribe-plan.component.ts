import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { APIPlan, TPPApp, TPPSubscription } from '../../core/models/models';
import { ProductService } from '../../core/services/product.service';
import { TppService } from '../../core/services/tpp.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-subscription-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscribe-plan.component.html'
})
export class SubscriptionPlanComponent implements OnInit {

  plans: APIPlan[] = [];
  apps: TPPApp[] = [];
  subscriptions: TPPSubscription[] = [];

  selectedPlanId: number | null = null;
  selectedAppId: number | null = null;

  isLoading = true;
  isSubscribing = false;
  submitAttempted = false;
  
  cancellingId: number | null = null;
  errorMessage = '';
  successMessage = '';

  constructor(
    private productService: ProductService,
    private tppService: TppService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }



  get selectedPlan(): APIPlan | undefined {
    return this.plans.find(p => p.planId === this.selectedPlanId!);
  }

  get selectedAppName(): string {
    const app = this.apps.find(a => a.tppAppId === this.selectedAppId);
    return app ? app.appName : '';
  }


  get plansForSelectedApp(): APIPlan[] {
    if (!this.selectedAppId) return [];
    const app = this.apps.find(a => a.tppAppId === this.selectedAppId);
    if (!app) return [];
    const target = (app.appName || '').trim().toLowerCase();
    return this.plans.filter(p =>
      (p.apiProduct?.name || '').trim().toLowerCase() === target
    );
  }



  loadData(): void {
    this.isLoading = true;

    this.productService.getPlans().subscribe({
      next: p => this.plans = p || [],
      error: () => this.errorMessage = 'Failed to load plans'
    });

    this.tppService.getMyApps(this.authService.getEmail()).subscribe({
      next: a => {
        this.apps = a || [];
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load apps';
        this.isLoading = false;
      }
    });
  }

  loadSubscriptions(): void {
    if (!this.selectedAppId) {
      this.subscriptions = [];
      return;
    }

    this.productService.getSubscriptionsByApp(this.selectedAppId).subscribe({
      next: d => this.subscriptions = d || [],
      error: () => this.subscriptions = []
    });
  }

  onAppChange(): void {
    this.selectedPlanId = null;
    this.successMessage = '';
    this.errorMessage = '';
    this.loadSubscriptions();
  }



  getApprovedApps(): TPPApp[] {
    return this.apps.filter(app => app.status === 'APPROVED');
  }



  subscribe(): void {
    this.submitAttempted = true;
    if (this.selectedPlanId == null || this.selectedAppId == null) {
      this.errorMessage = 'Please select both an app and a plan.';
      return;
    }

    const selectedPlan = this.plans.find(p => p.planId === this.selectedPlanId);
    const selectedApp = this.apps.find(a => a.tppAppId === this.selectedAppId);

    if (!selectedPlan || !selectedApp) {
      this.errorMessage = 'Invalid plan or app selection.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isSubscribing = true;

    
    const payload = {
      apiPlan: { planId: selectedPlan.planId },
      tppApp:  { tppAppId: selectedApp.tppAppId }
    } as Partial<TPPSubscription>;

    this.productService.createSubscription(payload).subscribe({
      next: (saved) => {
        this.isSubscribing = false;
        this.successMessage = `Subscribed "${selectedApp.appName}" to "${selectedPlan.apiProduct?.name || 'plan'}". Active until ${saved?.expiryDate ? new Date(saved.expiryDate).toLocaleDateString() : 'plan expiry'}.`;
        this.selectedPlanId = null;
        this.loadSubscriptions();
        setTimeout(() => this.router.navigate(['/developer/apps']), 2000);
      },
      error: (err) => {
        this.isSubscribing = false;
        const serverMsg = err?.error?.message || err?.error?.error;
        if (err?.status === 0) {
          this.errorMessage = 'Cannot reach the backend. Make sure it is running on port 8081.';
        } else if (err?.status === 401) {
          this.errorMessage = 'Your session has expired. Please sign in again.';
        } else if (serverMsg) {
          this.errorMessage = serverMsg;
        } else {
          this.errorMessage = `Subscription failed (HTTP ${err?.status || '?'})`;
        }
      }
    });
  }



  cancelSubscription(sub: TPPSubscription): void {
    if (!sub?.subscriptionId || sub.status !== 'ACTIVE') return;
    if (!confirm(`Cancel subscription #${sub.subscriptionId}? The app will lose access to "${sub.apiPlan?.apiProduct?.name || 'this plan'}" immediately.`)) {
      return;
    }

    this.cancellingId = sub.subscriptionId;
    this.errorMessage = '';
    this.successMessage = '';

    this.productService.cancelSubscription(sub.subscriptionId).subscribe({
      next: (updated) => {
        this.cancellingId = null;
       
        sub.status = updated?.status || 'CANCELLED';
        this.successMessage = `Subscription #${sub.subscriptionId} cancelled.`;
        this.loadSubscriptions();
        setTimeout(() => (this.successMessage = ''), 3000);
      },
      error: (err) => {
        this.cancellingId = null;
        const serverMsg = err?.error?.message || err?.error?.error;
        this.errorMessage = serverMsg
          || `Failed to cancel subscription #${sub.subscriptionId} (HTTP ${err?.status || '?'}).`;
      }
    });
  }
}
