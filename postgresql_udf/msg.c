#include "msg.h"

void 
msg_cpy (char* buf, char* str, int length) {
	memcpy(buf, str, length);
}

void
add_end_msg(char* buf) {
	cstring s = "\t\n"; 
	memcpy(buf+strlen(buf), s, strlen(s));
}

//CLIENT(C) send message
int
send_msg_tcp( conn_addr* dexServer, char* buf, char* out) {
	int s; 
	struct sockaddr_in client,server;
	char recv_buf[MAX_LEN];
	//socket
	if ((s = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		elog(ERROR,"client: create socket error");  
		exit(1); 
	}
	
	//设置服务器地址结构
	bzero(&server, sizeof(server)); 
	server.sin_family = AF_INET; 
	server.sin_port = htons(dexServer->port);
	server.sin_addr.s_addr = inet_addr(dexServer->ip); 
	
	//连接服务器
	if (connect(s, (struct sockaddr *)&server, sizeof(server)) < 0 ) {
		elog(ERROR, "connect error!");
		close(s); 
		exit(1); 
	}

	//向服务器发送信息
	if (strlen(buf) != write(s,buf,strlen(buf))) {
		elog(ERROR, "write error!");
		exit(1); 
	}

	
	//获取服务器的返回信息
	memset(recv_buf, 0, MAX_LEN); 
	if (read(s, recv_buf, sizeof(recv_buf)) < 0) {
		elog(ERROR, "read error!");
		exit(1); 
	}

	msg_cpy(out, recv_buf, strlen(recv_buf));
	
	close(s);
	return 0; 
}

int 
recv_msg_udp(char* buf, int port) {
	int sockListen;
	if ((sockListen = socket(AF_INET, SOCK_DGRAM, 0)) == -1) {
		elog(ERROR, "socket fail"); 
		return -1; 
	}
	int set = 1; 

	setsockopt(sockListen, SOL_SOCKET, SO_REUSEADDR, &set, sizeof(int)); 
	struct sockaddr_in recvAddr; 
	memset(&recvAddr, 0, sizeof(struct sockaddr_in)); 
	recvAddr.sin_family = AF_INET; 
	recvAddr.sin_port = htons(port); 
	recvAddr.sin_addr.s_addr = INADDR_ANY;
	//绑定监听端口号
	if (bind(sockListen, (struct sockaddr *)&recvAddr, sizeof(struct sockaddr)) == -1) {
		elog(ERROR, "bind fail"); 
		return -1; 
	}	

	int recvbytes; 

	char recvbuf[1024]; 
	int addrLen = sizeof(struct sockaddr_in); 
	if ((recvbytes = recvfrom(sockListen, recvbuf, 1024, 0, (struct sockaddr*)&recvAddr, &addrLen))!=-1) {
		recvbuf[recvbytes] = '\0'; 
		ereport(INFO, (errmsg("receive a broadCast message:%s\n", recvbuf))); 
	} else {
		elog(ERROR, "recvfrom fail\n");	
	}

	close(sockListen); 
	return 0; 
}

