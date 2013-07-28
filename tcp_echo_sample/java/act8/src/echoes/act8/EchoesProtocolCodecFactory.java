package echoes.act8;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class EchoesProtocolCodecFactory implements ProtocolCodecFactory {

    public ProtocolDecoder getDecoder() throws Exception
    {
        return new EchoesMessageDecoder();
    }

    public ProtocolEncoder getEncoder() throws Exception
    {
        return new EchoesMessageEncoder();
    }

}
