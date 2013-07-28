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

import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;

import echoes.act9.protocol.EchoesMessage;

/**
 * Echoes Act9 client-side IoHandler
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesClientHandler extends IoHandlerAdapter {

    Random rnd = new Random();

    int chunk_size;

    byte[] data;

    long echo_interval;

    public EchoesClientHandler(int chunk_size_, long echo_interval_)
    {
        chunk_size = chunk_size_;
        echo_interval = echo_interval_;
        int buf_sz = EchoesMessage.calcMessageSize(chunk_size);
        data = new byte[buf_sz];
    }

    ByteBuffer nextRandomByteBuffer()
    {
        rnd.nextBytes(data);
        return ByteBuffer.wrap(data);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
        SessionLog.info(session, "open: " + session.getLocalAddress() + "->"
                + session.getRemoteAddress());
        // 1st transmission
        session.write(new EchoesMessage((byte) 1,
                                        chunk_size,
                                        nextRandomByteBuffer())).join();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof EchoesMessage)) {
            return;
        }
        EchoesMessage req = (EchoesMessage) message;

        InetSocketAddress l = (InetSocketAddress) session.getLocalAddress();
        InetSocketAddress r = (InetSocketAddress) session.getRemoteAddress();
        SessionLog.debug(session, "RECV : [" + r.getPort() + "] => ["
                + l.getPort() + "] : " + req.getLength());

        try {
            Thread.sleep(echo_interval);
        } catch (InterruptedException e) {
        }

        // next transmission
        session.write(new EchoesMessage((byte) 1,
                                        chunk_size,
                                        nextRandomByteBuffer())).join();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception
    {
        SessionLog.info(session, "close: " + session.getLocalAddress() + "->"
                + session.getRemoteAddress());
    }
}