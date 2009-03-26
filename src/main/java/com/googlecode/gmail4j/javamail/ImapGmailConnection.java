/*
 * Copyright (c) 2008-2009 Tomas Varaneckas
 * http://www.varaneckas.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.gmail4j.javamail;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Properties;

import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.HttpProxyAwareSslSocketFactory;
import com.googlecode.gmail4j.http.ProxyAware;

/**
 * TODO document
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id: ImapGmailClient.java 30 2009-03-25 10:16:04Z tomas.varaneckas $
 * @since 0.3
 */
public class ImapGmailConnection extends GmailConnection implements ProxyAware{
    
    private static final Log log = LogFactory.getLog(ImapGmailConnection.class);
    
    private String gmailImapHost = "imap.gmail.com";
    
    private Credentials proxyCredentials;
    
    private Properties properties;
    
    private Proxy proxy;
    
    private Session mailSession;
    
    private Store store;
    
    public ImapGmailConnection() {
        //javax.net.ssl.SSLSocketFactory
    }
    
    public ImapGmailConnection(final String username, final char[] password) {
        this(new Credentials(username, password));
    }
    
    public ImapGmailConnection(final Credentials loginCredentials) {
        this();
        this.loginCredentials = loginCredentials;
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

    public String getGmailImapHost() {
        return gmailImapHost;
    }

    public void setGmailImapHost(String gmailImapHost) {
        this.gmailImapHost = gmailImapHost;
    }
    
    public Store openGmailStore() {
        synchronized (this) {
            if (mailSession == null) {
                properties = System.getProperties();
                if (proxy != null) {
                    log.debug("Setting proxy: " + proxy.address());
                    final SocketFactory sf = new HttpProxyAwareSslSocketFactory(proxy);
                    properties.setProperty("mail.imap.host", gmailImapHost);
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
        try {
            store = mailSession.getStore("imaps");
            store.connect(gmailImapHost, loginCredentials.getUsername()
                    .concat("@gmail.com"), 
                    new String(loginCredentials.getPasword()));
        } catch (final Exception e) {
            throw new GmailException("Failed opening Gmail IMAP store", e);
        }
        return store;
    }
    
    public void disconnect() {
        try {
            if (store != null) {
                store.close();
            }
        } catch (final Exception e) {
            log.warn("Failed closing Gmail IMAP store", e);
        }
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
    
    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
    
}
