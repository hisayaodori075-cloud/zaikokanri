DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE authorities (
    username VARCHAR(50),
    authority VARCHAR(50),
    CONSTRAINT fk_authorities_users
        FOREIGN KEY(username) REFERENCES users(username)
);