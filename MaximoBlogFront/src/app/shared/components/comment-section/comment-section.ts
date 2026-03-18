import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommentService } from '../../../core/services/comment.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-comment-section',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './comment-section.html'
})
export class CommentSection implements OnInit {
  @Input() articleId!: number;
  comments: any[] = [];
  newCommentContent = '';
  replyingToId: number | null = null;
  replyContent = '';

  constructor(private commentService: CommentService, public authService: AuthService) {}

  ngOnInit() {
    this.loadComments();
  }

  loadComments() {
    if (!this.articleId) return;
    this.commentService.getByArticleId(this.articleId).subscribe({
      next: (res: any) => this.comments = res.comments || [],
      error: (err: any) => console.error('Failed to load comments', err)
    });
  }

  postComment() {
    if (!this.newCommentContent.trim()) return;
    
    this.commentService.addComment({
      articleId: this.articleId,
      content: this.newCommentContent
    }).subscribe({
      next: () => {
        this.newCommentContent = '';
        this.loadComments();
      },
      error: (err: any) => console.error('Failed to post comment', err)
    });
  }

  startReply(commentId: number) {
    this.replyingToId = commentId;
    this.replyContent = '';
  }

  cancelReply() {
    this.replyingToId = null;
    this.replyContent = '';
  }

  postReply(parentCommentId: number) {
    if (!this.replyContent.trim()) return;

    this.commentService.addComment({
      articleId: this.articleId,
      content: this.replyContent,
      parentCommentId
    }).subscribe({
      next: () => {
        this.cancelReply();
        this.loadComments();
      },
      error: (err: any) => alert(err.error?.message || 'Failed to post reply')
    });
  }

  deleteComment(commentId: number) {
    if (confirm('Delete this comment?')) {
      this.commentService.deleteComment(commentId).subscribe({
        next: () => this.loadComments(),
        error: (err: any) => alert(err.error?.message || 'Failed to delete comment')
      });
    }
  }

  isOwner(commentId: number, authorEmail: string): boolean {
    if (!this.authService.isAuthenticated()) return false;
    // Assuming backend returns authorEmail in the comment DTO, otherwise we map roughly by name 
    // Wait, backend CommentResponse returns authorName only.
    // If we only have authorName, and assuming name is unique (or we store name in localStorage)
    const currentName = this.authService.getCurrentUser().name;
    return currentName === authorEmail; // fallback relying on name match for delete button visibility
  }
}
