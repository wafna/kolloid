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

INSERT INTO users (id, username)
VALUES ('f0abd1a5-d9b9-4b15-bc35-41138dfb781d', 'admin');

INSERT INTO passwords (user_id, salt, hash)
VALUES ('f0abd1a5-d9b9-4b15-bc35-41138dfb781d',
        '\xe5729eef3273370bbe6f11a1e9a968b28b2d43c15431498bc37dbf4b94f974c2aa1472fbddd06165e56e259a32b62aa7a1db6549fcb1ba9bb373ed3869ccfde3'::BYTEA,
        '\x970ab1bdeca7c16ceec7e1c0d2de13b13fd6034e28910210f8e0b6d588e4dd33'::BYTEA);
