import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private apiUrl = `${environment.apiUrl}/api/comments`;

  constructor(private http: HttpClient) {}

  getByArticleId(articleId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/article/${articleId}`);
  }

  addComment(data: { articleId: number, content: string, parentCommentId?: number }): Observable<any> {
    return this.http.post(this.apiUrl, data);
  }

  deleteComment(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
