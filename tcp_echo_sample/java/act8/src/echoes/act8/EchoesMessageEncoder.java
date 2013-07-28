package echoes.act8;

import java.nio.ByteOrder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class EchoesMessageEncoder extends ProtocolEncoderAdapter {

    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception
    {
        EchoesMessage m = (EchoesMessage) message;

        int cap = 1 + 4 + m.getLength();
        ByteBuffer d = ByteBuffer.allocate(cap);
        d.order(ByteOrder.LITTLE_ENDIAN);

        d.put(m.getMark());
        d.putInt(m.getLength());
        d.put(m.getData());
        d.flip();

        out.write(d);
    }
}
