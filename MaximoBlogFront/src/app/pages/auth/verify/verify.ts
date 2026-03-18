import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './verify.html'
})
export class Verify implements OnInit {
  isLoading = true;
  isSuccess = false;
  message = '';

  constructor(private route: ActivatedRoute, private authService: AuthService) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.verifyToken(token);
      } else {
        this.isLoading = false;
        this.isSuccess = false;
        this.message = 'No verification token provided in the URL.';
      }
    });
  }

  verifyToken(token: string) {
    this.authService.verifyEmail(token).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.isSuccess = true;
        this.message = res.message || 'Email verified successfully! You can now log in.';
      },
      error: (err) => {
        this.isLoading = false;
        this.isSuccess = false;
        this.message = err.error?.message || err.error?.error || 'Verification failed. The token may be invalid or expired.';
      }
    });
  }
}
