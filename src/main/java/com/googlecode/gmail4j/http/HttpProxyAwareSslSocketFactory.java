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
import java.net.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.test.TestConfigurer;

/**
 * HTTP Proxy aware {@link SSLSocketFactory} implementation
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class HttpProxyAwareSslSocketFactory extends SSLSocketFactory 
    implements ProxyAware {

    private static final Log log = LogFactory.getLog(HttpProxyAwareSslSocketFactory.class);
    
    private Proxy proxy;
    
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
            , new UsernamePasswordCredentials("tomasv", "***"));
        ProxyClient.ConnectResponse resp = proxyClient.connect();
        return resp.getSocket();
    }
    
    public HttpProxyAwareSslSocketFactory(final Proxy proxy) {
        super();
        this.proxy = proxy;
    }
    
    public Socket createSocket(Socket s, String host, int port,
            boolean autoClose) throws IOException {
        log.debug("CreateSocket" + s);
        return s;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        // TODO Auto-generated method stub
        log.debug("getDefaultCipherSuites" );
        return null;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        log.debug("getSupportedCipherSuites" );
        return null;
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException,
            UnknownHostException {
        // TODO Auto-generated method stub
        log.debug("createSocket(String arg0, int arg1)" );
        return null;
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        // TODO Auto-generated method stub
        log.debug("createSocket(InetAddress arg0, int arg1)" );
        return null;
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3)
            throws IOException, UnknownHostException {
        // TODO Auto-generated method stub
        log.debug("createSocket(String arg0, int arg1, InetAddress arg2, int arg3)" );
        return null;
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2,
            int arg3) throws IOException {
        // TODO Auto-generated method stub
        log.debug("createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3" );
        return null;
    }

    public Proxy getProxy() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProxy(Proxy proxy) {
        // TODO Auto-generated method stub
        
    }

    public void setProxy(String proxyHost, int proxyPort) {
        // TODO Auto-generated method stub
        
    }

    public void setProxyCredentials(Credentials proxyCredentials) {
        // TODO Auto-generated method stub
        
    }

    public void setProxyCredentials(String username, char[] password) {
        // TODO Auto-generated method stub
        
    }


}
