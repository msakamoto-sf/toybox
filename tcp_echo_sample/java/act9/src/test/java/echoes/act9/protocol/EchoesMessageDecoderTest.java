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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import echoes.act9.TestUtils;

@RunWith(JUnit4.class)
public class EchoesMessageDecoderTest extends TestCase {

    @Test
    public void needMoreData() throws Exception
    {
        int data_len = 10;
        ByteBuffer buf = TestUtils.buildMessageByteBuffer(data_len, 1);

        EchoesMessageDecoder decoder = new EchoesMessageDecoder();
        IoSession session = mock(IoSession.class);
        ProtocolDecoderOutput out = mock(ProtocolDecoderOutput.class);
        doNothing().when(out).write(any(ByteBuffer.class));
        assertFalse(decoder.doDecode(session, buf, out));
    }

    @Test
    public void decodeMessage() throws Exception
    {
        int data_len = 10;
        ByteBuffer buf = TestUtils.buildMessageByteBuffer(data_len);

        EchoesMessageDecoder decoder = new EchoesMessageDecoder();
        IoSession session = mock(IoSession.class);
        ProtocolDecoderOutput out = mock(ProtocolDecoderOutput.class);
        doNothing().when(out).write(any(EchoesMessage.class));

        assertTrue(decoder.doDecode(session, buf, out));
        assertEquals(0, buf.remaining());
        buf.rewind();

        ByteBuffer buf2 = TestUtils.buildMessageByteBuffer(data_len);
        EchoesMessage expected = new EchoesMessage((byte) 1, data_len, buf2);
        verify(out).write(expected);
    }
}
