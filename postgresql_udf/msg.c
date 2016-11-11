#include "msg.h"

//copy the message
void
msg_cpy (char* buf, char* str, int length) {
    memcpy(buf, str, length);
}

//add the end flat for the request
void
add_end_msg(string buf) {
    cstring s = "\t\n";
    memcpy(buf+strlen(buf), s, strlen(s));
}

//expand the buffer
int
buf_expand(string buf , int times ) {
    if  ( (buf = (string ) malloc (MAX_LEN * times)) == NULL ) {
        return -1;
    } else {
        memset(buf, 0 , MAX_LEN * times);
        return 0;
    }
}

//find out the end of the message
//"\t\n" is  the end flag
int
find_end_msg(string buf, int start, int finish) {
    int i  = start;
    for (; i < finish -1; i ++ ) {
        if (buf[i] == '\t' && buf[i+1] == '\n') {
            break;
        }
    }
    if (i == finish - 1)
        return -1;
    else
        return i  + 2;
}

//receive the message
string
recv_msg(int s) {
    string recv_buf = (string) malloc ( sizeof (char)  * MAX_LEN);
    memset(recv_buf, 0, MAX_LEN);
    int a = 0;
    int count = 1;
    recv_buf[0] = '\0';
    while(1) {  //半包
        int length = strlen (recv_buf);
        if ( (a = read(s, recv_buf + length, MAX_LEN * count - length - 1) ) < 0) {
            elog(ERROR, "read error!");
            exit(1);
        }
        recv_buf[length + a] = '\0';
        int end;
        char tmp[MAX_LEN];
        memset(tmp, 0, MAX_LEN);
       // 黏包
        if ( (end =  find_end_msg(recv_buf , length, length + a) )  !=  -1) {
            if (end < strlen(recv_buf) ) {
                msg_cpy(tmp, recv_buf + end, strlen(recv_buf));
                memset(recv_buf+end, 0, strlen(recv_buf) - end + 1);

                //handle one message
                ereport(INFO, (errmsg("%s", recv_buf)));

                memset(recv_buf,  0 ,  MAX_LEN * count);
                msg_cpy(recv_buf, tmp, strlen(tmp));
            } else {
                //handle one message
                ereport(INFO, (errmsg("%s", recv_buf)));
                break;
            }
        }
    // if  the size  of recv_buf is too small to store the response ,
    //expand the size
        if (strlen(recv_buf) == MAX_LEN*count  - 1) {
             count ++;
            if ( buf_expand(recv_buf, count) < 0) {
                elog (ERROR, " The size of the recieve message is too large !");
                exit(1);
            }
        }
   }
           return recv_buf;
}

//send message to server
int
send_msg(int s, string buf) {
    if (strlen(buf) != write(s,buf,strlen(buf))) {
        return -1;
    } else {
        return 0;
    }
}

//CLIENT(C) send message
int
send_msg_tcp( conn_addr* dexServer, char* buf, char* out) {
    int s;
    struct sockaddr_in client,server;
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
    send_msg(s, buf);

    //获取服务器的返回信息
    string recv_buf =  recv_msg(s);
    memcpy(out, recv_buf, strlen(recv_buf));

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

    if ((recvbytes = recvfrom(sockListen, recvbuf, 1024, 0, (struct sockaddr*)&recvAddr, &addrLen))!=-1) {
        recvbuf[recvbytes] = '\0';
        ereport(INFO, (errmsg("receive a broadCast message:%s\n", recvbuf)));
    } else {
        elog(ERROR, "recvfrom fail\n");
    }

    close(sockListen);
    return 0;
}

