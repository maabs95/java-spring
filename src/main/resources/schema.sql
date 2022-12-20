CREATE TABLE IF NOT EXISTS usertable (
    username varchar(255) PRIMARY KEY,
    password varchar(255) not null,
    email varchar(255) not null,
    firstname varchar(255) not null,
    lastname varchar(255) not null,
    role varchar(15) not null
);