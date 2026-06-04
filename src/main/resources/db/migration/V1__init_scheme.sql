CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    avatar     VARCHAR(255),
    state      VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    updated_at TIMESTAMP,
    created_at TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT CHK_user_state CHECK (state IN ('ACTIVE', 'SUSPENDED', 'DELETED'))
);

CREATE TABLE boards
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    background  VARCHAR(255),
    updated_at  TIMESTAMP,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    owner_id    UUID REFERENCES users (id)
);

CREATE TABLE board_members
(
    board_id UUID REFERENCES boards (id),
    user_id  UUID REFERENCES users (id),
    role     VARCHAR(255) NOT NULL,
    CONSTRAINT PK_board_members PRIMARY KEY (board_id, user_id),
    CONSTRAINT CHK_board_members_role CHECK (role IN ('ADMIN', 'MEMBER'))
);

CREATE TABLE columns
(
    id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name     VARCHAR(255) NOT NULL,
    position INTEGER      NOT NULL,
    board_id UUID REFERENCES boards (id) ON DELETE CASCADE
);

CREATE TABLE tasks
(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    priority    VARCHAR(255) NOT NULL,
    due_date    TIMESTAMP,
    column_id   UUID REFERENCES columns (id) ON DELETE CASCADE,
    CONSTRAINT CHK_task_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'))
);

CREATE TABLE task_assignees
(
    task_id UUID REFERENCES tasks (id) ON DELETE CASCADE,
    user_id UUID REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT PK_task_assignee PRIMARY KEY (task_id, user_id)
);

CREATE TABLE sub_tasks
(
    id        UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    title     VARCHAR(255) NOT NULL,
    completed BOOLEAN      NOT NULL DEFAULT FALSE,
    task_id   UUID REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE TABLE comments
(
    id         UUID PRIMARY KEY   DEFAULT uuid_generate_v4(),
    content    TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id    UUID REFERENCES users (id),
    task_id    UUID REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens
(
    id         UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    token      VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id    UUID REFERENCES users (id)
);

CREATE INDEX IDX_refresh_tokens_user_id ON refresh_tokens (user_id);