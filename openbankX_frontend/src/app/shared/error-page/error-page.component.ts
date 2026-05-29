import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-error-page',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './error-page.component.html',
  styleUrl: './error-page.component.css'
})
export class ErrorPageComponent {
  code: string = '404';

  constructor(private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      this.code = params['code'] || '404';
    });
  }

  getTitle(): string {
    switch (this.code) {
      case '401': return 'Unauthorized';
      case '403': return 'Access Forbidden';
      case '429': return 'Too Many Requests';
      case '500': return 'Server Error';
      default: return 'Page Not Found';
    }
  }

  getDescription(): string {
    switch (this.code) {
      case '401': return 'Your session has expired or your credentials are invalid. Please log in again.';
      case '403': return 'You do not have permission to access this resource. Contact your administrator.';
      case '429': return 'You have exceeded the API rate limit. Please wait before making more requests.';
      case '500': return 'An unexpected error occurred on the server. Our team has been notified.';
      default: return 'The page you are looking for does not exist or has been moved.';
    }
  }

  getIcon(): string {
    switch (this.code) {
      case '401': return 'fas fa-lock';
      case '403': return 'fas fa-ban';
      case '429': return 'fas fa-tachometer-alt';
      case '500': return 'fas fa-server';
      default: return 'fas fa-search';
    }
  }

  getColor(): string {
    switch (this.code) {
      case '401': return '#f59e0b';
      case '403': return '#ef4444';
      case '429': return '#8b5cf6';
      case '500': return '#dc2626';
      default: return '#667eea';
    }
  }
}
