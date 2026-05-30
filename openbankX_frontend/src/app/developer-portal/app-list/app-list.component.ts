import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';
import { ProductService } from '../../core/services/product.service';
import { AuthService } from '../../core/services/auth.service';
import { TPPApp, TPPSubscription } from '../../core/models/models';


@Component({
  selector: 'app-app-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './app-list.component.html',
  styleUrl: './app-list.component.css'
})
export class AppListComponent implements OnInit {
  apps: TPPApp[] = [];
  appSubscriptions: { [appId: number]: TPPSubscription[] } = {};
  isLoading = true;
  errorMessage = '';

  private colors = [
    '#0a2540',
    '#1e40af',
    '#c79a2a',
    '#0e7490',
    '#16a34a'
  ];

  constructor(
    private tppService: TppService,
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.tppService.getMyApps(this.authService.getEmail()).subscribe({
      next: (data) => {
        this.apps = data || [];
        this.isLoading = false;
       
        for (const app of this.apps) {
          this.productService.getSubscriptionsByApp(app.tppAppId).subscribe({
            next: (subs) => { this.appSubscriptions[app.tppAppId] = subs || []; },
            error: () => { this.appSubscriptions[app.tppAppId] = []; }
          });
        }
      },
      error: () => {
        this.errorMessage = 'Unable to load applications.';
        this.isLoading = false;
      }
    });
  }

  parseScopes(str: string): string[] {
    try { return JSON.parse(str); } catch { return []; }
  }

  getAppColor(index: number): string {
    return this.colors[index % this.colors.length];
  }
}

