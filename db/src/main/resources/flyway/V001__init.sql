-- noinspection SqlDialectInspectionForFile

CREATE TABLE users
(
    id       UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE
);

INSERT INTO users (id, username)
VALUES ('f0abd1a5-d9b9-4b15-bc35-41138dfb781d', 'admin');

CREATE TABLE passwords
(
    user_id UUID  NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (id),
    salt    BYTEA NOT NULL,
    hash    BYTEA NOT NULL
);

INSERT INTO passwords (user_id, salt, hash)
VALUES ('f0abd1a5-d9b9-4b15-bc35-41138dfb781d',
        '\x7b23b5d86bb4d3d96b3385121658c617f93f8f150f2d65c67ba20d0e7e2569643ee39724abb3325d630d9d268e069ec8003b24f9c3e180a421dc094b41dc9903'::BYTEA,
        '\x813aaa844b1ed9987434f03e782bd5a5884a05e220a128aa055f2f4ff445bceb'::BYTEA);
