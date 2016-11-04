#include "postgres.h"
#include "fmgr.h"
#include "utils/geo_decls.h"
#include "utils/array.h"
#include "catalog/pg_type.h"
#include "utils/builtins.h"
#include "dex.h"

#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define HOSTIP "127.0.0.1"
#define HOSTPORT 8080
#define DexServerIP "127.0.0.1"
#define DexServerPort 8888

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC; 
#endif

PG_FUNCTION_INFO_V1(Dex_connect); 

//UDF to build Dex session
Datum
Dex_connect(PG_FUNCTION_ARGS){
	
	//validate the number of the arguments and types of the arguments
	if (PG_NARGS() == 0) {
		conn_addr* dexserver;
		connMsg *msg; 
 		
		msg_cpy(dexserver->ip, "127.0.0.1", strlen(DexServerIP));		
		dexserver->port = DexServerPort; 

		//to construct a message structure
		msg->name = "Pi"; 
		msg->spark_home = "/home/cai/Downloads/spark-2.0.1-bin-hadoop2.4"; 
		msg->main_class = "dex.core.DexServer"; 
		msg->master = "local"; 
		msg->deploy_mode = "client"; 
		msg->app_resource = "/home/cai/Documents/workplace/DexServer.jar"; 

		Init_DexConnection(dexserver, msg); // init the connection with the Dex
		
	} else { 
		ereport(ERROR, (errmsg("to many arguments")));
	}
		
	PG_RETURN_NULL(); 
}


