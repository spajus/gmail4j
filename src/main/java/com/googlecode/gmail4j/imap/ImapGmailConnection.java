package com.googlecode.gmail4j.imap;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.auth.Credentials;

public class ImapGmailConnection extends GmailConnection {
    
    private String gmailImapHost = "imap.gmail.com";
    
    private Credentials proxyCredentials;
    
    private Properties properties;
    
    private Session mailSession;
    
    public ImapGmailConnection(final Credentials loginCredentials) {
        properties = System.getProperties();
        properties.setProperty("mail.imap.host", gmailImapHost);
        properties.setProperty ("mail.imap.socketFactory.class", 
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty ("mail.imap.socketFactory.fallback", "false");
        properties.setProperty ("mail.imap.socketFactory.port", "993");
    }
    
    public void setProxy(final String host, final int port) {
        properties.setProperty("http.proxyHost", host);
        properties.setProperty("http.proxyPort", String.valueOf(port));
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
    
    public Session openSession() {
        synchronized (this) {
            if (mailSession == null) {
                mailSession = Session.getInstance(properties, getAuthenticator());
            }
        }
        return mailSession;
    }

    private Authenticator getAuthenticator() {
        if (proxyCredentials != null) {
            return new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            proxyCredentials.getUsername(),
                            new String(proxyCredentials.getPasword()));
                }
            };
        }
        return null;
    }
    
}
