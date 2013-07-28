Echo Server/Client Sample By Apache MINA. (Act9 : ${project.version})

* new features

1. Mavenized
2. Graceful-Shutdown
3. JMX integration
4. Unit Test (Mockito + PowerMock)
5. NT Service Daemonized (require Apache Commons Daemon)

* protocol

simple variable length binary byte data packet like this:

packet {
    byte[0] : byte mark; // 1 or 0, not used
    byte[1]: int  length; // following byte[] array length
    - byte[4]
    byte[5] : byte[] data;
    - byte[N]
}

* requirement

- Apache Maven2
- Apache Commons Daemon (for NT Service)

* compile

 mvn clean
 mvn compile
 mvn test
 mvn package : build tar.gz/tar.bz2/zip archives

* run

run server:
 > ea9_server.bat
 $ ea9_server.sh

run client:
 > ea9_client.bat
 $ ea9_client.sh

graceful-shutdown:
 > ea9_shutdown.bat
 $ ea9_shutdown.sh
or
 telnet localhost 8081, send "shutdown"

* register as NT Service

1. get Apache Commons Daemon Win32 Binary (commons-daemon-1.x.x-bin-windows.zip)

2. expand commons-daemon-1.x.x-bin-windows.zip, copy prunmgr.exe and prunsrv.exe 
into your PATH envoirnment directory (e.g. C:\WINDOWS)

3. register as NT Service
 > ntsvc_install.bat

4. unregister
 > ntsvc_uninstall.bat

Thank you.

2010-10-15, sakamoto-gsyc-3s@glamenv-septzen.net
