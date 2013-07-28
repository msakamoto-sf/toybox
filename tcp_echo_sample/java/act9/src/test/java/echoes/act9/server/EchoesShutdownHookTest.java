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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import junit.framework.TestCase;

import org.apache.mina.common.IoAcceptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { EchoesShutdownHook.class, ObjectName.class, System.class })
public class EchoesShutdownHookTest extends TestCase {

    @Test
    public void runnable() throws Throwable
    {

        PowerMockito.mockStatic(ObjectName.class);
        MBeanServer mockMbs = Mockito.mock(MBeanServer.class);
        ObjectName mockOname = Mockito.mock(ObjectName.class);
        IoAcceptor acceptor = Mockito.mock(IoAcceptor.class);
        EchoesShutdownHook h = new EchoesShutdownHook(acceptor, mockMbs);

        Mockito.when(ObjectName.getInstance(EchoesServer.MBEAN_KEY_IO_SERVICE_MANAGER))
               .thenReturn(mockOname);
        Mockito.when(mockMbs.invoke(mockOname, "closeAllSessions", null, null))
               .thenReturn(null);

        h.run();

        Mockito.verify(mockMbs).invoke(mockOname, "closeAllSessions", null, null);
        Mockito.verify(acceptor, Mockito.times(1)).unbindAll();
    }

    @Test
    public void shutdown()
    {
        PowerMockito.mockStatic(System.class);

        MBeanServer mbs = Mockito.mock(MBeanServer.class);
        IoAcceptor acceptor = Mockito.mock(IoAcceptor.class);
        EchoesShutdownHook h = new EchoesShutdownHook(acceptor, mbs);
        h.shutdown();

        PowerMockito.verifyStatic(Mockito.times(1));
        System.exit(0);
    }
}
