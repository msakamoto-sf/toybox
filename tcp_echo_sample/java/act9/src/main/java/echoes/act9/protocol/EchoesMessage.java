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

import org.apache.mina.common.ByteBuffer;

/**
 * Echoes Act9 business logic layer message POJO.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesMessage {

    byte mark;

    int length;

    ByteBuffer recvBuffer;

    public EchoesMessage(byte mark_, int length_, ByteBuffer recvBuffer_)
    {
        mark = mark_;
        length = length_;
        recvBuffer = recvBuffer_;
    }

    public static int calcMessageSize(int l)
    {
        return Byte.SIZE / 8 + Integer.SIZE / 8 + l;
    }

    public byte getMark()
    {
        return mark;
    }

    public void setMark(byte mark)
    {
        this.mark = mark;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public ByteBuffer getRecvBuffer()
    {
        return recvBuffer;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("mark=[");
        sb.append(mark);
        sb.append("],len=[");
        sb.append(length);
        sb.append("],data=[");
        if (null != recvBuffer) {
            int length = recvBuffer.limit();
            if (10 < length) {
                length = 10;
            }
            ByteBuffer h = ByteBuffer.wrap(recvBuffer.array(),
                                           recvBuffer.position(),
                                           length);
            sb.append(h.getHexDump());
        } else {
            sb.append("null");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof EchoesMessage)) {
            return false;
        }
        EchoesMessage o = (EchoesMessage) obj;
        return (mark == o.getMark() && length == o.getLength() && recvBuffer.equals(o.getRecvBuffer()));
    }
}
