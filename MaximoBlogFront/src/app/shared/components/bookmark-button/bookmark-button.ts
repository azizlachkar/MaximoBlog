import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookmarkService } from '../../../core/services/bookmark.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-bookmark-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button *ngIf="authService.isAuthenticated()" 
            (click)="toggleBookmark()" 
            class="group w-10 h-10 rounded-full flex items-center justify-center transition-all bg-black/40 border"
            [ngClass]="isBookmarked ? 'border-maximo-pink text-maximo-pink shadow-[0_0_10px_rgba(255,0,127,0.3)]' : 'border-white/10 text-gray-400 hover:border-white/30 hover:text-white'">
      <svg class="w-5 h-5 transition-transform group-hover:scale-110" 
           [attr.fill]="isBookmarked ? 'currentColor' : 'none'" 
           viewBox="0 0 24 24" 
           stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z"></path>
      </svg>
    </button>
  `
})
export class BookmarkButton implements OnInit {
  @Input() articleId!: number;
  isBookmarked = false;

  constructor(private bookmarkService: BookmarkService, public authService: AuthService) {}

  ngOnInit() {
    if (this.authService.isAuthenticated()) {
      this.checkBookmarkStatus();
    }
  }

  checkBookmarkStatus() {
    // We fetch all user bookmarks and check if articleId exists.
    this.bookmarkService.getUserBookmarks().subscribe({
      next: (bookmarks: any[]) => {
        this.isBookmarked = bookmarks.some(b => b.articleId === this.articleId);
      }
    });
  }

  toggleBookmark() {
    if (this.isBookmarked) {
      this.bookmarkService.removeBookmark(this.articleId).subscribe({
        next: () => this.isBookmarked = false
      });
    } else {
      this.bookmarkService.addBookmark(this.articleId).subscribe({
        next: () => this.isBookmarked = true
      });
    }
  }
}
