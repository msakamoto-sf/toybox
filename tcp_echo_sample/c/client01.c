/*
 * INET(ipv4) socket, sending "hello" simple client
 *
 * compile: $ gcc -Wall -o client01 client01.c
 * invoke : $ ./client01 hostname port (client-side bind() port)
 * 
 * (public domain software)
 */
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#define REUSE_ADDR 1

int main(int argc, char *argv[]) {
    int port_c = 0;
    int port_s = 0;
    int fd_c = 0;
    int sopt = REUSE_ADDR;
    socklen_t sopt_len = sizeof(sopt);

    struct hostent *host_s = NULL;
    struct sockaddr_in addr_c;
    struct sockaddr_in addr_s;
    socklen_t addr_in_len = sizeof(struct sockaddr_in);

    int data_len = 0;
    char *hello  = "hello";
    char data_buf[10];

    if (3 > argc) {
        printf("usage: %s hostname port (local bind port)\n", argv[0]);
        return 0;
    }
    if (4 == argc) {
        port_c = atoi(argv[3]);
        printf("socket will be bound to local port [%d]\n", port_c);
    }
    port_s = atoi(argv[2]);

    /*
     * resolve hostname
     */
    host_s = gethostbyname(argv[1]);
    if (NULL == host_s) {
        herror("gethostbyname()");
        exit(-1);
    }

    /*
     * create socket
     */
    fd_c = socket(AF_INET, SOCK_STREAM, 0);
    if (-1 == fd_c) {
        perror("socket()");
        exit(-1);
    }
    if (-1 == setsockopt(fd_c, SOL_SOCKET, SO_REUSEADDR, &sopt, sopt_len)) {
        perror("setsockopt(SO_REUSEADDR)");
        exit(-1);
    }
    printf("socket was created, hit RET for connect()...");
    getchar();

    if (port_c) {
        /*
         * bind local inet address
         */
        bzero(&addr_c, sizeof(addr_c));
        addr_c.sin_len = sizeof(addr_c);
        addr_c.sin_family = AF_INET;
        addr_c.sin_port = htons(port_c);
        addr_c.sin_addr.s_addr = INADDR_ANY; // comes from netinet/in.h
        if (-1 == bind(fd_c, (struct sockaddr*)&addr_c, addr_in_len)) {
            perror("bind()");
            exit(-1);
        }
        printf("socket was bound to LOCAL port [%d], hit RET for connect()...", port_c);
        getchar();
    }

    /*
     * connect to echo server
     */
    bzero(&addr_s, sizeof(addr_s));
    addr_s.sin_len = sizeof(addr_s);
    addr_s.sin_family = AF_INET;
    addr_s.sin_port = htons(port_s);
    bcopy(host_s->h_addr, (char*)&addr_s.sin_addr.s_addr, host_s->h_length);
    if (-1 == connect(fd_c, (struct sockaddr*)&addr_s, addr_in_len)) {
        perror("connect()");
        exit(-1);
    }
    printf("connect() success, hit RET for read/write 'hello'...");
    getchar();

    /*
     * "hello" read-write
     */
    data_len = write(fd_c, hello, strlen(hello));
    if (-1 == data_len) {
        perror("write()");
        exit(-1);
    }
    printf("[%d] bytes data write to server.\n", data_len);
    data_len = read(fd_c, data_buf, 10);
    if (-1 == data_len) {
        perror("read()");
        exit(-1);
    }
    printf("[%d] bytes data echoed from server, [%s]\n", data_len, data_buf);

    if (-1 == close(fd_c)) {
        perror("close()");
        exit(-1);
    }

    return 0;
}
