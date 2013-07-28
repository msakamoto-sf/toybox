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

import static org.mockito.Mockito.*;

import java.nio.ByteOrder;

import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class EchoesMessageEncoderTest extends TestCase {

    static {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
    }

    @Test
    public void writtenByteBufferIsCorrect() throws Exception
    {
        int data_len = 10;
        int buf_size = EchoesMessage.calcMessageSize(data_len);
        ByteBuffer buf = ByteBuffer.allocate(buf_size);
        for (byte i = 1; i <= buf_size; i++) {
            buf.put(i);
        }
        buf.flip();
        EchoesMessage m = new EchoesMessage((byte) 1, data_len, buf);
        EchoesMessageEncoder encoder = new EchoesMessageEncoder();

        IoSession session = mock(IoSession.class);
        ProtocolEncoderOutput out = mock(ProtocolEncoderOutput.class);
        doNothing().when(out).write(any(ByteBuffer.class));
        encoder.encode(session, m, out);

        ByteBuffer expected = ByteBuffer.allocate(buf_size);
        expected.order(ByteOrder.LITTLE_ENDIAN);
        expected.put((byte) 1);
        expected.putInt(data_len);
        byte[] barray = buf.array();
        for (int i = expected.position(); i < barray.length; i++) {
            expected.put(barray[i]);
        }
        expected.flip();

        verify(out).write(expected);
    }

    @Test
    public void writeShorterLengthThanActual() throws Exception
    {
        int data_len = 10;
        int buf_size = EchoesMessage.calcMessageSize(data_len);
        ByteBuffer buf = ByteBuffer.allocate(buf_size);
        for (byte i = 1; i <= buf_size; i++) {
            buf.put(i);
        }
        buf.flip();
        int SHORTER_LEN = data_len - 1;
        EchoesMessage m = new EchoesMessage((byte) 1, SHORTER_LEN, buf);
        EchoesMessageEncoder encoder = new EchoesMessageEncoder();

        IoSession session = mock(IoSession.class);
        ProtocolEncoderOutput out = mock(ProtocolEncoderOutput.class);
        doNothing().when(out).write(any(ByteBuffer.class));
        encoder.encode(session, m, out);

        ByteBuffer expected = ByteBuffer.allocate(buf_size);
        expected.order(ByteOrder.LITTLE_ENDIAN);
        expected.put((byte) 1);
        expected.putInt(SHORTER_LEN);
        byte[] barray = buf.array();
        for (int i = expected.position(); i < barray.length - 1; i++) {
            expected.put(barray[i]);
        }
        expected.flip();

        verify(out).write(expected);
    }

    @Test
    public void writeLongerLengthThanActual() throws Exception
    {
        int data_len = 10;
        int buf_size = EchoesMessage.calcMessageSize(data_len);
        ByteBuffer buf = ByteBuffer.allocate(buf_size);
        for (byte i = 1; i <= buf_size; i++) {
            buf.put(i);
        }
        buf.flip();
        int LONGER_LEN = data_len + 1;
        EchoesMessage m = new EchoesMessage((byte) 1, LONGER_LEN, buf);
        EchoesMessageEncoder encoder = new EchoesMessageEncoder();

        IoSession session = mock(IoSession.class);
        ProtocolEncoderOutput out = mock(ProtocolEncoderOutput.class);
        doNothing().when(out).write(any(ByteBuffer.class));
        encoder.encode(session, m, out);

        ByteBuffer expected = ByteBuffer.allocate(buf_size);
        expected.order(ByteOrder.LITTLE_ENDIAN);
        expected.put((byte) 1);
        expected.putInt(LONGER_LEN);
        byte[] barray = buf.array();
        for (int i = expected.position(); i < barray.length; i++) {
            expected.put(barray[i]);
        }
        expected.flip();

        verify(out).write(expected);
    }
}
