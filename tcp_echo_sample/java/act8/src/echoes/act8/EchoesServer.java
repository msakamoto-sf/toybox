package echoes.act8;

import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class EchoesServer {

    public static void main(String[] args) throws Exception
    {
        if (1 > args.length) {
            System.err.println("port number not given in command line arguments");
            return;
        }
        int port = Integer.parseInt(args[0]);
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        new EchoesServer(port);
    }

    public EchoesServer(int port) throws Exception
    {
        IoAcceptor acceptor = new SocketAcceptor();
        IoAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getFilterChain()
           .addLast("codec",
                    new ProtocolCodecFilter(new EchoesProtocolCodecFactory()));
        cfg.getFilterChain().addLast("logger", new LoggingFilter());

        acceptor.bind(new InetSocketAddress(port),
                      new EchoesServerHandler(),
                      cfg);
        System.out.println("Listening on port " + port);
    }

}
