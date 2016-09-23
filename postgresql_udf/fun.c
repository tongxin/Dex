#include "postgres.h"
#include "fmgr.h"
#include "utils/geo_decls.h"
#include "utils/array.h"
#include "catalog/pg_type.h"
#include "utils/builtins.h"
#include "cJSON.h"

#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>

#define SER_PORT 8888
#define MAX_LEN 1024

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC; 
#endif

//CLIENT(C) send message
char*
sendMsg(cJSON *out) {
	int s; 
	struct sockaddr_in client,server;
	char recv_buf[MAX_LEN];
	//socket
	if ((s = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		elog(ERROR,"clietn: create socket error");  
		exit(1); 
	}
	
	//设置服务器地址结构
	bzero(&server, sizeof(server)); 
	server.sin_family = AF_INET; 
	server.sin_port = htons(SER_PORT);
	server.sin_addr.s_addr = inet_addr("172.20.110.181"); 
	
	//连接服务器
	if (connect(s, (struct sockaddr *)&server, sizeof(server)) < 0 ) {
		elog(ERROR, "connect error!");
		close(s); 
		exit(1); 
	}

	//向服务器发送信息
	if (strlen(out) != write(s,out,strlen(out))) {
		elog(ERROR, "write error!");
		exit(1); 
	}

	
	//获取服务器的返回信息
	memset(recv_buf, 0, MAX_LEN); 
	if (read(s, recv_buf, sizeof(recv_buf)) < 0) {
		elog(ERROR, "read error!");
		exit(1); 
	}
	
	close(s);
	return recv_buf; 
}

PG_FUNCTION_INFO_V1(spark_PI); 

Datum
spark_PI(PG_FUNCTION_ARGS)
{
	if (PG_NARGS() > 1) {
		elog(ERROR, "Error parameters!"); 
		exit(1); 	
	} else {
		char* out; 
		cJSON *root, *params; 

		//获取返回pi的精度，默认为７
		int precision = 0; 
		if (PG_NARGS() == 0) {
			precision = 7; 		
		} else {
			precision = PG_GETARG_INT32(0); 		
		}
		
		//将模型信息封闭为json格式
		root = cJSON_CreateObject(); 
		cJSON_AddStringToObject(root, "ModelName", "spark_PI");
		cJSON_AddItemToObject(root, "Parameters", params=cJSON_CreateObject());  
		cJSON_AddNumberToObject(params, "Precision", precision); 
		out =(char*) cJSON_Print(root);  
		cJSON_Delete(root); 
		memcpy(out+strlen(out), "\t\n", 3); //添加结束符

		char *str =sendMsg(out); //发送信息并获取返回数据
		//ereport(INFO,(errmsg("%s", str)));  

		PG_RETURN_CSTRING(str); 		
	}
}

//通用的spark调用UDF
//参数为（text, TextArray, TextArray）
//第一参数为模型名称
//第二参数为参数名称,目前设置为文本格式
//第三参数为参数值
PG_FUNCTION_INFO_V1(spark_compute); 

Datum
spark_compute(PG_FUNCTION_ARGS) {
	Datum *elemskey;
	Datum *elemsval; 
	int i; 
	int nelemskey;
	int nelemsval; 
	
	ArrayType *modelParamskey, *modelParamsval;

	char* modelName =text_to_cstring(PG_GETARG_TEXT_P(0));
	ereport(INFO, (errmsg("%s", modelName))); 
	modelParamskey = PG_GETARG_ARRAYTYPE_P(1); 
	modelParamsval = PG_GETARG_ARRAYTYPE_P(2);
	if( ARR_NDIM(modelParamskey)!= 1 
			|| ARR_HASNULL(modelParamskey) 
			|| ARR_ELEMTYPE(modelParamskey)!= TEXTOID)
		elog(ERROR, "expected 1-D text array"); 
	if( ARR_NDIM(modelParamsval)!= 1 
			|| ARR_HASNULL(modelParamsval) 
			|| ARR_ELEMTYPE(modelParamsval)!= TEXTOID)
		elog(ERROR, "expected 1-D text array");

	deconstruct_array(modelParamskey, TEXTOID, -1, false, 'i', &elemskey, NULL, &nelemskey);
	deconstruct_array(modelParamsval, TEXTOID, -1, false, 'i', &elemsval, NULL, &nelemsval);

	assert(nelemskey == nelemsval);
	
	cJSON *root, *params; 
	char *out; 
	root = cJSON_CreateObject(); 
	cJSON_AddStringToObject(root, "ModelName", modelName); 
	cJSON_AddItemToObject(root, "Parameters",params=cJSON_CreateObject()); 
	
	for ( i = 0; i < nelemskey; i++) {
		cJSON_AddStringToObject(params, 
							TextDatumGetCString(elemskey[i]),
							TextDatumGetCString(elemsval[i]));
	}

	out =(char*) cJSON_Print(root);  
	cJSON_Delete(root); 
	memcpy(out+strlen(out), "\t\n", 3); //添加结束符
	ereport(INFO, (errmsg("%s", modelName)));
	ereport(INFO, (errmsg("%s", out)));

	char *str =sendMsg(out); //发送信息并获取返回数据
	ereport(INFO,(errmsg("%s", str)));  	
	
	pfree(elemskey);
	pfree(elemsval); 
	
	PG_RETURN_VOID(); 
}
