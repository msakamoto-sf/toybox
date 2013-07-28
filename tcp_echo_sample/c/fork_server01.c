/*
 * INET(ipv4) socket, multi-accept-fork, blocking-mode echo server
 * (don't wait child process terminate)
 *
 * compile: $ gcc -Wall -o fork_server01 fork_server01.c
 * invoke : $ ./server01 listening-port-number
 * 
 * (public domain software)
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define REUSE_ADDR 1
// SOMAXCONN comes from sys/socket.h
//#define LISTEN_QUEUE_SIZE SOMAXCONN
#define LISTEN_QUEUE_SIZE 2
#define DATA_BUFSIZE 256

void do_echo(int fd_c) {
    char data_buf[DATA_BUFSIZE];
    int read_len = 0;
    while (1) {
        bzero(data_buf, DATA_BUFSIZE);
        read_len = read(fd_c, data_buf, DATA_BUFSIZE - 1);
        if (0 == read_len) {
            printf("client disconnected.\n");
            break;
        } else if (-1 == read_len) {
            perror("read()");
            exit(-1);
        } else {
            printf("[%d] bytes data read from client, echos [%s].\n", read_len, data_buf);
            read_len = write(fd_c, data_buf, read_len);
            if (-1 == read_len) {
                perror("write()");
                exit(-1);
            }
            printf("[%d] bytes data echoed to client.\n", read_len);
        }
    }
    return;
}

int main(int argc, char *argv[]) {
    int in_port = 0;
    int fd_s    = 0;
    int fd_c    = 0;
    int sopt    = REUSE_ADDR;
    socklen_t sopt_len = sizeof(sopt);

    /* socket address structures references:
     *   struct sockaddr    : netintro(4), sys/socket.h
     *   struct sockaddr_in : inet(4), netinet/in.h
     */
    struct sockaddr_in addr_s;
    struct sockaddr_in addr_c;
    socklen_t addr_in_len = sizeof(struct sockaddr_in);

    int pid = 0;
    char *client_ip = NULL;

    if (2 > argc) {
        printf("usage: %s listening-pot-num\n", argv[0]);
        return 0;
    }
    in_port = atoi(argv[1]);

    /*
     * create socket
     */
    fd_s = socket(AF_INET, SOCK_STREAM, 0);
    if (-1 == fd_s) {
        perror("socket()");
        exit(-1);
    }
    if (-1 == setsockopt(fd_s, SOL_SOCKET, SO_REUSEADDR, &sopt, sopt_len)) {
        perror("setsockopt(SO_REUSEADDR)");
        exit(-1);
    }

    /*
     * bind to inet address
     */
    bzero(&addr_s, sizeof(addr_s));
    addr_s.sin_len = sizeof(addr_s);
    addr_s.sin_family = AF_INET;
    addr_s.sin_port = htons(in_port);
    addr_s.sin_addr.s_addr = INADDR_ANY; // comes from netinet/in.h
    if (-1 == bind(fd_s, (struct sockaddr*)&addr_s, addr_in_len)) {
        perror("bind()");
        exit(-1);
    }

    /*
     * listen
     */
    if (-1 == listen(fd_s, LISTEN_QUEUE_SIZE)) {
        perror("listen()");
        exit(-1);
    }

    while (1) {
        addr_in_len = sizeof(struct sockaddr_in);
        /*
         * accept, blocking mode
         */
        fd_c = accept(fd_s, (struct sockaddr*)&addr_c, &addr_in_len);
        if (-1 == fd_c) {
            perror("accept()");
            exit(-1);
        }
        in_port = ntohs(addr_c.sin_port);
        client_ip = inet_ntoa(addr_c.sin_addr);
        printf("accepted from [%s], port [%d]\n", client_ip, in_port);

        if (0 == (pid = fork())) {
            /* child process */
            do_echo(fd_c);
            if (-1 == close(fd_c)) {
                perror("close() client socket");
            }
            if (-1 == close(fd_s)) {
                perror("close() server socket");
            }
            exit(0);
        } else if (1 <= pid) {
            /* parent process close accepted new client socket */
            if (-1 == close(fd_c)) {
                perror("close");
            }
        } else {
            perror("fork()");
            exit(-1);
        }
    }
    return 0;
}
