import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Login } from './pages/auth/login/login';
import { Register } from './pages/auth/register/register';
import { Verify } from './pages/auth/verify/verify';
import { ForgotPassword } from './pages/auth/forgot-password/forgot-password';
import { ResetPassword } from './pages/auth/reset-password/reset-password';

import { ArticleList } from './pages/articles/article-list/article-list';
import { ArticleDetail } from './pages/articles/article-detail/article-detail';
import { ArticleForm } from './pages/articles/article-form/article-form';

import { ScriptList } from './pages/scripts/script-list/script-list';
import { ScriptDetail } from './pages/scripts/script-detail/script-detail';
import { ScriptForm } from './pages/scripts/script-form/script-form';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'verify', component: Verify },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  
  { path: 'articles', component: ArticleList },
  { path: 'articles/new', component: ArticleForm },
  { path: 'articles/:id', component: ArticleDetail },
  { path: 'articles/:id/edit', component: ArticleForm },

  { path: 'scripts', component: ScriptList },
  { path: 'scripts/new', component: ScriptForm },
  { path: 'scripts/:id', component: ScriptDetail },
  { path: 'scripts/:id/edit', component: ScriptForm },

  { path: '**', redirectTo: '' }
];
