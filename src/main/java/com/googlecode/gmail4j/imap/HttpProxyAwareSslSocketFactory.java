package com.googlecode.gmail4j.imap;

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
import com.googlecode.gmail4j.http.ProxyAware;
import com.googlecode.gmail4j.test.TestConfigurer;

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
    
    @Override
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
