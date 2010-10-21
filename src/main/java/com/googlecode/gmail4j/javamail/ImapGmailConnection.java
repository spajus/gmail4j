/*
 * Copyright (c) 2008-2009 Tomas Varaneckas
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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Properties;

import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.HttpProxyAwareSslSocketFactory;
import com.googlecode.gmail4j.http.ProxyAware;

/**
 * JavaMail based IMAP {@link GmailConnection} implementation.
 * <p>
 * ImapGmailConnection is {@link ProxyAware}, allowing to use JavaMail IMAP
 * via HTTP proxy, which was not possible by JavaMail design.
 * <p>
 * Example:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     conn.setLoginCredentials(new Credentials("user", "pass".toCharArray()));
 *     //if proxy is required
 *     ((ProxyAware) conn).setProxy("proxy.example.com", 8080);
 *     //if proxy auth is required
 *     ((ProxyAware) conn).setProxyCredentials("proxyuser", 
 *         "proxypass".toCharArray());
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 * </pre></blockquote></p>
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class ImapGmailConnection extends GmailConnection implements ProxyAware {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(ImapGmailConnection.class);
    /**
     * Gmail IMAP host. Should not be set unless Gmail server has moved.
     * 
     * @see #getGmailImapHost()
     * @see #setGmailImapHost(String)
     */
    private String gmailImapHost = "imap.gmail.com";
    /**
     * Gmail SMTP host for mail sending. Should not be set unless Gmail server 
     * has moved.
     * 
     * @see #getGmailSmtpHost()
     * @see #setGmailSmtpHost(String)
     */
    private String gmailSmtpHost = "smtp.gmail.com";
    /**
     * Gmail SMTP port for mail sending. Should not be set unless Gmail server 
     * has moved.
     * 
     * @see #getGmailSmtpPort()
     * @see #setGmailSmtpPort(String)
     */
    private int gmailSmtpPort = 465;
    /**
     * Proxy Credentials
     */
    private Credentials proxyCredentials;
    /**
     * JavaMail configuration {@link Properties}. Derrived from {@link System}
     * properties.
     * 
     * @see System#getProperties()
     * @see System#setProperty(String, String)
     */
    private Properties properties;
    /**
     * Proxy in use
     */
    private Proxy proxy;
    /**
     * JavaMail {@link Session}
     * 
     * @see #getSession()
     */
    private Session mailSession;
    /**
     * JavaMail {@link Store}
     */
    private Store store;
    /**
     * Contain the state of current connection {@link Store}.
     *
     * @see #isConnected()
     * @see #setConnected(boolean)
     */
    private boolean connected = false;

    /**
     * Argless constructor.
     */
    public ImapGmailConnection() {
    }

    /**
     * Constructor with Gmail username and password 
     * 
     * @param username Gmail username
     * @param password Gmail password
     */
    public ImapGmailConnection(final String username, final char[] password) {
        this(new Credentials(username, password));
    }

    /**
     * Constructor with Gmail {@link Credentials}
     * 
     * @param loginCredentials Gmail login credentials
     */
    public ImapGmailConnection(final Credentials loginCredentials) {
        this();
        this.loginCredentials = loginCredentials;
    }

    /**
     * Gets {@link #gmailImapHost}
     * 
     * @return Gmail IMAP host
     */
    public String getGmailImapHost() {
        return gmailImapHost;
    }

    /**
     * Sets {@link #gmailImapHost}.
     * <p>
     * You don't have to set Gmail IMAP host, unless it differs from the
     * predefined value (imap.gmail.com).
     * 
     * @param gmailImapHost Gmail IMAP host
     */
    public void setGmailImapHost(final String gmailImapHost) {
        this.gmailImapHost = gmailImapHost;
    }

    /**
     * Gets {@link #gmailSmtpHost}
     * 
     * @return Gmail SMTP host
     */
    public String getGmailSmtpHost() {
        return gmailSmtpHost;
    }

    /**
     * Sets {@link #gmailSmtpHost}.
     * <p>
     * You don't have to set Gmail SMTP host, unless it differs from the
     * predefined value (smtp.gmail.com).
     * 
     * @param gmailSmtpHost Gmail SMTP host
     */
    public void setGmailSmtpHost(final String gmailSmtpHost) {
        this.gmailSmtpHost = gmailSmtpHost;
    }

    /**
     * Gets {@link #gmailSmtpPort}
     * 
     * @return Gmail SMTP port
     */
    public int getGmailSmtpPort() {
        return gmailSmtpPort;
    }

    /**
     * Sets {@link #gmailSmtpPort}.
     * <p>
     * You don't have to set Gmail SMTP port, unless it differs from the
     * predefined value (465).
     * 
     * @param gmailSmtpPort Gmail SMTP port
     */
    public void setGmailSmtpPort(final int gmailSmtpPort) {
        this.gmailSmtpPort = gmailSmtpPort;
    }

    /**
     * Opens Gmail {@link Store}
     * 
     * @return singleton instance of Gmail {@link Store}
     */
    public Store openGmailStore() {
        try {
            // if stort object instance is not null and the service connection state
            // is connected then close this service and terminate its connection.
            // Note : due to security concerns gmail imap only allow max 10 connections
            // because of this reason any existing connection that is open
            // should terminate.
            if (isConnected()) {
                disconnect();
            }
            
            store = getSession().getStore("imaps");
            store.connect(gmailImapHost, loginCredentials.getUsername().concat("@gmail.com"),
                    new String(loginCredentials.getPasword()));
            setConnected(store.isConnected());
        } catch (final Exception e) {
            System.out.println("openGmailStore error : ");
            e.printStackTrace();
            //throw new GmailException("Failed opening Gmail IMAP store", e);
        }
        return store;
    }

    /**
     * Gets Gmail {@link Transport}
     * 
     * @return Configured and ready for use Transport
     */
    public Transport getTransport() {
        try {
            final Transport transport = getSession().getTransport();
            transport.connect(loginCredentials.getUsername(),
                    new String(loginCredentials.getPasword()));
            return transport;
        } catch (final Exception e) {
            throw new GmailException("ImapGmailClient requires ImapGmailConnection!");
        }
    }

    /**
     * Gets Gmail {@link Session}
     * 
     * @return Configured and ready for use Gmail {@link Session}
     */
    public Session getSession() {
        synchronized (this) {
            if (mailSession == null) {
                properties = System.getProperties();
                properties.put("mail.transport.protocol", "smtps");
                properties.put("mail.debug", true);
                properties.put("mail.smtps.host", gmailSmtpHost);
                properties.put("mail.smtps.port", gmailSmtpPort);
                if (proxy != null) {
                    log.debug("Setting proxy: " + proxy.address());
                    final SocketFactory sf =
                            new HttpProxyAwareSslSocketFactory(proxy, proxyCredentials);
                    properties.put("mail.imap.host", gmailImapHost);
                    properties.put("mail.imaps.ssl.socketFactory", sf);
                    properties.put("mail.imaps.ssl.socketFactory.fallback", "false");
                    properties.put("mail.imaps.ssl.socketFactory.port", "993");
                }
                if (proxyCredentials != null) {
                    mailSession = Session.getInstance(properties);
                } else {
                    mailSession = Session.getInstance(properties);
                }
                for (Provider p : mailSession.getProviders()) {
                    log.debug(p);
                }
            }
        }
        return mailSession;
    }

    /**
     * Disconnects from Gmail
     */
    public void disconnect() {
        try {
            if (isConnected()) {
                store.close();
            }
        } catch (final Exception e) {
            log.warn("Failed closing Gmail IMAP store", e);
        }
    }

    /**
     * Sets {@link #connected}.
     * <p>
     * @param connected Gmail SMTP port
     */
    public void setConnected(final boolean connected) {
        this.connected = connected;
    }

    /**
     * Is this service currently connected?
     * <p>
     *
     * This implementation uses a private boolean field to
     * store the connection state. This method returns the value
     * of that field. <p>
     *
     * @return true if the service is connected, false if it is not connected
     */
    public boolean isConnected() {
        return connected;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    public void setProxy(final String host, final int port) {
        setProxy(new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(host, port)));
    }

    public void setProxyCredentials(final Credentials credentials) {
        credentials.validate();
        proxyCredentials = credentials;
    }

    public void setProxyCredentials(final String user, final char[] pass) {
        setProxyCredentials(new Credentials(user, pass));
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}
