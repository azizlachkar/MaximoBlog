import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ArticleService } from '../../core/services/article.service';
import { ScriptService } from '../../core/services/script.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html'
})
export class Home implements OnInit {
  latestArticles: any[] = [];
  popularScripts: any[] = [];
  loading = true;

  constructor(
    private articleService: ArticleService,
    private scriptService: ScriptService
  ) {}

  ngOnInit(): void {
    // Fetch latest 3 articles
    this.articleService.getAll(0, 3).subscribe({
      next: (res: any) => this.latestArticles = res.content,
      error: (err: any) => console.error('Failed to load articles', err)
    });

    // Fetch popular 3 scripts
    this.scriptService.getPopular(0, 3).subscribe({
      next: (res: any) => {
        this.popularScripts = res.content;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load scripts', err);
        this.loading = false;
      }
    });
  }
}
