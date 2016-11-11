#include "backend.h"

int
resetBackendInfo(string host, int port) {
	MyBackendDriverIP = host;
	MyBackendDriverPort = port;
	return 0;
}

string
getBackendDriverIP() {
	return MyBackendDriverIP;
}

int
getBackendDriverPort() {
	return MyBackendDriverPort;
}
