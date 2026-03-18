-- ============================================================
-- MaximoBlog – PostgreSQL Schema
-- ============================================================

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(30)  NOT NULL DEFAULT 'USER',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON users (email);

-- 2. Articles
CREATE TABLE IF NOT EXISTS articles (
    id          BIGSERIAL    PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL,
    category    VARCHAR(100),
    author_id   BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_article_category   ON articles (category);
CREATE INDEX IF NOT EXISTS idx_article_author      ON articles (author_id);
CREATE INDEX IF NOT EXISTS idx_article_created_at  ON articles (created_at);

-- 3. Comments
CREATE TABLE IF NOT EXISTS comments (
    id          BIGSERIAL PRIMARY KEY,
    content     TEXT      NOT NULL,
    user_id     BIGINT    NOT NULL REFERENCES users (id)    ON DELETE CASCADE,
    article_id  BIGINT    NOT NULL REFERENCES articles (id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_comment_article ON comments (article_id);
CREATE INDEX IF NOT EXISTS idx_comment_user    ON comments (user_id);

-- 4. Scripts
CREATE TABLE IF NOT EXISTS scripts (
    id          BIGSERIAL    PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    code        TEXT         NOT NULL,
    description TEXT,
    category    VARCHAR(100),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_script_category ON scripts (category);

-- 5. Bookmarks
CREATE TABLE IF NOT EXISTS bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL REFERENCES users (id)    ON DELETE CASCADE,
    article_id  BIGINT    NOT NULL REFERENCES articles (id) ON DELETE CASCADE,
    CONSTRAINT  uk_bookmark_user_article UNIQUE (user_id, article_id)
);

CREATE INDEX IF NOT EXISTS idx_bookmark_user    ON bookmarks (user_id);
CREATE INDEX IF NOT EXISTS idx_bookmark_article ON bookmarks (article_id);
