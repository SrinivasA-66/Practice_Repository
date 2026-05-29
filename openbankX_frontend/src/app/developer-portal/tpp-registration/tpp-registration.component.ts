import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TppService } from '../../core/services/tpp.service';

@Component({
  selector: 'app-tpp-registration',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tpp-registration.component.html',
  styleUrl: './tpp-registration.component.css'
})
export class TppRegistrationComponent {
  legalName = '';
  registrationNumber = '';

  contactInfo = '';
  certificationRef = '';
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private tppService: TppService,
    private router: Router
  ) {}

  register(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.legalName) {
      this.errorMessage = 'Legal company name is required.';
      return;
    }
    if (!this.registrationNumber) {
      this.errorMessage = 'Registration number is required.';
      return;
    }

    const contact = (this.contactInfo || '').trim();
    if (!contact) {
      this.errorMessage = 'Contact email is required.';
      return;
    }
   
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(contact)) {
      this.errorMessage = 'Please enter a valid contact email.';
      return;
    }

    this.isSubmitting = true;
    this.tppService.registerTpp({
      legalName: this.legalName.trim(),
      registrationNumber: this.registrationNumber.trim(),
      contactInfo: contact,
      certificationRef: (this.certificationRef || '').trim(),
      status: 'SANDBOX'
    }).subscribe({
      next: () => {
        this.successMessage = 'TPP registered successfully! Redirecting to apps...';
        setTimeout(() => this.router.navigate(['/developer/apps']), 1500);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = this.describeError(err);
      }
    });
  }


  private describeError(err: any): string {
    if (err?.status === 0) {
      return 'Cannot reach the backend. Make sure it is running on port 8081.';
    }
    if (err?.status === 401) {
      return 'Your session has expired. Please sign in again.';
    }
    if (err?.status === 403) {
      return 'You do not have permission to register a TPP.';
    }
    const serverMsg: string | undefined =
      err?.error?.message || err?.error?.error || (typeof err?.error === 'string' ? err.error : undefined);
    if (serverMsg) {
      if (/duplicate|unique|already exists|constraint/i.test(serverMsg)) {
        return `A TPP with registration number "${this.registrationNumber}" already exists. Use a different number.`;
      }
      return serverMsg;
    }
    if (err?.status === 409) {
      return `A TPP with registration number "${this.registrationNumber}" already exists.`;
    }
    if (err?.status === 500) {
      return 'Server error while registering TPP. Likely the registration number is already taken — try a different one.';
    }
    return 'Failed to register TPP. Please try again.';
  }
}
