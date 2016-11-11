#ifndef _MSG_H
#define _MSG_H

#include "postgres.h"
#include "fmgr.h"
#include "utils/geo_decls.h"
#include "utils/array.h"
#include "catalog/pg_type.h"
#include "utils/builtins.h"

#include <stdio.h>
#include <assert.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <netdb.h>

#include "globals.h"

#define MAX_LEN  1024


//CLIENT(C) send message

//copy string from str to buf
void
msg_cpy (char* buf, char* str, int length);

//add the end flat for the request
void
add_end_msg(string buf) ;

//send request to dexServer through tcp
int
send_msg_tcp( conn_addr* dexServer, char* buf, char* out) ;

//recieve information from dexServer through udp
int
recv_msg_udp(char* buf, int port);




#endif
