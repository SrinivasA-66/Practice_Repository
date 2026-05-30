import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name = '';
  email = '';
  phone = '';
  password = '';
  role: 'CUSTOMER' | 'TPP' = 'CUSTOMER';
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';


  roles: { value: 'CUSTOMER' | 'TPP', label: string, icon: string, desc: string }[] = [
    { value: 'CUSTOMER', label: 'Bank Customer', icon: 'fas fa-user', desc: 'Manage consents & accounts' },
    { value: 'TPP', label: 'TPP Developer', icon: 'fas fa-code', desc: 'Register apps & test APIs' }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.name || !this.email || !this.password || !this.phone) {
      this.errorMessage = 'Name, email, phone and password are all required.';
      return;
    }

    if (this.name.trim().length < 2) {
      this.errorMessage = 'Name must be at least 2 characters.';
      return;
    }

    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(this.email)) {
      this.errorMessage = 'Please enter a valid email address.';
      return;
    }

    if (this.password.length < 8 || !/[A-Za-z]/.test(this.password) || !/\d/.test(this.password)) {
      this.errorMessage = 'Password must be at least 8 characters and include both letters and numbers.';
      return;
    }

    const phonePattern = /^[6-9][0-9]{9}$/;
    if (!phonePattern.test(this.phone)) {
      this.errorMessage = 'Phone must be exactly 10 digits and start with 6, 7, 8 or 9.';
      return;
    }

    if (!this.role) {
      this.errorMessage = 'Please select a role.';
      return;
    }

    this.isSubmitting = true;

    this.authService.register({
      name: this.name.trim(),
      email: this.email.trim(),
      phone: this.phone.trim(),
      password: this.password,
      role: this.role
    }).subscribe({
      next: (response: any) => {
        this.authService.saveSession(
          response.userId,
          response.name,
          response.email,
          response.role,
          response.accessToken
        );
        this.successMessage = `Welcome, ${response.name}! Redirecting...`;
        const target = this.routeForRole(response.role);
        setTimeout(() => {
          this.isSubmitting = false;
          this.router.navigate([target]);
        }, 800);
      },
      error: (err: any) => {
        this.isSubmitting = false;
        if (err.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please ensure the backend is running.';
        } else if (err.error?.message) {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Registration failed. Please try again.';
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
