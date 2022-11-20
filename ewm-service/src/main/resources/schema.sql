CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name  VARCHAR(255)                            NOT NULL UNIQUE,
    email VARCHAR(255)                            NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    name  VARCHAR(255)                            NOT NULL UNIQUE
);