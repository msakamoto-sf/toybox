package echoes.act8;

import java.net.InetSocketAddress;
import java.util.Random;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;

class EchoesClientHandler extends IoHandlerAdapter {

    Random rnd = new Random();

    int chunk_size;

    byte[] data;

    long echo_interval;

    public EchoesClientHandler(int chunk_size_, long echo_interval_)
    {
        chunk_size = chunk_size_;
        echo_interval = echo_interval_;
        data = new byte[chunk_size];
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception
    {
        rnd.nextBytes(data);
        // 1st transmission
        session.write(new EchoesMessage((byte) 1, data.length, data)).join();
    }

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof EchoesMessage)) {
            return;
        }
        EchoesMessage req = (EchoesMessage) message;

        InetSocketAddress l = (InetSocketAddress) session.getLocalAddress();
        InetSocketAddress r = (InetSocketAddress) session.getRemoteAddress();
        SessionLog.info(session, "RECV : [" + r.getPort() + "] => ["
                + l.getPort() + "] : " + req.getLength());

        try {
            Thread.sleep(echo_interval);
        } catch (InterruptedException e) {
        }

        // new transmission
        rnd.nextBytes(data);
        // 1st transmission
        session.write(new EchoesMessage((byte) 1, data.length, data)).join();
    }
}