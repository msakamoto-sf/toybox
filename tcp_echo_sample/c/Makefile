
CC=gcc
OPTS=-Wall
RM=rm

help:
	@echo "target: server01 fork_server01 client01 client02 clean"

server01: server01
	${CC} ${OPTS} -o $@ $@.c

fork_server01: fork_server01
	${CC} ${OPTS} -o $@ $@.c

client01: client01 
	${CC} ${OPTS} -o $@ $@.c

client02: client02 
	${CC} ${OPTS} -o $@ $@.c

all:server01 fork_server01 client01 client02

clean:
	@${RM} server01
	@${RM} fork_server01
	@${RM} client01
	@${RM} client02
