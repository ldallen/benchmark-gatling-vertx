#!/bin/sh
./set_sql_db.sh
if [ "vertx" = "$1" ]; then
	if [ $2 ]; then
		echo "starting $2 vertx servers"
		java -jar -Xmx2048M -Xms2048M app/VertxRest.jar -conf conf/vertx-conf.json -instances $2
	else
		echo "starting vertx server"
		java -jar -Xmx2048M -Xms2048M app/VertxRest.jar -conf conf/vertx-conf.json
	fi
elif [ "nubes" = "$1" ];then
	if [ $2 ]; then
		echo "starting $2 nubes servers"
		java -jar -Xmx2048M -Xms2048M app/NubesRest.jar -conf conf/conf_sql.json -instances $2
	else
		echo "starting nubes server"
		java -jar -Xmx2048M -Xms2048M app/NubesRest.jar -conf conf/conf_sql.json
	fi
elif [ "nubesdebug" = "$1" ]; then
	if [ $2 ]; then
		echo "starting $2 nubes servers -- debug mode"
		java -jar -Xmx2048M -Xms2048M app/NubesDebug.jar -conf conf/conf_sql.json -instances $2
	else
		echo "starting nubes server -- debug mode"
		java -jar -Xmx2048M -Xms2048M app/NubesDebug.jar -conf conf/conf_sql.json
	fi
elif [ "nubesMongo" = "$1" ]; then
	if [ $2 ]; then
		echo "starting $2 nubes servers -- mongo mode"
		./set_mongodb.sh
		java -jar -Xmx2048M -Xms2048M app/NubesRestMongo.jar -conf conf/conf_mongo.json -instances $2
	else
		echo "starting nubes server -- mongo mode"
		./set_mongodb.sh
		java -jar -Xmx2048M -Xms2048M app/NubesRestMongo.jar -conf conf/conf_mongo.json
	fi
else
	echo "wrong parameter:\ncommande must be ./start_server.sh vertx \nor ./start_server.sh nubes"
fi