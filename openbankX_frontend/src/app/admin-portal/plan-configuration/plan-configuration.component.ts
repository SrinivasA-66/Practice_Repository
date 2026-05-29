import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { APIPlan, APIProduct } from '../../core/models/models';

@Component({
  selector: 'app-plan-configuration',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './plan-configuration.component.html',
  styleUrl: './plan-configuration.component.css'
})
export class PlanConfigurationComponent implements OnInit {

  plans: APIPlan[] = [];
  products: APIProduct[] = [];
  isLoading = true;
  isSaving = false;
  showForm = false;
  errorMessage = '';
  successMessage = '';

  editingPlan: APIPlan | null = null;

  form: any = this.blankForm();

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadPlans();
  }

  private blankForm() {
    return {
      productId: null as number | null,
      environment: 'SANDBOX',
      rateLimitPerMin: 100,
      dailyQuota: 1000,
      sla: 99,
      durationValue: 1,
      durationUnit: 'MONTHS'
    };
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: d => this.products = d || [],
      error: () => { this.errorMessage = 'Unable to load API products.'; }
    });
  }

  loadPlans(): void {
    this.isLoading = true;
    this.productService.getPlans().subscribe({
      next: d => { this.plans = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load plans'; this.isLoading = false; }
    });
  }

  resolveProductName(p: APIPlan): string {
    if (p.apiProduct?.name) return p.apiProduct.name;
    const match = this.products.find(prod => prod.productId === p.apiProduct?.productId);
    return match?.name || `#${p.apiProduct?.productId ?? '?'}`;
  }

  startCreate(): void {
    this.editingPlan = null;
    this.form = this.blankForm();
    if (this.products.length > 0 && this.form.productId == null) {
      this.form.productId = this.products[0].productId;
    }
    this.successMessage = '';
    this.errorMessage = '';
    this.showForm = true;
  }

  edit(plan: APIPlan): void {
    this.editingPlan = plan;
    this.form = {
      productId: plan.apiProduct?.productId ?? null,
      environment: plan.environment,
      rateLimitPerMin: plan.rateLimitPerMin,
      dailyQuota: plan.dailyQuota,
      sla: plan.sla,
      durationValue: plan.durationValue,
      durationUnit: plan.durationUnit
    };
    this.successMessage = '';
    this.errorMessage = '';
    this.showForm = true;
  }

  save(): void {
    this.errorMessage = '';
    if (this.form.productId == null) {
      this.errorMessage = 'Please choose an API product.';
      return;
    }
    if (!['SANDBOX', 'PRODUCTION'].includes(this.form.environment)) {
      this.errorMessage = 'Please choose a valid environment.';
      return;
    }
    if (!Number.isFinite(this.form.rateLimitPerMin) || this.form.rateLimitPerMin < 1) {
      this.errorMessage = 'Rate limit must be at least 1.';
      return;
    }
    if (!Number.isFinite(this.form.dailyQuota) || this.form.dailyQuota < 1) {
      this.errorMessage = 'Daily quota must be at least 1.';
      return;
    }
    if (!Number.isFinite(this.form.sla) || this.form.sla < 0 || this.form.sla > 100) {
      this.errorMessage = 'SLA must be between 0 and 100.';
      return;
    }
    if (!Number.isFinite(this.form.durationValue) || this.form.durationValue < 1) {
      this.errorMessage = 'Duration value must be at least 1.';
      return;
    }
    if (!['DAYS', 'MONTHS', 'YEARS'].includes(this.form.durationUnit)) {
      this.errorMessage = 'Please choose a valid duration unit.';
      return;
    }

    this.isSaving = true;
    const payload = {
      environment: this.form.environment,
      rateLimitPerMin: this.form.rateLimitPerMin,
      dailyQuota: this.form.dailyQuota,
      sla: this.form.sla,
      durationValue: this.form.durationValue,
      durationUnit: this.form.durationUnit,
      apiProduct: { productId: this.form.productId }
    };

    const request$ = this.editingPlan
      ? this.productService.updatePlan(this.editingPlan.planId, payload as any)
      : this.productService.createPlan(payload as any);

    request$.subscribe({
      next: () => {
        const productName = this.products.find(p => p.productId === this.form.productId)?.name || 'product';
        this.successMessage = this.editingPlan
          ? `Plan updated for "${productName}".`
          : `Plan created for "${productName}".`;
        setTimeout(() => (this.successMessage = ''), 3500);
        this.isSaving = false;
        this.loadPlans();
        this.cancel();
      },
      error: err => {
        this.isSaving = false;
        this.errorMessage = err?.error?.message || (this.editingPlan ? 'Update failed' : 'Create failed');
      }
    });
  }

  cancel(): void {
    this.showForm = false;
    this.editingPlan = null;
    this.form = this.blankForm();
    this.errorMessage = '';
  }

  deletePlan(plan: APIPlan): void {
    if (!confirm(`Delete plan #${plan.planId}?`)) return;
    this.productService.deletePlan(plan.planId).subscribe({
      next: () => {
        this.successMessage = `Plan #${plan.planId} deleted.`;
        setTimeout(() => (this.successMessage = ''), 3500);
        this.loadPlans();
      },
      error: err => {
        this.errorMessage = err?.error?.message || 'Delete failed';
      }
    });
  }
}
