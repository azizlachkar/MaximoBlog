import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookmarkService {
  private apiUrl = `${environment.apiUrl}/api/bookmarks`;

  constructor(private http: HttpClient) {}

  getUserBookmarks(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  addBookmark(articleId: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${articleId}`, {});
  }

  removeBookmark(articleId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${articleId}`);
  }
}
