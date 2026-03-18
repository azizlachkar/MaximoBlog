import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ArticleService } from '../../../core/services/article.service';

@Component({
  selector: 'app-article-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './article-form.html'
})
export class ArticleForm implements OnInit {
  articleId: number | null = null;
  isEditMode = false;
  
  title = '';
  content = '';
  category = '';
  
  error = '';
  loading = false;
  loadingData = false;

  constructor(
    private articleService: ArticleService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.articleId = Number(id);
        this.isEditMode = true;
        this.loadArticleData();
      }
    });
  }

  loadArticleData() {
    this.loadingData = true;
    this.articleService.getById(this.articleId!).subscribe({
      next: (article: any) => {
        this.title = article.title;
        this.content = article.content;
        this.category = article.category;
        this.loadingData = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load article details for editing.';
        this.loadingData = false;
      }
    });
  }

  onSubmit() {
    this.error = '';
    this.loading = true;

    const payload = {
      title: this.title,
      content: this.content,
      category: this.category
    };

    if (this.isEditMode && this.articleId) {
      this.articleService.update(this.articleId, payload).subscribe(this.handleResponse());
    } else {
      this.articleService.create(payload).subscribe(this.handleResponse());
    }
  }

  handleResponse() {
    return {
      next: (res: any) => {
        this.loading = false;
        // Navigate to the article details page
        this.router.navigate(['/articles', res.id]);
      },
      error: (err: any) => {
        this.loading = false;
        this.error = err.error?.message || err.error?.error || 'Failed to save the article. Please check your inputs.';
      }
    };
  }

  cancel() {
    if (this.isEditMode) {
      this.router.navigate(['/articles', this.articleId]);
    } else {
      this.router.navigate(['/articles']);
    }
  }
}
