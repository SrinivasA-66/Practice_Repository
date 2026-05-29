import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  email: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please enter your email and password.';
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

    if (this.password.length < 8) {
      this.errorMessage = 'Password must be at least 8 characters.';
      return;
    }

    this.isLoading = true;

    this.authService.login(this.email, this.password).subscribe({
      next: (response: any) => {
        this.authService.saveSession(
          response.userId,
          response.name,
          response.email,
          response.role,
          response.accessToken
        );

        this.successMessage = `Welcome, ${response.name}!`;

        const target = this.routeForRole(response.role);
        setTimeout(() => {
          this.isLoading = false;
          this.router.navigate([target]);
        }, 400);
      },
      error: (err: any) => {
        this.isLoading = false;
        if (err.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please ensure the backend is running on port 8081.';
        } else if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Invalid email or password. Please try again.';
        }
      }
    });
  }

  private routeForRole(role: string): string {
    switch (role) {
      case 'TPP':        return '/developer/dashboard';
      case 'CUSTOMER':   return '/customer/consents';
      case 'OPERATIONS': return '/operations/health';
      case 'ADMIN':      return '/admin/products';
      default:           return '/login';
    }
  }
}
