#include "postgres.h"
#include "fmgr.h"
#include "utils/geo_decls.h"

#include "dex.h"


//convert message structure to Json
int
Dex_connMessageToJSON (connMsg* msg, string out) {
	string json_out;
	dexJson *root;
//construct message json
	root = dexJson_createObject();
	dexJson_addStringtoObject(root, "name", msg->name );
	dexJson_addStringtoObject(root, "spark_home", msg->spark_home );
	dexJson_addStringtoObject(root, "main_class", msg->main_class );
	dexJson_addStringtoObject(root, "master", msg->master );
	dexJson_addStringtoObject(root, "deploy_mode", msg->deploy_mode );
	dexJson_addStringtoObject(root, "app_resource", msg->app_resource );

	//using string to store json
	json_out = (string) dexJson_Print(root);

	msg_cpy(out, json_out, strlen(json_out));

	return 0;
}

// parse the response of connection
conn_addr
Dex_parseConnReponse (string rep) {
	conn_addr driverServer;
	dexJson *recvJSON, *item ;

	recvJSON = dexJson_Parse(rep);

	item = dexJson_GetObjectItem(recvJSON,"host");
	ereport(INFO,(errmsg("%s",item-> valuestring)));
	memset(driverServer.ip, 0 , 512);
    msg_cpy(driverServer.ip , item->valuestring, strlen(item->valuestring) );

	item = dexJson_GetObjectItem(recvJSON,"port");
	driverServer.port = item->valueint;

	return driverServer;
}

// connect PG with spark
int
Init_DexConnection(conn_addr* dexServer, connMsg* msg) {
	char out[8192] = {0};

	Dex_connMessageToJSON(msg, out); // convert message sructure to Json

	add_end_msg(out);  //add the end flag

	//string rep = (string) palloc(1024);
    //the request for initilizing DexConnection
	char rep[8192]={0};
	ereport(INFO, (errmsg("Init_DexConnection:\n%s", out)));

	send_msg_tcp(dexServer, out, rep); // send request to server

	conn_addr driverServer;
	driverServer= Dex_parseConnReponse (rep); //get information from the response

    //reset the ip and port of driverServer
	resetBackendInfo(driverServer.ip, driverServer.port);
	ereport(INFO,(errmsg("%s",driverServer.ip)));
	ereport(INFO,(errmsg("%d",driverServer.port)));
	return 0;
}

void
optionstoJson(dexJson* option, Options options) {
	int optionid;
	for (optionid = 0; optionid < sizeof(options)/sizeof(strmap); optionid++) {
		dexJson_addStringtoObject(option, options[optionid].key, options[optionid].value);
	}
}

//convert dexdataSetJDBCOption to json request
int
Dex_DataSetJDBCOptiontoJson(OptionName name, dexDataSetJDBCOption options, string out) {
	string json_out;
	dexJson *root, *message, *option;

	root = dexJson_createObject();
	dexJson_addStringtoObject(root, "optionName", name );
	dexJson_addItemtoObject(root, "options", option=dexJson_createObject());

	//convert the options to json
	optionstoJson(option, options);

	//using string to store json
	json_out = (string) dexJson_Print(root);

	msg_cpy(out, json_out, strlen(json_out));

	return 0;
}


int
Dex_createDataSetbyJDBC(dexDataSetJDBCOption options, conn_addr* dexServer) {
	char out[2048]={0};
	OptionName name = "newDataset";
	Dex_DataSetJDBCOptiontoJson(name, options, out);

	add_end_msg(out);

	string rep = (string) palloc(1024); // a string to identify the dataset

	ereport(INFO, (errmsg("%s", out)));

	send_msg_tcp(dexServer, out, rep); // send request to server

	return 0;
}

// convert the command to json request for option between two datasets
int
Dex_DataSet_command_toJson(OptionName name, dataset d1, dataset d2, Options options, string out) {
	string json_out;
	dexJson *root, *message, *option;

	root = dexJson_createObject();
	dexJson_addStringtoObject(root, "OptionName", name);
	dexJson_addStringtoObject(root, "dataset1", d1);
	dexJson_addStringtoObject(root, "dataset2", d2);
	if (option != NULL) {
		dexJson_addItemtoObject(root, "option", option=dexJson_createObject());
		optionstoJson(option, options);
	}

	json_out = (string) dexJson_Print(root);

	msg_cpy(out, json_out, strlen(json_out));

	return 0;
}

int
Dex_DataSet_Join_toJson(dataset d1, dataset d2, dexDataSetjoinConditions conditions, string out) {
	Dex_DataSet_command_toJson("Join", d1, d2, conditions, out);
}

int
Dex_DataSet_Join(dataset d1, dataset d2, dexDataSetjoinConditions conditions, conn_addr* dexServer) {
	char out[2048]={0};

	Dex_DataSet_Join_toJson(d1, d2, conditions, out);
	add_end_msg(out);

	string rep = (string) palloc(1024); // a string to identify the dataset

	ereport(INFO, (errmsg("%s", out)));

	send_msg_tcp(dexServer, out, rep); // send request to server

	return 0;
}

int
Dex_DataSet_Union_toJson(dataset d1, dataset d2, string out) {
	Dex_DataSet_command_toJson("Union", d1, d2, NULL, out);
}

int
Dex_DataSet_Union(dataset d1, dataset d2, conn_addr* dexServer) {
	char out[2048]={0};

	Dex_DataSet_Union_toJson(d1, d2, out);
	add_end_msg(out);

	string rep = (string) palloc(1024); // a string to identify the dataset

	ereport(INFO, (errmsg("%s", out)));

	send_msg_tcp(dexServer, out, rep); // send request to server

	return 0;
}

int
close_DexConnection(conn_addr* dexServer, connMsg* msg) {
	char out[1024];

	Dex_connMessageToJSON(msg, out); // convert message sructure to Json

	add_end_msg(out);  //add the end flag

	string rep = (string) palloc(1024);

	ereport(INFO, (errmsg("%s", out)));

	send_msg_tcp(dexServer, out, rep); // send request to server

	return 0;
}











