#ifndef _DEX_H
#define _DEX_H

#include "dexJson.h"
#include "msg.h"
#include "globals.h"
#include "backend.h"

#define commandTye 0
#define dataType 1

#define APPLICATION_START 1
#define APPLICATION_ERROR -1


struct commandMessage{
	int type;
	int length;
	cJSON* message;
};

struct dataMessage{
	string key;
	string value;
};

typedef struct strMap{
	string key;
	string value;
} strmap;

//structure information for building the connection
typedef struct connectionMessage {
	string name;
	string spark_home;
	string main_class;
	string master;
	string deploy_mode;
	string app_resource;
} connMsg;

//when connection is built, Dex respond to the PG
typedef struct responseMessage {
	int satus;
	conn_addr driverServer;
}repMsg;


typedef struct SparkSession {
	string master;
	string appName;
	strmap* config;
}sparkSession;

typedef struct DexSession {
	string name;
	sparkSession ss;
} dexSession;

typedef char* dataset;
typedef char* OptionName;

typedef strmap* Options;
typedef Options dexDataSetJDBCOption;
typedef Options dexDataSetjoinConditions;


int
Init_DexConnection(conn_addr* dexServer, connMsg* msg);

int
Dex_connMessageToJSON (connMsg* msg, string out);

int
Dex_createDataSetbyJDBC(dexDataSetJDBCOption options, conn_addr* dexServer);

int
Dex_DataSet_Join(dataset d1, dataset d2, dexDataSetjoinConditions conditions, conn_addr* dexServer);

int
Dex_DataSet_Union(dataset d1, dataset d2, conn_addr* dexServer);


#endif
