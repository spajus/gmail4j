package com.googlecode.gmail4j.imap;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.ProxyAware;

public class ImapGmailConnection extends GmailConnection implements ProxyAware{
    
    private static final Log log = LogFactory.getLog(ImapGmailConnection.class);
    
    private String gmailImapHost = "imap.gmail.com";
    
    private Credentials proxyCredentials;
    
    private Properties properties;
    
    private Proxy proxy;
    
    private Session mailSession;
    
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
                if (proxy != null) {
                    log.debug("Setting proxy: " + proxy.address());
                    InetSocketAddress addr = (InetSocketAddress) proxy.address();
                    properties = System.getProperties();
                    properties.setProperty("mail.imap.host", gmailImapHost);
                    properties.put("mail.imap.ssl.enable", false);
                    properties.put("mail.socket.debug", true);
                    properties.put("mail.imaps.connectionpool.debug", true);
                    properties.put("mail.imaps.ssl.socketFactory", new HttpProxyAwareSslSocketFactory(proxy));
                    properties.put("mail.imaps.socketFactory", new HttpProxyAwareSslSocketFactory(proxy));
                    properties.put("mail.imaps.ssl.socketFactory.fallback", "false");
                    properties.put("mail.imaps.ssl.socketFactory.port", "993");
//                    properties.put("mail.imap.sasl.authorizationid", 
//                            proxyCredentials.getUsername());
//                    properties.put("proxySet", "true"); 
//                    properties.put("http.proxyHost", addr.getHostName());
//                    properties.put("http.proxyPort", 
//                            String.valueOf(addr.getPort()));
                }
                mailSession = Session.getInstance(properties, null);
                for (Provider p : mailSession.getProviders()) {
                    log.debug(p);
                }
                mailSession.setDebug(true);
            }
        }
        Store store;
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

    private Authenticator getAuthenticator() {
        if (proxyCredentials != null) {
            return new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            loginCredentials.getUsername(),
                            new String(loginCredentials.getPasword()));
                }
            };
        }
        return null;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }
    
}
