package com.googlecode.gmail4j.test;

import java.util.ResourceBundle;

import javax.swing.UIManager;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.util.LoginDialog;

public class TestConfigurer {

    private static final TestConfigurer instance = new TestConfigurer();

    ResourceBundle testProperties = ResourceBundle.getBundle("test");
    
    private Credentials gmailCredentials;
    
    private Credentials proxyCredentials;
    
    private TestConfigurer() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static TestConfigurer getInstance() {
        return instance;
    }
    
    public Credentials getGmailCredentials() {
        if (gmailCredentials == null) {
            String user = testProperties.getString("test.gmail.user");
            String pass = testProperties.getString("test.gmail.pass");
            if (user.equals("") || pass.equals("")) {
                gmailCredentials = LoginDialog.getInstance().show("Gmail login");
            } else {
                gmailCredentials = new Credentials(user, pass.toCharArray());
            }
        }
        return gmailCredentials;
    }
    
    public String getProxyHost() {
        return testProperties.getString("test.proxy.host");
    }
    
    public int getProxyPort() {
        return Integer.valueOf(testProperties.getString("test.proxy.port"));
    }
    
    public boolean useProxy() {
        return !getProxyHost().equals("");
    }
    
    public Credentials getProxyCredentials() {
        if (!useProxy()) {
            return null;
        }
        if (proxyCredentials == null) {
            String user = testProperties.getString("test.proxy.user");
            String pass = testProperties.getString("test.proxy.pass");
            if (!user.equals("") && pass.equals("")) {
                proxyCredentials = LoginDialog.getInstance()
                .show("Proxy login (" + getProxyHost() + ")");
            } else {
                proxyCredentials = new Credentials(user, pass.toCharArray());
            }
        }
        return proxyCredentials;
    }
    
}
