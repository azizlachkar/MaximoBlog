import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ArticleService } from '../../../core/services/article.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-article-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './article-list.html'
})
export class ArticleList implements OnInit {
  articles: any[] = [];
  currentPage = 0;
  totalPages = 0;
  keyword = '';
  category = '';
  loading = true;

  constructor(public authService: AuthService, private articleService: ArticleService) {}

  ngOnInit() {
    this.loadArticles();
  }

  loadArticles() {
    this.loading = true;
    if (this.keyword) {
      this.articleService.search(this.keyword, this.currentPage).subscribe(this.handleResponse());
    } else {
      this.articleService.getAll(this.currentPage).subscribe(this.handleResponse());
    }
  }

  handleResponse() {
    return {
      next: (res: any) => {
        this.articles = res.content;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load articles', err);
        this.loading = false;
      }
    };
  }

  onSearch() {
    this.currentPage = 0;
    this.loadArticles();
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadArticles();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadArticles();
    }
  }
}
