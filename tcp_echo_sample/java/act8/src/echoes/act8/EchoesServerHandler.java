package echoes.act8;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;

class EchoesServerHandler extends IoHandlerAdapter {

    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception
    {
        if (!(message instanceof EchoesMessage)) {
            return;
        }
        EchoesMessage req = (EchoesMessage) message;

        EchoesMessage res = new EchoesMessage((byte) 0,
                                              req.getLength(),
                                              req.getData());

        InetSocketAddress r = (InetSocketAddress) session.getRemoteAddress();
        InetSocketAddress s = (InetSocketAddress) session.getServiceAddress();
        SessionLog.info(session, "RECV: [" + r.getPort() + "] => ["
                + s.getPort() + "] : " + req.getLength());
        SessionLog.info(session, "SEND: [" + s.getPort() + "] => ["
                + r.getPort() + "] : " + res.getLength());

        session.write(res).join();
    }
}