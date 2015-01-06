webshop-javaee
==============

Project in ID2212 Network Programming with Java,

Uses JSF, EJB and JPA.
Running on Glassfish and uses Derby(MySql) as DB
Using JTA with resource and connection pool made in Admin webconsole. 

DB scripts

CREATE TABLE Items (
    ItemID INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
    ItemName varchar(255) not null, 
    ItemStock INT not null, 
    ItemDescription varchar(255) not null, 
    ItemPrice INT not null); 

CREATE TABLE Users (
    UserID INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
    UserName varchar(255) not null, 
    Password varchar(255) not null, 
    LastName varchar(255) not null, 
    FirstName varchar(255) not null, 
    Mail varchar(255), 
    IsAdmin boolean, 
    IsBanned boolean);

INSERT INTO Items (ItemName, ItemStock, ItemDescription, ItemPrice) 
    VALUES('Red gnome', 28, 'Smiling gnome.', 75);
INSERT INTO Items (ItemName, ItemStock, ItemDescription, ItemPrice) 
    VALUES('Green gnome', 41, 'Laughing gnome.', 82);
INSERT INTO Items (ItemName, ItemStock, ItemDescription, ItemPrice) 
    VALUES('Blue gnome', 37, 'Angry gnome.', 60);

INSERT INTO Users (UserName, Password, LastName, FirstName, Mail, IsAdmin, IsBanned) 
    VALUES('hedvig', 'hedvig', 'Halv', 'Hedvig', 'hedvig@mail.com', 'true', 'false');
INSERT INTO Users (UserName, Password, LastName, FirstName, Mail, IsAdmin, IsBanned) 
    VALUES('kalle', 'kalle', 'Kula', 'Kalle', 'kalle@mail.com', 'false', 'false');
INSERT INTO Users (UserName, Password, LastName, FirstName, Mail, IsAdmin, IsBanned) 
    VALUES('anna', 'anna', 'Aktsam', 'Anna', 'anna@mail.com', 'false', 'true');
