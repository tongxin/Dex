#include "backend.h"

int
resetBackendInfo(string host, int port) {
	MyBackendDriverIP = host; 
	MyBackendDriverPort = port;  
}

getBackendDriverIP() {
	return MyBackendDriverIP; 
}

int
getBackendDriverPort() {
	return MyBackendDriverPort; 
}
