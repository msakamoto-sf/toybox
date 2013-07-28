package echoes.act8;

import java.nio.ByteOrder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class EchoesMessageDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, ByteBuffer in,
            ProtocolDecoderOutput out) throws Exception
    {
        in.order(ByteOrder.LITTLE_ENDIAN);

        int old_pos = in.position();
        byte mark = in.get();
        int length = in.getInt();
        if (length > in.remaining()) {
            // need more data for body
            in.position(old_pos);
            return false;
        }
        byte[] d = new byte[length];
        in.get(d);

        EchoesMessage m = new EchoesMessage(mark, length, d);

        out.write(m);
        return true;
    }

}
