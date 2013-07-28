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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.common.IoAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Echoes Act9 System.exit()'s shutdown hook (graceful-shutdown).
 *
 * @author sakamoto-gsyc-3s@glamenv-septzen.net
 */
public class EchoesShutdownHook extends Thread implements
        EchoesServerShutdownListener {

    IoAcceptor acceptor;

    MBeanServer mbs;

    public static final Logger logger = LoggerFactory.getLogger(EchoesShutdownHook.class);

    public EchoesShutdownHook(IoAcceptor acceptor_, MBeanServer mbs_)
    {
        acceptor = acceptor_;
        mbs = mbs_;
    }

    @Override
    public void run()
    {
        logger.info("shutdown sequence start");

        try {
            ObjectName name = ObjectName.getInstance(EchoesServer.MBEAN_KEY_IO_SERVICE_MANAGER);
            mbs.invoke(name, "closeAllSessions", null, null);
        } catch (Throwable t) {
            logger.error("closeAllSessions() failed.", t);
        }

        acceptor.unbindAll();
        logger.info("IoAcceptor unboud");
    }

    public void shutdown()
    {
        System.exit(0);
    }
}
