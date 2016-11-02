#TARGET=fun.so
#OBJ=fun.o cJSON.o

fun.so : fun.o cJSON.o
	gcc -shared -o fun.so fun.o cJSON.o

cJSON.o : cJSON.c cJSON.h
	gcc -I /opt/postgresql-9.4/include/server -fpic -c cJSON.c -o cJSON.o

fun.o : fun.c
	gcc -I /opt/postgresql-9.4/include/server -fpic -c fun.c -o fun.o

clean:
	rm -f cJSON.o fun.o fun.so
