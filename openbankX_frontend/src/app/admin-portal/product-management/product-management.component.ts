import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../core/services/product.service';
import { APIProduct, APIPlan, TPPSubscription } from '../../core/models/models';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-management.component.html',
  styleUrl: './product-management.component.css'
})
export class ProductManagementComponent implements OnInit, OnDestroy {

  products: APIProduct[] = [];
  plans: APIPlan[] = [];
  subscriptions: TPPSubscription[] = [];
  isLoading = true;
  isSaving = false;
  showForm = false;
  editMode = false;
  errorMessage = '';
  successMessage = '';

  private refreshTimer: any;

  form: any = this.blankForm();

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadPlans();
    this.loadSubscriptions();
    
    this.refreshTimer = setInterval(() => this.loadSubscriptions(), 15000);
  }

  ngOnDestroy(): void {
    if (this.refreshTimer) clearInterval(this.refreshTimer);
  }

  private blankForm() {
    return {
      productId: null,
      name: '',
      description: '',
      endpointsJSON: '',
      status: 'ACTIVE'
    };
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (d) => { this.products = d || []; this.isLoading = false; },
      error: () => { this.errorMessage = 'Unable to load products.'; this.isLoading = false; }
    });
  }

  loadPlans(): void {
    this.productService.getPlans().subscribe({
      next: (d) => this.plans = d || [],
      error: () => { this.errorMessage = 'Unable to load plans.'; }
    });
  }

  loadSubscriptions(): void {
    this.productService.getSubscriptions().subscribe({
      next: (d) => this.subscriptions = d || [],
      error: () => { this.errorMessage = 'Unable to load subscriptions.'; }
    });
  }

  reloadSubscribers(): void {
    this.loadSubscriptions();
  }

  getPlansForProduct(productId: number): APIPlan[] {
    return this.plans.filter(p => p.apiProduct?.productId === productId);
  }

  getSubscribersForProduct(productId: number): TPPSubscription[] {
    return this.subscriptions.filter(s =>
      s.apiPlan?.apiProduct?.productId === productId
    );
  }

  openCreate(): void {
    this.editMode = false;
    this.form = this.blankForm();
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  edit(p: APIProduct): void {
    this.editMode = true;
    this.form = {
      productId: p.productId,
      name: p.name || '',
      description: p.description || '',
      endpointsJSON: p.endpointsJSON || '[]',
      status: p.status || 'ACTIVE'
    };
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  save(): void 
  {
    if (!this.form.name || this.form.name.trim().length < 3) {
      this.errorMessage = 'Product name is required (min 3 characters).';
      return;
    }
    if (!this.form.description || this.form.description.trim().length < 10) {
      this.errorMessage = 'Description is required (min 10 characters).';
      return;
    }
    if (!this.form.endpointsJSON || !this.form.endpointsJSON.trim()) {
      this.errorMessage = 'Endpoints are required.';
      return;
    }
    try {
      const parsed = JSON.parse(this.form.endpointsJSON);
      if (!Array.isArray(parsed) || parsed.length === 0) {
        this.errorMessage = 'Endpoints must be a non-empty JSON array (e.g. ["/accounts","/payments"]).';
        return;
      }
      if (!parsed.every((e: any) => typeof e === 'string' && e.startsWith('/'))) {
        this.errorMessage = 'Every endpoint must be a string starting with "/".';
        return;
      }
    } catch {
      this.errorMessage = 'Endpoints must be valid JSON.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    const req = this.editMode
      ? this.productService.updateProduct(this.form.productId, this.form)
      : this.productService.createProduct(this.form);

    req.subscribe({
      next: (saved) => {
        if (this.editMode) {
          const idx = this.products.findIndex(x => x.productId === saved.productId);
          if (idx > -1) this.products[idx] = saved;
        } else {
          this.products.unshift(saved);
        }
        this.successMessage = `Product "${saved.name}" saved.`;
        setTimeout(() => (this.successMessage = ''), 3500);
        this.isSaving = false;
        this.showForm = false;
        this.form = this.blankForm();
      },
      error: () => {
        this.isSaving = false;
        this.errorMessage = this.editMode ? 'Failed to update product.' : 'Failed to create product.';
      }
    });
  }

  remove(p: APIProduct): void {
    if (!confirm(`Delete product "${p.name}"?`)) return;

    this.productService.deleteProduct(p.productId).subscribe({
      next: () => {
        this.products = this.products.filter(x => x.productId !== p.productId);
        this.successMessage = `Product "${p.name}" deleted.`;
        setTimeout(() => (this.successMessage = ''), 3500);
      },
      error: () => {
        this.errorMessage = 'Failed to delete product.';
      }
    });
  }

  cancel(): void {
    this.showForm = false;
    this.form = this.blankForm();
    this.errorMessage = '';
  }

  parseJson(s: string): string[] {
    try {
      const parsed = JSON.parse(s);
      return Array.isArray(parsed) ? parsed : [];
    } catch {
      return [];
    }
  }
}
