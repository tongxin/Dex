#ifndef _BACKEND_H
#define _BACKEND_H

#include <stdlib.h>
#include "postgres.h"
#include "fmgr.h"
#include "storage/backendid.h"
#include "storage/proc.h"

#include "globals.h"

int
resetBackendInfo(string host, int port);

string
getBackendDriverIP() ;

int
getBackendDriverPort() ;

#endif

