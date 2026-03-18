import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ScriptService } from '../../../core/services/script.service';

@Component({
  selector: 'app-script-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './script-form.html'
})
export class ScriptForm implements OnInit {
  scriptId: number | null = null;
  isEditMode = false;
  
  title = '';
  code = '';
  description = '';
  category = 'Bash'; // strict default for select dropdowns
  
  categories = ['Bash', 'Python', 'PowerShell', 'JavaScript', 'Other'];
  
  error = '';
  loading = false;
  loadingData = false;

  constructor(
    private scriptService: ScriptService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.scriptId = Number(id);
        this.isEditMode = true;
        this.loadScriptData();
      }
    });
  }

  loadScriptData() {
    this.loadingData = true;
    this.scriptService.getById(this.scriptId!).subscribe({
      next: (script: any) => {
        this.title = script.title;
        this.code = script.code;
        this.description = script.description;
        this.category = script.category;
        this.loadingData = false;
      },
      error: (err: any) => {
        this.error = 'Failed to load script properties securely.';
        this.loadingData = false;
      }
    });
  }

  onSubmit() {
    this.error = '';
    this.loading = true;

    const payload = {
      title: this.title,
      code: this.code,
      description: this.description,
      category: this.category
    };

    if (this.isEditMode && this.scriptId) {
      this.scriptService.update(this.scriptId, payload).subscribe(this.handleResponse());
    } else {
      this.scriptService.create(payload).subscribe(this.handleResponse());
    }
  }

  handleResponse() {
    return {
      next: (res: any) => {
        this.loading = false;
        this.router.navigate(['/scripts', res.id]);
      },
      error: (err: any) => {
        this.loading = false;
        this.error = err.error?.message || err.error?.error || 'Execution failed. Review parameters.';
      }
    };
  }

  cancel() {
    if (this.isEditMode) {
      this.router.navigate(['/scripts', this.scriptId]);
    } else {
      this.router.navigate(['/scripts']);
    }
  }
}
