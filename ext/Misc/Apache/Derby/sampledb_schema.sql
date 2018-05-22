--
-- create the table and indexes for EMPLOYEES
--
CREATE TABLE employees (
id varchar(255) NOT NULL PRIMARY KEY,
fname varchar(255) NOT NULL,
lname varchar(255) NOT NULL,
fullname varchar(255),
email varchar(255),
title varchar(255),
telephone varchar(255),
manager varchar(255),
roles varchar(255),
org varchar(255),
location varchar(255),
password varchar(255),
forgotdata varchar(255),
photo blob(1M)
);
CREATE INDEX EI1 ON employees(fname);
CREATE INDEX EI2 ON employees(lname);
CREATE INDEX EI3 ON employees(manager);
CREATE INDEX EI4 ON employees(email);
--
-- create the table and indexes for LOCATIONS
--
CREATE TABLE locations (
id      varchar(255) NOT NULL PRIMARY KEY,
description varchar(255) NOT NULL,
street varchar(255),
city varchar(255),
state varchar(255),
postalCode varchar(255)
);
--
-- create the table and indexes for MEDIA
--
CREATE TABLE media (
uuid varchar(255) primary key not null,
name varchar(255),
type varchar(255),
size int,
modified varchar(255),
context varchar(255),
subject varchar(255),
relationship varchar(255),
digest varchar(255) not null,
data blob(1M) not null
);
CREATE INDEX MI1 ON media(context);
CREATE INDEX MI2 ON media(subject);
CREATE INDEX MI3 ON media(relationship);
