DROP TABLE IF EXISTS BudgetMaster;
CREATE TABLE BudgetMaster (
  ID int(11) NOT NULL AUTO_INCREMENT,
  NAME varchar(50) NOT NULL,
  LAST_USED datetime NOT NULL,
  IS_TEMPLATE int(1) NOT NULL DEFAULT 0,
  DISABLED int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (ID)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

insert into BudgetMaster (NAME,LAST_USED,IS_TEMPLATE) values
	('Default Template',CURRENT_TIMESTAMP,1),
	('Vacation',CURRENT_TIMESTAMP,1),
	('September 2020',CURRENT_TIMESTAMP,0),
	('October 2020',CURRENT_TIMESTAMP,0),
	('November 2020',CURRENT_TIMESTAMP,0);

DROP TABLE IF EXISTS Item;
CREATE TABLE Item (
  ID int(11) NOT NULL AUTO_INCREMENT,
  BUDGET_ID int(11) NOT NULL,
  NAME varchar(50) NOT NULL,
  AMOUNT DECIMAL(13,2) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT) values
	(1,'Groceries','500'),
	(1,'Clothing','50'),
	(1,'Restaurants','200'),
	(1,'Splurge','50'),
	(1,'Home Improvement','300'),
	(1,'Personal Care','100'),
	(1,'Baby Supplies','100'),
	(1,'Entertainment','50'),
	(1,'Gas','100'),
	(5,'Groceries','433'),
	(5,'Clothing','29'),
	(5,'Restaurants','166'),
	(5,'Splurge','50'),
	(5,'Home Improvement','242'),
	(5,'Personal Care','100'),
	(5,'Baby Supplies','75'),
	(5,'Entertainment','44'),
	(5,'Gas','100');
	

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT) select 3,NAME,(AMOUNT) from Item where BUDGET_ID = 1;
INSERT INTO Item (BUDGET_ID,NAME,AMOUNT) select 4,NAME,(AMOUNT - 3) from Item where BUDGET_ID = 5;

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT) values
	(2,'Groceries','300'),
	(2,'Clothing','100'),
	(2,'Restaurants','300'),
	(2,'Splurge','100'),
	(2,'Personal Care','100'),
	(2,'Entertainment','100'),
	(2,'Gas','200');

DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
  USERNAME varchar(50) NOT NULL,
  PASSWORD varchar(100) NOT NULL,
  FIRSTNAME varchar(100) NOT NULL,
  LASTNAME varchar(100) NOT NULL,
  EMAIL varchar(100) NOT NULL,
  ACCOUNT varchar(100) NOT NULL,
  enabled tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Users VALUES ('demo_user','$2a$10$KnRdXb09WIgf1gYwYAj/pO7mB7Rp0i0xejpncp2ZZnlqZW9sj4h/m','Demo','User','demo_user@stephenky.com','999999999',1);

DROP TABLE IF EXISTS Authorities;
CREATE TABLE Authorities (
  USERNAME varchar(50) NOT NULL,
  AUTHORITY varchar(50) NOT NULL,
  UNIQUE KEY ix_auth_username (username,authority),
  CONSTRAINT authorities_ibfk_1 FOREIGN KEY (username) REFERENCES Users (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Authorities VALUES ('demo_user','USER');