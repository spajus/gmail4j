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
package com.googlecode.gmail4j.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.Proxy.Type;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.test.TestConfigurer;

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
     * Gmail credentials
     */
    private Credentials gmailCredentials;
    
    /**
     * Proxy username/password
     */
    private Credentials proxyCredentials;
    
    @Override
    public Socket createSocket() throws IOException {
        //FIXME doesn't work
        log.debug("Creating socket! with proxy: " + proxy.address());
        ProxyClient proxyClient = new ProxyClient();
        proxyClient.getHostConfiguration().setHost("nmap.gmail.com", 993);
        proxyClient.getHostConfiguration().setProxy("**", 123);
        proxyClient.getState().setCredentials(new AuthScope("172.16.1.99", 993),
                new UsernamePasswordCredentials(TestConfigurer.getInstance().getGmailCredentials().getUsername(), 
                        new String(TestConfigurer.getInstance().getGmailCredentials().getPasword())));
        proxyClient.getState().setProxyCredentials(AuthScope.ANY
            , new UsernamePasswordCredentials(proxyCredentials.getUsername(), 
                    new String(proxyCredentials.getPasword())));
        ProxyClient.ConnectResponse resp = proxyClient.connect();
        return resp.getSocket();
    }
    
    /**
     * Constructor with {@link Proxy}
     * 
     * @param proxy Proxy settings
     */
    public HttpProxyAwareSslSocketFactory(final Proxy proxy, 
            final Credentials gmailCredentials) {
        super();
        this.proxy = proxy;
        this.gmailCredentials = gmailCredentials;
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
