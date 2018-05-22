--
-- DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
--
--      Portions Copyright 2009 Sun Microsystems, Inc.
--
-- The contents of this file are subject to the terms of the
-- Common Development and Distribution License, Version 1.0 only
-- (the "License").  You may not use this file except in compliance
-- with the License.
--
-- You can obtain a copy of the license at
-- trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
-- or https://openptk.dev.java.net/OpenPTK.LICENSE.
-- See the License for the specific language governing permissions
-- and limitations under the License.
--
-- When distributing Covered Code, include this CDDL HEADER in each
-- file and include the reference to
-- trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
-- add the following below this CDDL HEADER, with the fields enclosed
-- by brackets "[]" replaced with your own identifying information:
--      Portions Copyright [yyyy] [name of copyright owner]
--
--
-- Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
--

-- Example MySQL CREATE scripts.  You may use these scripts to create an employee and location table
-- for testing using the supplied openptk-jdbc.xml configuration

DROP DATABASE IF EXISTS openptk;

CREATE DATABASE IF NOT EXISTS openptk;

GRANT ALL PRIVILEGES ON openptk.* TO 'test' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON openptk.* TO 'test'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON openptk.* TO 'test'@'localhost' IDENTIFIED BY 'password';

USE openptk;

-- DROP TABLE employees;

CREATE TABLE employees (
	id		      varchar(255) NOT NULL PRIMARY KEY,
	fname		   varchar(255) NOT NULL,
	lname		   varchar(255) NOT NULL,
	fullname	   varchar(255),
	email		   varchar(255),
	title		   varchar(255),
	telephone	varchar(255),
	manager		varchar(255),
	roles		   varchar(255),
	org		   varchar(255),
	location	   varchar(255),
	password	   varchar(255),
	forgotdata	varchar(255),
   photo       mediumblob,
	INDEX INDEX1 (fname),
	INDEX INDEX2 (lname),
	INDEX INDEX3 (manager)
);


-- Load example data from the employeedata.csv file.  Also default the following colums
--   password   - Passw0rd
--   roles      - emp
--   fullname   - firstname + space + lastname
--

LOAD DATA LOCAL INFILE 'employeedata.csv' REPLACE INTO TABLE employees
  FIELDS TERMINATED BY ','
  IGNORE 2 LINES
  ( id, org, manager, email, @lname, @fname, telephone, location )
  SET
    password='Passw0rd',
    roles='emp',
    lname=@lname,
    fname=@fname,
    fullname=concat(@fname, ' ', @lname)
  ;

-- DROP TABLE locations;

CREATE TABLE locations (
	id		      varchar(255) NOT NULL PRIMARY KEY,
	description	varchar(255) NOT NULL,
	street		varchar(255),
	city		   varchar(255),
	state		   varchar(255),
	postalCode	varchar(255)
);


-- Load example data from the locationdata.csv file.  

LOAD DATA LOCAL INFILE 'locationdata.csv' REPLACE INTO TABLE locations
  FIELDS TERMINATED BY ','
  IGNORE 2 LINES
  ( id, description, street, city, state, postalCode)
  ;

-- DROP TABLE roles;

CREATE TABLE roles (
   id          varchar(255) NOT NULL PRIMARY KEY,
   description varchar(255) NOT NULL,
   owner       varchar(255),
   access      varchar(255),
   createdate  varchar(255),
   status      varchar(255)
);

-- Load example data from the roledata.csv file.

LOAD DATA LOCAL INFILE 'roledata.csv' REPLACE INTO TABLE roles
  FIELDS TERMINATED BY ','
  IGNORE 2 LINES
  ( id,description,owner,access,createdate,status)
  ;

-- DROP TABLE userrole;

-- example not used due to issue OPENPTK-258
-- CREATE TABLE userrole (
--   id MEDIUMINT NOT NULL AUTO_INCREMENT,
--   userid   varchar(255) NOT NULL,
--   roleid   varchar(255) NOT NULL,
--   PRIMARY KEY (id)
-- );

CREATE TABLE userrole (
   id varchar(255) NOT NULL,
   userid   varchar(255) NOT NULL,
   roleid   varchar(255) NOT NULL,
   PRIMARY KEY (id)
);

-- Load example data from the roledata.csv file.

LOAD DATA LOCAL INFILE 'userroledata.csv' REPLACE INTO TABLE userrole
  FIELDS TERMINATED BY ','
  IGNORE 2 LINES
  (id,userid,roleid)
  ;

-- Create the Media table

CREATE TABLE media (
    uuid            varchar(255) primary key not null,
    name            varchar(255),
    type            varchar(255),
    size            int,
    modified        varchar(255),
    context         varchar(255),
    subject         varchar(255),
    relationship    varchar(255),
    digest          varchar(255) not null,
    data            mediumblob not null,
    INDEX INDEX1 (context),
    INDEX INDEX2 (subject),
    INDEX INDEX3 (relationship)
);

