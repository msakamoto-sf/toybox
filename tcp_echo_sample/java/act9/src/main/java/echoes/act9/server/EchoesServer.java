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
package echoes.act9.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Properties;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoService;
import org.apache.mina.common.IoServiceConfig;
import org.apache.mina.common.IoServiceListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.integration.jmx.IoServiceManager;
import org.apache.mina.integration.jmx.IoSessionManager;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import echoes.act9.protocol.EchoesProtocolCodecFactory;

/**
 * Echoes Act9 server entry-point.
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesServer {

    public static final String MBEAN_KEY_IO_SERVICE_MANAGER = "echoes.act9:type=IoServiceManager,name=EchoesServer";

    public static final String MBEAN_KEY_IO_SESSION_MANAGER_PREFIX = "echoes.act9.session:type=IoSessionManager,name=";

    public static void main(String[] args) throws Exception
    {
        if (1 > args.length) {
            System.err.println("ini file not given");
            return;
        }

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(args[0])));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        new EchoesServer(prop);
    }

    public EchoesServer(Map<Object, Object> prop) throws Exception
    {

        int port_echoes = Integer.parseInt((String) prop.get("echoes.act9.EchoesServer.Port"));
        int port_shutdown = Integer.parseInt((String) prop.get("echoes.act9.EchoesServer.ShutdownPort"));

        IoAcceptor acceptor = new SocketAcceptor();

        EchoesShutdownHook shutdownHook = new EchoesShutdownHook(acceptor,
                                                                 ManagementFactory.getPlatformMBeanServer());
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        registerMBeans(acceptor);
        bindEchoesHandler(acceptor, port_echoes);
        bindShutdownHandler(acceptor, port_shutdown, shutdownHook);
    }

    protected void registerMBeans(IoAcceptor acceptor)
    {
        try {
            IoServiceManager iosm = new IoServiceManager(acceptor);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName(MBEAN_KEY_IO_SERVICE_MANAGER);
            mbs.registerMBean(iosm, name);
        } catch (JMException e) {
            e.printStackTrace();
        }

        acceptor.addListener(new IoServiceListener() {
            public void serviceActivated(IoService service,
                    SocketAddress serviceAddress, IoHandler handler,
                    IoServiceConfig config)
            {
            }

            public void serviceDeactivated(IoService service,
                    SocketAddress serviceAddress, IoHandler handler,
                    IoServiceConfig config)
            {
            }

            public void sessionCreated(IoSession session)
            {
                try {
                    IoSessionManager sessMgr = new IoSessionManager(session);
                    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                    ObjectName name = new ObjectName(MBEAN_KEY_IO_SESSION_MANAGER_PREFIX
                            + session.getRemoteAddress()
                                     .toString()
                                     .replace(':', '/'));
                    mbs.registerMBean(sessMgr, name);
                } catch (JMException e) {
                    e.printStackTrace();
                }
            }

            public void sessionDestroyed(IoSession session)
            {
                try {
                    ObjectName name = new ObjectName(MBEAN_KEY_IO_SESSION_MANAGER_PREFIX
                            + session.getRemoteAddress()
                                     .toString()
                                     .replace(':', '/'));
                    ManagementFactory.getPlatformMBeanServer()
                                     .unregisterMBean(name);
                } catch (JMException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void bindEchoesHandler(IoAcceptor acceptor, int port)
            throws IOException
    {
        IoAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getFilterChain()
           .addLast("codec",
                    new ProtocolCodecFilter(new EchoesProtocolCodecFactory()));
        cfg.getFilterChain().addLast("logger", new LoggingFilter());

        acceptor.bind(new InetSocketAddress(port),
                      new EchoesServerHandler(),
                      cfg);
    }

    protected void bindShutdownHandler(IoAcceptor acceptor, int port,
            EchoesServerShutdownListener listener) throws IOException
    {
        IoAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getFilterChain()
           .addLast("codec",
                    new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        acceptor.bind(new InetSocketAddress(port),
                      new EchoesServerShutdownHandler(listener),
                      cfg);
    }
}
