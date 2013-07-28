/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package echoes.act9.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import echoes.act9.protocol.EchoesProtocolCodecFactory;

/**
 * Echoes Act9 multithreaded, endless loop client.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesClient extends Thread {

    String hostname;

    int port;

    int chunk_size;

    long echo_interval;

    public static void main(String[] args) throws Exception
    {
        if (1 > args.length) {
            System.err.println("ini file not given");
            return;
        }

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(args[0])));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String host = (String) prop.get("echoes.act9.EchoesClient.Host");
        int port = Integer.parseInt((String) prop.get("echoes.act9.EchoesClient.Port"));
        int thread_num = Integer.parseInt((String) prop.get("echoes.act9.EchoesClient.ThreadNum"));
        int chunk_size = Integer.parseInt((String) prop.get("echoes.act9.EchoesClient.ChunkSize"));
        long echo_interval = Long.parseLong((String) prop.get("echoes.act9.EchoesClient.EchoInterval"));

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        for (int i = 0; i < thread_num; i++) {
            new EchoesClient(host, port, chunk_size, echo_interval).start();
        }
    }

    public EchoesClient(String hostname_, int port_, int chunk_size_,
            long echo_interval_)
    {
        hostname = hostname_;
        port = port_;
        chunk_size = chunk_size_;
        echo_interval = echo_interval_;
    }

    @Override
    public void run()
    {
        SocketConnector connector = new SocketConnector();
        connector.setWorkerTimeout(10);
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.getFilterChain()
           .addLast("codec",
                    new ProtocolCodecFilter(new EchoesProtocolCodecFactory()));
        cfg.getFilterChain().addLast("logger", new LoggingFilter());

        try {
            connector.connect(new InetSocketAddress(hostname, port),
                              new EchoesClientHandler(chunk_size, echo_interval),
                              cfg);
        } catch (RuntimeIOException e) {
            System.err.println("Failed to connect.");
            return;
        }
    }
}
