import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ScriptService } from '../../../core/services/script.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-script-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './script-list.html'
})
export class ScriptList implements OnInit {
  scripts: any[] = [];
  currentPage = 0;
  totalPages = 0;
  keyword = '';
  category = '';
  loading = true;

  constructor(public authService: AuthService, private scriptService: ScriptService) {}

  ngOnInit() {
    this.loadScripts();
  }

  loadScripts() {
    this.loading = true;
    if (this.keyword) {
      this.scriptService.search(this.keyword, this.currentPage).subscribe(this.handleResponse());
    } else {
      this.scriptService.getAll(this.currentPage, 10, this.category).subscribe(this.handleResponse());
    }
  }

  handleResponse() {
    return {
      next: (res: any) => {
        this.scripts = res.content;
        this.totalPages = res.totalPages;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Failed to load scripts', err);
        this.loading = false;
      }
    };
  }

  onSearch() {
    this.currentPage = 0;
    this.category = ''; // clear category if searching by keyword
    this.loadScripts();
  }

  applyCategoryFilter(cat: string) {
    this.category = cat;
    this.keyword = '';
    this.currentPage = 0;
    this.loadScripts();
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadScripts();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadScripts();
    }
  }
}
