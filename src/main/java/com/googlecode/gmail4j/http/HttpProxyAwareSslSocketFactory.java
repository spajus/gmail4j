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
package com.googlecode.gmail4j.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.Proxy.Type;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.auth.Credentials;

/**
 * HTTP Proxy aware {@link SSLSocketFactory} implementation.
 * <p>
 * Used for making JavaMail work through HTTP Proxy.
 * 
 * FIXME incomplete!
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class HttpProxyAwareSslSocketFactory extends SSLSocketFactory 
    implements ProxyAware {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(
            HttpProxyAwareSslSocketFactory.class);
    
    /**
     * Proxy to go through
     */
    private Proxy proxy;
    
    /**
     * Proxy username/password
     */
    private Credentials proxyCredentials;
    
    @Override
    public Socket createSocket() throws IOException {
        //FIXME won't work..
        log.debug("Creating socket! with proxy: " + proxy.address());
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        ProxyClient proxyClient = new ProxyClient();
        proxyClient.getHostConfiguration().setHost("imap.gmail.com", 993);
        proxyClient.getHostConfiguration().setProxy(addr.getHostName(), 
                addr.getPort());
        if (proxyCredentials != null) {
            proxyClient.getState().setProxyCredentials(
                    AuthScope.ANY, 
                    new UsernamePasswordCredentials(proxyCredentials.getUsername(), 
                        new String(proxyCredentials.getPasword())));
        }
        log.debug("Trying to connect to proxy");
        ProxyClient.ConnectResponse resp = proxyClient.connect();
        if (resp.getConnectMethod().getStatusCode() != HttpStatus.SC_OK) {
            log.error("Failed to connect. " + resp.getConnectMethod().getStatusLine());
            throw new GmailException("Failed connecting to IMAP through proxy: " 
                    + resp.getConnectMethod().getStatusLine());
        }
        log.debug("Connected, returning socket");
        return resp.getSocket();
    }
    
    /**
     * Constructor with {@link Proxy} and Proxy {@link Credentials}
     * 
     * @param proxy Proxy settings
     * @param proxyCredentials Proxy credentials (null if none)
     */
    public HttpProxyAwareSslSocketFactory(final Proxy proxy, 
            final Credentials proxyCredentials) {
        super();
        this.proxy = proxy;
        this.proxyCredentials = proxyCredentials;
    }
    
    public Socket createSocket(final Socket s, final String host, int port,
            boolean autoClose) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException,
            UnknownHostException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
            throws IOException, UnknownHostException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Socket createSocket(final InetAddress address, final int port, 
            final InetAddress address2,
            int port2) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    public void setProxy(final String proxyHost, final int proxyPort) {
        this.proxy = new Proxy(Type.HTTP, InetSocketAddress
                .createUnresolved(proxyHost, proxyPort));
    }

    public void setProxyCredentials(final Credentials proxyCredentials) {
        proxyCredentials.validate();
        this.proxyCredentials = proxyCredentials;
    }

    public void setProxyCredentials(final String username, final char[] password) {
        setProxyCredentials(new Credentials(username, password));
    }

}
