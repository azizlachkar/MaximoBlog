import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html'
})
export class Register {
  name = '';
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';
    this.isLoading = true;

    this.authService.register({ name: this.name, email: this.email, password: this.password }).subscribe({
      next: (res) => {
        this.isLoading = false;
        // The backend returns a success message for registration with email verification pending.
        this.successMessage = res.message || 'Registration successful! Please check your email to verify your account.';
        // Optionally redirect to login after a delay
        setTimeout(() => this.router.navigate(['/login']), 5000);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || err.error?.error || 'Registration failed. Please try again.';
      }
    });
  }
}
