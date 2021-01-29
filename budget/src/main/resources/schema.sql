DROP TABLE IF EXISTS BudgetMaster;
CREATE TABLE BudgetMaster (
  ID int(11) NOT NULL AUTO_INCREMENT,
  NAME varchar(50) NOT NULL,
  DESCRIPTION varchar(255),
  LAST_USED datetime NOT NULL,
  IS_TEMPLATE int(1) NOT NULL DEFAULT 0,
  DISABLED int(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (ID)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

insert into BudgetMaster (NAME,LAST_USED,IS_TEMPLATE,DESCRIPTION) values
	('Default Template',CURRENT_TIMESTAMP,1,'Desc 1'),
	('Vacation',CURRENT_TIMESTAMP,1,'Desc 2'),
	('September 2020',CURRENT_TIMESTAMP,0,'Desc 3'),
	('October 2020',CURRENT_TIMESTAMP,0,'Desc 4'),
	('November 2020',CURRENT_TIMESTAMP,0,'Desc 5');

DROP TABLE IF EXISTS Item;
CREATE TABLE Item (
  ID int(11) NOT NULL AUTO_INCREMENT,
  BUDGET_ID int(11) NOT NULL,
  NAME varchar(50) NOT NULL,
  AMOUNT DECIMAL(13,2) NOT NULL,
  MAX_DENOM int(11) NOT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT,MAX_DENOM) values
	(1,'Groceries','500',100),
	(1,'Clothing','50',100),
	(1,'Restaurants','200',50),
	(1,'Splurge','50',100),
	(1,'Home Improvement','300',100),
	(1,'Personal Care','100',100),
	(1,'Baby Supplies','100',100),
	(1,'Entertainment','50',100),
	(1,'Gas','100',100),
	(5,'Groceries','433',100),
	(5,'Clothing','29',100),
	(5,'Restaurants','166',50),
	(5,'Splurge','50',100),
	(5,'Home Improvement','242',100),
	(5,'Personal Care','100',100),
	(5,'Baby Supplies','75',100),
	(5,'Entertainment','44',100),
	(5,'Gas','100',100);
	

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT,MAX_DENOM) select 3,NAME,(AMOUNT),MAX_DENOM from Item where BUDGET_ID = 1;
INSERT INTO Item (BUDGET_ID,NAME,AMOUNT,MAX_DENOM) select 4,NAME,(AMOUNT - 3),MAX_DENOM from Item where BUDGET_ID = 5;

INSERT INTO Item (BUDGET_ID,NAME,AMOUNT,MAX_DENOM) values
	(2,'Groceries','300',100),
	(2,'Clothing','100',100),
	(2,'Restaurants','300',50),
	(2,'Splurge','100',100),
	(2,'Personal Care','100',100),
	(2,'Entertainment','100',100),
	(2,'Gas','200',100);

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