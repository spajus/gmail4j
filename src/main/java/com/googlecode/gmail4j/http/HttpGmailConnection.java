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

/**
 * {@link GmailConnection} implementation that uses HTTP transport. 
 * <p>
 * It is {@link Proxy} aware. Example use:
 * <p><blockquote><pre>
 *     GmailClient client = //...
 *     GmailConnection conn = new HttpGmailConnection(gmailUser, gmailPass);
 *     client.setConnection(conn);
 * </pre></blockquote><p>
 * When using with {@link Proxy}:
 * <p><blockquote><pre>
 *     GmailClient client = //...
 *     HttpGmailConnection conn = new HttpGmailConnection(gmailUser, gmailPass);
 *     conn.setProxy(proxyHost, proxyPort);
 *     conn.setProxyCredentials(proxyUser, proxyPass);
 *     client.setConnection(conn);
 * </pre></blockquote>
 * 
 * @see Credentials
 * @see GmailConnection
 * @see Proxy
 * @see ProxyAware
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.2
 */
public class HttpGmailConnection extends GmailConnection implements ProxyAware {
    
    /**
     * Argless constructor.
     * <p>
     * Set credentials manually with {@link #setLoginCredentials(Credentials)}
     * or {@link #setLoginCredentials(String, char[])}.
     */
    public HttpGmailConnection() {
        //nothing to do
    }
    
    /**
     * Constructor with Gmail {@link Credentials}
     * 
     * @param loginCredentials Gmail login
     * @throws GmailException if provided credentials are empty
     */
    public HttpGmailConnection(final Credentials loginCredentials) {
        super(loginCredentials);
    }
    
    /**
     * Convenience constructor with Gmail {@link Credentials}
     * 
     * @param username Gmail username
     * @param password Gmail password
     * @throws GmailException if provided credentials are empty
     */    
    public HttpGmailConnection(final String username, final char[] password) {
        super(username, password);
    }

    /**
     * HTTP connection URL
     * 
     * @see #setUrl(String)
     */
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
    
    /**
     * If this is set to true, HttpGmailConnection will skip a call to
     * <code>Authenticator.setDefault(new GmailHttpAuthenticator);</code>
     * <p>
     * Default is <code>false</code>
     * 
     * @see #useCustomAuthenticator(boolean)
     */
    private boolean useCustomAuthenticator = false;
    
    /**
     * Sets {@link #useCustomAuthenticator(boolean)} flag. By default it's 
     * set to <code>false</code>
     * 
     * @param use skip setting of default {@link Authenticator}?
     */
    public void useCustomAuthenticator(final boolean use) {
        useCustomAuthenticator = use;
    }
    
    /**
     * Sets {@link #url} for this HTTP connection
     * 
     * @param url URL of Gmail service you want to connect to
     * @throws GmailException if URL is malformed
     */
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
     * @see #useCustomAuthenticator
     * @return connection
     * @throws IOException if opening a connection fails
     */
    public URLConnection openConnection() {
        try {
            if (!useCustomAuthenticator) {
                Authenticator.setDefault(
                        new GmailHttpAuthenticator(loginCredentials, proxyCredentials));
            }
            if (proxy != null) {
                return url.openConnection(proxy); 
            } else {
                return url.openConnection();
            }
        } catch (final Exception e) {
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
