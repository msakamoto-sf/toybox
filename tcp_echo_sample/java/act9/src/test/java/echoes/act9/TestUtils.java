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
package echoes.act9;

import java.nio.ByteOrder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.junit.Ignore;

import echoes.act9.protocol.EchoesMessage;

@Ignore
public class TestUtils {

    static {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
    }

    public static ByteBuffer buildMessageByteBuffer(int data_len)
    {
        return buildMessageByteBuffer(data_len, 0);
    }

    public static ByteBuffer buildMessageByteBuffer(int data_len,
            int difflen_to_written)
    {
        int buf_size = EchoesMessage.calcMessageSize(data_len);
        ByteBuffer buf = ByteBuffer.allocate(buf_size);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        for (byte i = 1; i <= buf_size; i++) {
            buf.put(i);
        }
        buf.flip();
        buf.put((byte) 1);
        buf.putInt(data_len + difflen_to_written);
        buf.rewind();

        return buf;
    }

}
