create database sacstatehornet;
use sacstatehornet; 

create table students (
id int auto_increment primary key,
name varchar(50) not null,
major varchar(50),
account_created timestamp default current_timestamp
)