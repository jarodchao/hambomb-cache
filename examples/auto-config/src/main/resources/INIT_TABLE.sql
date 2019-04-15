CREATE TABLE IF NOT EXISTS T_Person (
    Id INT NOT NULL primary key auto_increment,
    Name varchar(8),
    Gender varchar(5),
    Age INT,
    height VARCHAR(10),
    weight DECIMAL(6,2),
    CardId VARCHAR(18),
    Address VARCHAR(60)
);

CREATE TABLE IF NOT EXISTS T_Phone (
    Id INT NOT NULL primary key auto_increment,
    Brand varchar(10),
    model varchar(10),
    memory INT,
    color VARCHAR(10),
    weight INT,
    pattern VARCHAR(5),
    origin VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS T_holiday (
    Id INT NOT NULL primary key auto_increment,
    holiday DATE,
    origin VARCHAR(10)
);