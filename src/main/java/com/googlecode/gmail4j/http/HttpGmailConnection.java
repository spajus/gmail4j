package com.googlecode.gmail4j.http;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy.Type;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.auth.GmailHttpAuthenticator;

public class HttpGmailConnection extends GmailConnection {
    
    public HttpGmailConnection() {
    }
    
    public HttpGmailConnection(final Credentials loginCredentials) {
        super(loginCredentials);
    }
    
    public HttpGmailConnection(final String username, final char[] password) {
        super(username, password);
    }

    private URL url;
    
    /**
     * HTTP Proxy for making a connection.
     * 
     * @see #setProxy(Proxy)
     * @see #setProxy(String, int)
     * @see #getProxy()
     */
    private Proxy proxy = null;    
    
    /**
     * HTTP Proxy {@link Credentials}
     * 
     * @see #proxy
     * @see #setProxyCredentials(Credentials)
     * @see #setProxyCredentials(String, char[])
     */
    private Credentials proxyCredentials = null;
    
    public void setUrl(final String url) {
        try {
            this.url = new URL(url);
        } catch (final MalformedURLException e) {
            throw new GmailException("Failed creating Gmail connection", e);
        }
    }
    
    /**
     * Opens {@link URLConnection} for getting data from Gmail RSS.
     * <p>
     * May use {@link Proxy} if one is defined
     * 
     * @see #proxy
     * @return connection
     * @throws IOException if opening a connection fails
     */
    public URLConnection openConnection() {
        Authenticator.setDefault(
                new GmailHttpAuthenticator(loginCredentials, proxyCredentials));
        try {
            if (proxy != null) {
                return url.openConnection(proxy); 
            } else {
                return url.openConnection();
            }
        } catch (final IOException e) {
            throw new GmailException("Failed opening Gmail connection", e);
        }
    }
    
    /**
     * Sets the {@link #proxy}
     * 
     * @param proxy Proxy for RSS connection
     */
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }
    
    /**
     * A convenience method for setting HTTP {@link #proxy}
     * 
     * @param proxyHost Proxy host
     * @param proxyPort Proxy port
     */
    public void setProxy(final String proxyHost, final int proxyPort) {
        this.proxy = new Proxy(Type.HTTP, InetSocketAddress
                .createUnresolved(proxyHost, proxyPort));
    }
    
    /**
     * Sets {@link #proxyCredentials}
     * 
     * @param proxyCredentials Proxy authentication
     */
    public void setProxyCredentials(final Credentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }
    
    /**
     * A convenience method for setting {@link #proxyCredentials}
     * 
     * @param username Proxy auth username
     * @param password Proxy auth password
     */
    public void setProxyCredentials(final String username, final char[] password) {
        setProxyCredentials(new Credentials(username, password));
    }
    
    /**
     * Gets the {@link #proxy}
     * 
     * @return Proxy or null if unavailable
     */
    public Proxy getProxy() {
        return this.proxy;
    }
    
    @Override
    protected void finalize() throws Throwable {
        loginCredentials.dispose();
        if (proxyCredentials != null) {
            proxyCredentials.dispose();
        }
        super.finalize();
    }

}
