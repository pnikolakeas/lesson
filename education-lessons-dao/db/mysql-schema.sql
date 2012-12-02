# create user 'admin'@'localhost' identified by '1234';
# grant all on lessons.* to 'admin'@'localhost';

--drop database if exists lessons;
--create database lessons;
use lessons;

insert into lesson_composite(title, type) values ('Root', 'ROOT');

insert into Users (nickname, password, fullname, email, admin) values ('nikolakp', '111', 'Panos Nikolakeas', 'nikolakp@gmail.com', true);

insert into OpenIDs(address, user_id) values ('https://www.google.com/accounts/o8/id?id=AItOawm6BOQiLC8UAl6pgWiqMMg5XkKgZzWuQtA', 1);