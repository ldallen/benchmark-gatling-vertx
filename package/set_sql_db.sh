#!/bin/sh
  
Q1="create database if not exists vertxBase;"
Q2="use vertxBase;"
Q3="drop table todos;"
Q4="create table todos (id int Primary Key auto_increment, action varchar(200),done boolean, INDEX(action));"
Q5=""

ACTIONS="(\"newtodo\",false) (\"test\",true) (\"code\",false) (\"new\",false) (\"work\",false) (\"buyasandwich\",true) (\"drinkcoffee\",true) (\"sleepabit\",false)"

for A in $ACTIONS ; do
	Q5="${Q5}insert into todos (action,done) values $A;"
done


SQL="${Q1}${Q2}${Q3}${Q4}${Q5}"

mysql -uroot -proot -e "$SQL"