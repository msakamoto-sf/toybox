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
package echoes.act9.protocol;

import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EchoesMessageTest extends TestCase {

    static {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
    }

    @Test
    public void calcMessageSize()
    {
        assertEquals(15, EchoesMessage.calcMessageSize(10));
    }

    @Test
    public void toString_null()
    {
        EchoesMessage m = new EchoesMessage((byte) 2, 100, null);
        assertEquals("mark=[2],len=[100],data=[null]", m.toString());
    }

    @Test
    public void toString_0bytes()
    {
        ByteBuffer buf_0 = ByteBuffer.allocate(0);
        buf_0.clear();
        EchoesMessage m = new EchoesMessage((byte) 1, 1, buf_0);
        assertEquals("mark=[1],len=[1],data=[empty]", m.toString());
    }

    @Test
    public void toString_1bytes()
    {
        ByteBuffer buf_1 = ByteBuffer.allocate(1);
        buf_1.put((byte) 1);
        buf_1.flip();
        EchoesMessage m = new EchoesMessage((byte) 1, 1, buf_1);
        assertEquals("mark=[1],len=[1],data=[01]", m.toString());
    }

    @Test
    public void toString_10bytes()
    {
        ByteBuffer buf_10 = ByteBuffer.allocate(10);
        for (byte i = 1; i <= 10; i++) {
            buf_10.put(i);
        }
        buf_10.flip();
        EchoesMessage m = new EchoesMessage((byte) 1, 100, buf_10);
        assertEquals("mark=[1],len=[100],data=[01 02 03 04 05 06 07 08 09 0A]", m.toString());
    }

    @Test
    public void toString_11bytes()
    {
        ByteBuffer buf_11 = ByteBuffer.allocate(11);
        for (byte i = 1; i <= 11; i++) {
            buf_11.put(i);
        }
        buf_11.flip();
        EchoesMessage m = new EchoesMessage((byte) 1, 100, buf_11);
        assertEquals("mark=[1],len=[100],data=[01 02 03 04 05 06 07 08 09 0A]", m.toString());
    }
}
