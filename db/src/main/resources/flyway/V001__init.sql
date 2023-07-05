-- noinspection SqlDialectInspectionForFile

CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE passwords
(
    user_id UUID  NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    salt    BYTEA NOT NULL,
    hash    BYTEA NOT NULL
);
