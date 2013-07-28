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
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

/**
 * Echoes Act9 "shutdown" special command transmitter.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesShutdown {

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

        String hostname = "localhost";
        int port = Integer.parseInt((String) prop.get("echoes.act9.EchoesServer.ShutdownPort"));

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        SocketConnector connector = new SocketConnector();
        connector.setWorkerTimeout(3);
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.getFilterChain()
           .addLast("codec",
                    new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        try {
            ConnectFuture future = connector.connect(new InetSocketAddress(hostname,
                                                                           port),
                                                     new EchoesShutdownHandler(),
                                                     cfg);
            future.join();
            if (future.isConnected()) {
                System.out.println("Connected.");
            } else {
                System.err.println("Timeout.");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println("Connection Error.");
        }
    }
}

/**
 * Echoes Act9 "shutdown" special command transmitter IoHandler.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
class EchoesShutdownHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
        // 1st transmission
        session.write("shutdown").join();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof String)) {
            return;
        }
        String res = (String) message;
        System.out.println(res);
    }
}
