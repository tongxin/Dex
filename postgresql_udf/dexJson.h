#ifndef _DEXJSON_H
#define _DEXJSON_H

#include "cJSON.h"
#include "globals.h"

typedef cJSON dexJson;

dexJson*
dexJson_createObject(void);

void 
dexJson_addInttoObject(dexJson* root, const char* name, int n); 

void
dexJson_addItemtoObject(dexJson* root, const char* name, dexJson* item)  ; 

void
dexJson_addStringtoObject(dexJson* root, const char* name, const char* s) ;

char* 
dexJson_Print (dexJson* root) ;

dexJson* 
dexJson_GetObjectItem(cJSON *object,const char *string) ;

dexJson* 
dexJson_Parse(const char *value);

#endif
