#!/bin/sh
  
Q0="db.todo.remove({});db.repairDatabase();"
Q1=""
Q2="db.todo.createIndex( { action: \"text\" } );"

ACTIONS="\"newtodo\" \"test\" \"code\" \"new\" \"work\" \"buyasandwich\" \"drinkcoffee\" \"sleepabit\""

for A in $ACTIONS ; do
	Q1="${Q1}db.todo.insert({action:$A,done:false});"
done


QUERY="${Q0}${Q1}${Q2}"

mongo vertxBase --eval "$QUERY"