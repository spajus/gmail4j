/*
 * Copyright (c) 2008-2012 Tomas Varaneckas
 * http://www.varaneckas.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.gmail4j.javamail;

import javax.mail.event.ConnectionAdapter;
import javax.mail.event.ConnectionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JavaMail based IMAP {@link ConnectionAdapter} for Connection events.
 * <p>
 * ImapConnectionHandler is the adapter which receives connection events.
 * This class logs the connection event services if
 * {@code getSession().setDebug(true)}.
 * <p>
 * Example:
 * <p><blockquote><pre>
 *     // if implementing a {@link Transport protcol}.
 *     Transport transport = getSession().getTransport();
 *     transport.addConnectionListener(new ImapConnectionHandler(
 *                  new ConnectionInfo(loginCredentials.getUsername(),
 *                  gmailSmtpHost,
 *                  gmailSmtpPort)));
 * </pre></blockquote></p>
 *
 * @author Rajiv Perera &lt;rajivderas@gmail.com&gt;
 * @since 0.4
 */
public class ImapConnectionHandler extends ConnectionAdapter {

    /**
    * Logger
    */
    private static final Log LOG = LogFactory.getLog(ImapConnectionHandler.class);
    /**
     * Contain information of current connection service{@link ConnectionInfo}.
     *
     * @see #ConnectionInfo(String user, String host, int port)
     */
    private final ConnectionInfo connectionInfo;
    /**
     * Holds the current connection service start time in milli seconds.
     */
    private long startTime;

    /**
     * Constructor with ConnectionInfo object
     *
     * @param connectionInfo ConnectionInfo object
     */
    public ImapConnectionHandler(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public void opened(ConnectionEvent e) {
        if (LOG.isDebugEnabled()) {
            startTime = System.currentTimeMillis();
            LOG.debug("ImapGmailConnection " + connectionInfo + "is opened.");
        }
    }

    @Override
    public void closed(ConnectionEvent e) {
        if (LOG.isDebugEnabled()) {
            long upTime = (System.currentTimeMillis() - startTime) / 1000;
            LOG.debug("ImapGmailConnection " + connectionInfo 
                    + "is closed after " + upTime + " seconds.");
        }
    }

    @Override
    public void disconnected(ConnectionEvent e) {
        if (LOG.isDebugEnabled()) {
            long upTime = (System.currentTimeMillis() - startTime) / 1000;
            LOG.debug("ImapGmailConnection " + connectionInfo 
                    + "has been disconnectedafter " + upTime + " seconds.");
        }
    }
}