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

import junit.framework.TestCase;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.WriteFuture;
import org.apache.mina.util.SessionLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { SessionLog.class })
public class EchoesServerShutdownHandlerTest extends TestCase {

    @Test
    public void ignoreInvalidObject() throws Exception
    {
        EchoesServerShutdownListener l = Mockito.mock(EchoesServerShutdownListener.class);
        IoSession session = Mockito.mock(IoSession.class);
        Object invalidObject = new Object();

        EchoesServerShutdownHandler h = new EchoesServerShutdownHandler(l);
        h.messageReceived(session, invalidObject);

        Mockito.verify(session, Mockito.never()).write(Mockito.anyObject());
        Mockito.verify(l, Mockito.never()).shutdown();
    }

    @Test
    public void receiveIncorrectToken() throws Exception
    {
        EchoesServerShutdownListener l = Mockito.mock(EchoesServerShutdownListener.class);
        IoSession session = Mockito.mock(IoSession.class);
        WriteFuture wf = Mockito.mock(WriteFuture.class);
        Mockito.when(session.write(Mockito.anyObject())).thenReturn(wf);

        EchoesServerShutdownHandler h = new EchoesServerShutdownHandler(l);
        h.messageReceived(session, "incorrect");

        Mockito.verify(session).write("NG");
        Mockito.verify(wf).join();
        Mockito.verify(l, Mockito.never()).shutdown();
    }

    @Test
    public void receiveCorrectToken() throws Exception
    {
        EchoesServerShutdownListener l = Mockito.mock(EchoesServerShutdownListener.class);
        IoSession session = Mockito.mock(IoSession.class);
        WriteFuture wf = Mockito.mock(WriteFuture.class);
        Mockito.when(session.write(Mockito.anyObject())).thenReturn(wf);

        EchoesServerShutdownHandler h = new EchoesServerShutdownHandler(l);
        h.messageReceived(session, "shutdown");

        Mockito.verify(session).write("OK");
        Mockito.verify(wf).join();
        Mockito.verify(l).shutdown();
    }
}
