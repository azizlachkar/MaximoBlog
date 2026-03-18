import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reset-password.html'
})
export class ResetPassword implements OnInit {
  token = '';
  newPassword = '';
  confirmPassword = '';
  message = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.errorMessage = 'No reset token provided in the URL.';
      }
    });
  }

  onSubmit() {
    this.message = '';
    this.errorMessage = '';

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    if (!this.token) {
      this.errorMessage = 'Invalid or missing reset token.';
      return;
    }

    this.isLoading = true;

    this.authService.resetPassword({ token: this.token, newPassword: this.newPassword }).subscribe({
      next: (res: any) => {
        this.isLoading = false;
        this.message = res.message || 'Password has been reset successfully. You can now log in.';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err: any) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Failed to reset password. Token may be expired.';
      }
    });
  }
}
