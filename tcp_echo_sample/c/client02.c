/*
 * INET(ipv4) socket, sending "hello" simple client (support connect timeout)
 *
 * compile: $ gcc -Wall -o client02 client02.c
 * invoke : $ ./client02 hostname port connect-timeout-by-second
 * 
 * (public domain software)
 */
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <netdb.h>

#define REUSE_ADDR 1

int main(int argc, char *argv[]) {
    int timeout_sec = 0;
    int port_s = 0;
    int fd_c = 0;
    int sopt = REUSE_ADDR;
    socklen_t sopt_len = sizeof(sopt);
    int fd_status = 0;
    int syscall_r = 0;

    struct hostent *host_s = NULL;
    struct sockaddr_in addr_s;
    socklen_t addr_in_len = sizeof(struct sockaddr_in);

    fd_set fdset_c;
    struct timeval tv;
    int sockopt_val;
    socklen_t sockopt_len = sizeof(sockopt_val);

    int data_len = 0;
    char *hello  = "hello";
    char data_buf[10];

    if (4 > argc) {
        printf("usage: %s hostname port connect-timeout-by-second\n", argv[0]);
        return 0;
    }
    port_s = atoi(argv[2]);
    timeout_sec = atoi(argv[3]);
    tv.tv_sec = timeout_sec;
    tv.tv_usec = 0;

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

    // detect timeout by non-blocking connect()
    // see: http://www.developerweb.net/forum/showthread.php?p=13486

    /*
     * set non-blocking mode (for connect() timeout detection)
     */
    if (-1 == (fd_status = fcntl(fd_c, F_GETFL, NULL))) {
        perror("fcntl(F_GETFL)");
        exit(-1);
    }
    fd_status |= O_NONBLOCK;
    if (-1 == fcntl(fd_c, F_SETFL, fd_status)) {
        perror("fcntl(F_SETFL)");
        exit(-1);
    }

    /*
     * connect to echo server
     */
    bzero(&addr_s, sizeof(addr_s));
    addr_s.sin_len = sizeof(addr_s);
    addr_s.sin_family = AF_INET;
    addr_s.sin_port = htons(port_s);
    bcopy(host_s->h_addr, (char*)&addr_s.sin_addr.s_addr, host_s->h_length);

    syscall_r = connect(fd_c, (struct sockaddr*)&addr_s, addr_in_len);
    if (-1 == syscall_r) {
        if (EINPROGRESS != errno) {
            perror("connect()");
            exit(-1);
        }
        printf("EINPROGRESS in connect() - selecting\n"); 
        FD_ZERO(&fdset_c);
        FD_SET(fd_c, &fdset_c);
        syscall_r = select(fd_c + 1, NULL, &fdset_c, NULL, &tv);
        if (-1 == syscall_r) {
            perror("select()");
            exit(-1);
        }
        if (0  == syscall_r) {
            printf("DETECT CONNECTION TIMEOUT [%d] SECONDS!! ... terminates.\n", timeout_sec);
            exit(0);
        }
        // check socket error
        if (-1 == getsockopt(fd_c, 
                    SOL_SOCKET, 
                    SO_ERROR, 
                    (void*)(&sockopt_val), 
                    &sockopt_len))
        { 
            perror("getsockopt()");
            exit(-1); 
        } 
        if (sockopt_val) {
            fprintf(stderr, "socket error in delayed connection %d - %s\n",
                sockopt_val, strerror(sockopt_val));
            exit(-1);
        }
    }

    /*
     * restore to blocking-mode
     */
    if (-1 == (fd_status = fcntl(fd_c, F_GETFL, NULL))) {
        perror("fcntl(F_GETFL)");
        exit(-1);
    }
    fd_status &= (~O_NONBLOCK);
    if (-1 == fcntl(fd_c, F_SETFL, fd_status)) {
        perror("fcntl(F_SETFL)");
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
