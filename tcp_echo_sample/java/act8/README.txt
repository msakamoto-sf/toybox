Echo Server/Client Sample By Apache MINA.

* protocol

simple variable length binary byte data packet like this:

packet {
    byte[0] : byte mark; // 1 or 0, not used
    byte[1]: int  length; // following byte[] array length
    - byte[4]
    byte[5] : byte[] data;
    - byte[N]
}

* compile

1. prepare Apache MINA, SLF4j, log4j.

versions :
mina : 1.1.7
slf4j : 1.6.1
log4j : 1.2.14

2. add "APACHE_MINAS" as user-library including following *.jars in eclipse.

mina-core-1.1.7.jar
log4j-1.2.14.jar
slf4j-log4j12-1.6.1.jar

slf4j-api-1.6.1.jar

3. import this project into Eclipse workspace and refresh.

* testing

# start echo server at port 8080
java -cp ... echoes.act8.EchoesServer 8080

# 10 threads, send 1MB packet, 100ms interval
java -cp ... echoes.act8.EchoesClient localhost 8080 10 1048576 100

# with gcviewer example:
java -cp ... -server -Xms200m -Xmx200m -Xloggc:gc.log echoes.act8.EchoesServer 8080
