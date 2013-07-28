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

import java.nio.ByteOrder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * Echoes Act9 message POJO to ByteBuffer encoder.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesMessageEncoder extends ProtocolEncoderAdapter {

    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception
    {
        EchoesMessage m = (EchoesMessage) message;
        ByteBuffer buf = m.getRecvBuffer();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.rewind();
        buf.put(m.getMark());
        buf.putInt(m.getLength());
        int l = buf.position() + m.getLength();
        l = (buf.capacity() < l) ? buf.capacity() : l;
        buf.limit(l);
        buf.rewind();
        out.write(buf);
    }
}
