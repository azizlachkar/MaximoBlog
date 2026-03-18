# MaximoBlog API — Complete Documentation

## Project Structure

```
src/main/java/com/example/maximoblog/
├── config/          SecurityConfig
├── controller/      AuthController, ArticleController
├── dto/             Request/Response DTOs
├── entity/          User, Article, Comment, Script, Bookmark,
│                    VerificationToken, PasswordResetToken, Role
├── repository/      JPA Repositories
├── security/        JwtUtil, JwtAuthenticationFilter, CustomUserDetailsService
└── service/         AuthService, EmailService, ArticleService
```

---

## Authentication Endpoints

All `/auth/**` endpoints are **public** (no JWT required).

### 1. Register

```
POST /auth/register
Content-Type: application/json
```
```json
{
    "name": "Maximo",
    "email": "maximo@blog.com",
    "password": "securePass123"
}
```
**201 Created** — Verification email sent. User must verify before logging in.

### 2. Verify Email

```
GET /auth/verify?token=<token-from-email>
```
**200 OK** — Account activated. Token is single-use (24h expiry).

> **Dev tip:** If email isn't configured, get the token from the DB:
> ```sql
> SELECT token FROM verification_tokens WHERE user_id = 1;
> ```

### 3. Login

```
POST /auth/login
Content-Type: application/json
```
```json
{
    "email": "maximo@blog.com",
    "password": "securePass123"
}
```
**200 OK** — Returns JWT token, name, email, role.
**403** — Account not verified.
**401** — Invalid credentials.

### 4. Forgot Password

```
POST /auth/forgot-password
Content-Type: application/json
```
```json
{
    "email": "maximo@blog.com"
}
```
**200 OK** — Reset email sent (same response for unknown emails to prevent enumeration).

### 5. Reset Password

```
POST /auth/reset-password
Content-Type: application/json
```
```json
{
    "token": "<token-from-email>",
    "newPassword": "newSecurePass456"
}
```
**200 OK** — Password changed. Token is single-use (15min expiry).

---

## Article Endpoints

All `/api/**` endpoints require a **JWT token** in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

### 1. Create Article

```
POST /api/articles
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "title": "Getting Started with Spring Boot",
    "content": "Full article content here...",
    "category": "Spring Boot"
}
```
**201 Created** — Returns the created article with author info.

### 2. Get All Articles (Paginated)

```
GET /api/articles?page=0&size=10&sortBy=createdAt&direction=desc
Authorization: Bearer <token>
```
**200 OK** — Returns a paginated response with `content`, `totalElements`, `totalPages`, etc.

| Param | Default | Description |
|---|---|---|
| `page` | `0` | Page number (0-indexed) |
| `size` | `10` | Items per page |
| `sortBy` | `createdAt` | Sort field (`title`, `category`, `createdAt`) |
| `direction` | `desc` | Sort direction (`asc` or `desc`) |

### 3. Get Article by ID

```
GET /api/articles/1
Authorization: Bearer <token>
```
**200 OK** — Returns the article. **404** if not found.

### 4. Update Article

```
PUT /api/articles/1
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "title": "Updated Title",
    "content": "Updated content...",
    "category": "Java"
}
```
**200 OK** — Only the **author** can update. **403** if not the author.

### 5. Delete Article

```
DELETE /api/articles/1
Authorization: Bearer <token>
```
**200 OK** — Only the **author** can delete. **403** if not the author.

### 6. Search Articles

```
GET /api/articles/search?keyword=spring&page=0&size=10
Authorization: Bearer <token>
```
**200 OK** — Searches title and category (case-insensitive). Paginated.

### 7. Get by Category

```
GET /api/articles/category/Spring%20Boot?page=0&size=10
Authorization: Bearer <token>
```
**200 OK** — Filters by exact category. Paginated.

---

## Comment Endpoints

All `/api/comments/**` endpoints require a **JWT token**.

### 1. Add Comment

```
POST /api/comments
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "articleId": 1,
    "content": "Great article! Very helpful."
}
```
**201 Created** — Returns the comment with author info.

### 2. Reply to Comment (Nested)

```
POST /api/comments
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "articleId": 1,
    "content": "Thanks for the feedback!",
    "parentCommentId": 1
}
```
**201 Created** — Max nesting depth is **3 levels**.

### 3. Get Comments for Article

```
GET /api/comments/article/1
Authorization: Bearer <token>
```
**200 OK** — Returns top-level comments (newest first) with nested `replies` array:
```json
{
    "articleId": 1,
    "totalComments": 5,
    "comments": [
        {
            "id": 3,
            "content": "Nice post!",
            "authorName": "Maximo",
            "createdAt": "...",
            "replies": [
                {
                    "id": 4,
                    "content": "Agreed!",
                    "replies": []
                }
            ]
        }
    ]
}
```

### 4. Delete Comment

```
DELETE /api/comments/1
Authorization: Bearer <token>
```
**200 OK** — Only the **comment owner** or an **ADMIN** can delete. Deleting a parent comment also deletes all its replies.

---

## Bookmark Endpoints

All `/api/bookmarks/**` endpoints require a **JWT token**.

### 1. Add Bookmark

```
POST /api/bookmarks/{articleId}
Authorization: Bearer <token>
```
**201 Created** — Returns the bookmark details with article summary.

### 2. Remove Bookmark

```
DELETE /api/bookmarks/{articleId}
Authorization: Bearer <token>
```
**200 OK** — Removes the bookmark.

### 3. Get User Bookmarks

```
GET /api/bookmarks
Authorization: Bearer <token>
```
**200 OK** — Returns a list of all articles bookmarked by the user (newest bookmark first).

---

## Script Library Endpoints

All `/api/scripts/**` endpoints require a **JWT token**.

### 1. Create Script

```
POST /api/scripts
Authorization: Bearer <token>
Content-Type: application/json
```
```json
{
    "title": "React useFetch Hook",
    "code": "const useFetch = (url) => { ... }",
    "description": "Custom hook for fetching data",
    "category": "React"
}
```
**201 Created** — Returns the created script object (views initialize to `0`).

### 2. Get All Scripts (Paginated)

```
GET /api/scripts?page=0&size=10&sortBy=createdAt&direction=desc
Authorization: Bearer <token>
```
**200 OK** — Paginated list of scripts. 

### 3. Get Popular Scripts

```
GET /api/scripts/popular?page=0&size=10
Authorization: Bearer <token>
```
**200 OK** — List of scripts strictly ordered by **`views` (descending)**.

### 4. Get Script by ID (Increments Views)

```
GET /api/scripts/1
Authorization: Bearer <token>
```
**200 OK** — Returns script details. **Every request to this endpoint increments the `views` counter by 1.**

### 5. Update Script

```
PUT /api/scripts/1
Authorization: Bearer <token>
Content-Type: application/json
```
**200 OK** — Only the **author** can update. **403** if not the author.

### 6. Delete Script

```
DELETE /api/scripts/1
Authorization: Bearer <token>
```
**200 OK** — Only the **author** can delete.

### 7. Search Scripts

```
GET /api/scripts/search?keyword=react&page=0&size=10
Authorization: Bearer <token>
```
**200 OK** — Searches `title`, `description`, and `category` (case-insensitive).

### 8. Get Scripts by Category

```
GET /api/scripts/category/React?page=0&size=10
Authorization: Bearer <token>
```
**200 OK** — Filters exactly by category.

---

## Error Responses

| Scenario | Status | Message |
|---|---|---|
| Duplicate registration | `409` | Email is already registered |
| Invalid verification token | `400` | Invalid verification token |
| Expired verification token | `410` | Verification token has expired |
| Login before verification | `403` | Account not verified |
| Wrong credentials | `401` | Invalid email or password |
| Invalid reset token | `400` | Invalid reset token |
| Expired reset token | `410` | Reset token has expired |
| Article not found | `404` | Article not found |
| Not the author (article) | `403` | You can only update/delete your own articles |
| Comment not found | `404` | Comment not found |
| Not comment owner | `403` | You can only delete your own comments |
| Max reply depth | `400` | Maximum reply depth of 3 reached |
| Parent comment wrong article | `400` | Parent comment does not belong to this article |
| Article already bookmarked | `409` | Article is already bookmarked |
| Bookmark not found | `404` | Bookmark not found |
| Script not found | `404` | Script not found |
| Not the author (script) | `403` | You can only update/delete your own scripts |
| Missing JWT token | `401` | Unauthorized |
| Validation failure | `400` | Field-level error messages |

---

## SMTP Setup (Gmail)

In `application.properties`, replace the placeholders:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
```

To generate a Gmail App Password:
1. Go to [Google Account → Security](https://myaccount.google.com/security)
2. Enable **2-Step Verification**
3. Go to **App Passwords** → Generate one for "Mail"

---

## Security Summary

- Passwords hashed with **BCrypt**
- JWT tokens expire after **24 hours**
- Verification tokens expire after **24 hours** (single-use)
- Reset tokens expire after **15 minutes** (single-use)
- Forgot-password never reveals whether an email exists
- Only article authors can update/delete their articles
- Only comment owners or ADMINs can delete comments
- Reply depth limited to **3 levels**
- Users can only manage their own bookmarks
- `/auth/**` is public, `/api/**` requires JWT

