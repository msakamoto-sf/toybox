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

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

/**
 * Echoes Act9 "shutdown" special command IoHandler.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesServerShutdownHandler extends IoHandlerAdapter {

    protected EchoesServerShutdownListener listener;

    public EchoesServerShutdownHandler(EchoesServerShutdownListener listener_)
    {
        listener = listener_;
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof String)) {
            return;
        }
        String token = (String) message;
        if (!token.equals("shutdown")) {
            session.write("NG").join();
        } else {
            session.write("OK").join();
            listener.shutdown();
        }
    }

}
