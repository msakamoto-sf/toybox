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

import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.util.SessionLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import echoes.act9.TestUtils;
import echoes.act9.protocol.EchoesMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { SessionLog.class })
public class EchoesServerHandlerTest extends TestCase {

    @Test
    public void ignoreInvalidObject() throws Exception
    {
        IoSession session = Mockito.mock(IoSession.class);
        Object invalidObject = new Object();

        EchoesServerHandler h = new EchoesServerHandler();
        h.messageReceived(session, invalidObject);

        Mockito.verify(session, Mockito.never()).write(Mockito.anyObject());
    }

    @Test
    public void echoMessage() throws Exception
    {
        PowerMockito.mockStatic(SessionLog.class);

        IoSession session = Mockito.mock(IoSession.class);
        WriteFuture wf = Mockito.mock(WriteFuture.class);
        Mockito.when(session.write(Mockito.anyObject())).thenReturn(wf);
        InetSocketAddress mockAddr = new InetSocketAddress(8080);
        Mockito.when(session.getRemoteAddress()).thenReturn(mockAddr);
        Mockito.when(session.getServiceAddress()).thenReturn(mockAddr);

        int data_len = 10;
        ByteBuffer buf = TestUtils.buildMessageByteBuffer(data_len, 1);
        EchoesMessage m = new EchoesMessage((byte) 1, data_len, buf);

        EchoesServerHandler h = new EchoesServerHandler();
        h.messageReceived(session, m);

        ByteBuffer buf2 = TestUtils.buildMessageByteBuffer(data_len, 1);
        EchoesMessage expected = new EchoesMessage((byte) 0, data_len, buf2);
        Mockito.verify(session).write(expected);
    }

}
