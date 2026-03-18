import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit {
  isLoggedIn = false;
  userName: string | null = null;
  isMenuOpen = false;

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.checkAuth();
    // Optional: Subscribe to auth state changes if we implemented a Subject in AuthService
  }

  checkAuth() {
    this.isLoggedIn = this.authService.isAuthenticated();
    if (this.isLoggedIn) {
      this.userName = this.authService.getCurrentUser().name;
    }
  }

  logout() {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/']);
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }
}
