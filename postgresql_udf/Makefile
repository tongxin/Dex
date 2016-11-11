#TARGET=fun.so
#OBJ=fun.o cJSON.o

fun.so : fun.o cJSON.o dexJson.o dex.o backend.o msg.o
	gcc -shared -o fun.so fun.o cJSON.o dexJson.o dex.o backend.o msg.o

backend.o :backend.c backend.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c backend.c -o backend.o

cJSON.o : cJSON.c cJSON.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c cJSON.c -o cJSON.o

dexJson.o : dexJson.c dexJson.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c dexJson.c -o dexJson.o

msg.o : msg.c msg.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c msg.c -o msg.o

dex.o : dex.c dex.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c dex.c -o dex.o 

fun.o : fun.c
	gcc -I /opt/postgresql-9.4/include/server -fpic -c fun.c -o fun.o

clean:
	rm -f cJSON.o dexJson.o fun.o dex.o msg.o backend.o fun.so
