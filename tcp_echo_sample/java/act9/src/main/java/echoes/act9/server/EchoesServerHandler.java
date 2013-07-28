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
package echoes.act9.server;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;

import echoes.act9.protocol.EchoesMessage;

/**
 * Echoes Act9 server-side IoHandler.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesServerHandler extends IoHandlerAdapter {

    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
        SessionLog.info(session, "open: " + session.getRemoteAddress() + "->"
                + session.getServiceAddress());
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof EchoesMessage)) {
            return;
        }
        EchoesMessage req = (EchoesMessage) message;

        EchoesMessage res = new EchoesMessage((byte) 0,
                                              req.getLength(),
                                              req.getRecvBuffer().rewind());

        InetSocketAddress r = (InetSocketAddress) session.getRemoteAddress();
        InetSocketAddress s = (InetSocketAddress) session.getServiceAddress();
        SessionLog.debug(session, "RECV: [" + r.getPort() + "] => ["
                + s.getPort() + "] : " + req.getLength());
        SessionLog.debug(session, "SEND: [" + s.getPort() + "] => ["
                + r.getPort() + "] : " + res.getLength());

        session.write(res).join();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception
    {
        SessionLog.info(session, "close: " + session.getRemoteAddress() + "->"
                + session.getServiceAddress());
    }
}