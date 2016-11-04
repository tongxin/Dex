#include "dexJson.h"

dexJson*
dexJson_createObject(void) {
	return cJSON_CreateObject(); 
}

void 
dexJson_addInttoObject(dexJson* root, const char* name, int n) {
	cJSON_AddNumberToObject(root, name, n); 
}

void
dexJson_addItemtoObject(dexJson* root, const char* name, dexJson* item) {
	cJSON_AddItemToObject(root, name, item); 
}

void
dexJson_addStringtoObject(dexJson* root, const char* name, const char* s) {
	cJSON_AddStringToObject(root, name, s);
}

char* 
dexJson_Print (dexJson* root) {
	return cJSON_Print(root);
}

dexJson* 
dexJson_GetObjectItem(cJSON *object,const char *string) {
	return cJSON_GetObjectItem(object,string);
}

dexJson* 
dexJson_Parse(const char *value){
	return cJSON_Parse(value);
}




