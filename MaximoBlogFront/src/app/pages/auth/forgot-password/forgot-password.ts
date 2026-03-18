import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './forgot-password.html'
})
export class ForgotPassword {
  email = '';
  message = '';
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService) {}

  onSubmit() {
    this.message = '';
    this.errorMessage = '';
    this.isLoading = true;

    this.authService.forgotPassword(this.email).subscribe({
      next: (res: any) => {
        this.isLoading = false;
        this.message = res.message || 'If an account with that email exists, a password reset link has been sent.';
      },
      error: (err: any) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Failed to process request. Please try again later.';
      }
    });
  }
}
