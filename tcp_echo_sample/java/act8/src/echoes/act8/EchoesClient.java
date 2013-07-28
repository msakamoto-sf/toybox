package echoes.act8;

import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.RuntimeIOException;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

public class EchoesClient {

    public static void main(String[] args) throws Exception
    {
        if (5 > args.length) {
            System.err.println("args: host port thread_num chunk_size echo_interval");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int thread_num = Integer.parseInt(args[2]);
        int chunk_size = Integer.parseInt(args[3]);
        long echo_interval = Long.parseLong(args[4]);

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        for (int i = 0; i < thread_num; i++) {
            new EchoesClient(host, port, chunk_size, echo_interval);
        }
    }

    public EchoesClient(String hostname, int port, int chunk_size,
            long echo_interval)
    {
        SocketConnector connector = new SocketConnector();
        SocketConnectorConfig cfg = new SocketConnectorConfig();
        cfg.getFilterChain()
        .addLast("codec",
                 new ProtocolCodecFilter(new EchoesProtocolCodecFactory()));
        cfg.getFilterChain().addLast("logger", new LoggingFilter());

        try {
            connector.connect(new InetSocketAddress(hostname, port),
                              new EchoesClientHandler(chunk_size, echo_interval),
                              cfg);
        } catch (RuntimeIOException e) {
            System.err.println("Failed to connect.");
            return;
        }
    }

}
