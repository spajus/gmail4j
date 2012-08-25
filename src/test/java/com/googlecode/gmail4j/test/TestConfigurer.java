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
package com.googlecode.gmail4j.test;

import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.junit.Ignore;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.util.LoginDialog;

/**
 * Test configurer.
 * <p>Reads src/test/resources/test.properties and uses them
 * for unit testing Gmail4J. Missing properties are queried in during execution
 * with modal dialogs.
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 */
@Ignore
public class TestConfigurer {

    /**
     * Singleton instance
     */
    private static final TestConfigurer instance = new TestConfigurer();

    /**
     * Test properties bundle (test.properties)
     */
    protected ResourceBundle testProperties = ResourceBundle.getBundle("test");
    
    /**
     * Gmail login credentials
     */
    private Credentials gmailCredentials;
    
    /**
     * Proxy login credentials
     */
    private Credentials proxyCredentials;
    
    /**
     * Private singleton constructor
     */
    private TestConfigurer() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Singleton instance getter
     * 
     * @return instance
     */
    public static TestConfigurer getInstance() {
        return instance;
    }
    
    /**
     * Gets Gmail credentials defined in test.properties or asks to enter them
     * 
     * @return Gmail Credentials
     */
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
    
    /**
     * Gets test recipient's email address
     * 
     * @return email address
     */
    public String getTestRecipient() {
        return testProperties.getString("test.email.recipient");
    }
    
    /**
     * Gets proxy host
     * 
     * @return host or null if not defined
     */
    public String getProxyHost() {
        return testProperties.getString("test.proxy.host");
    }
    
    /**
     * Gets proxy port 
     * 
     * @return port
     */
    public int getProxyPort() {
        return Integer.valueOf(testProperties.getString("test.proxy.port"));
    }
    
    /**
     * Tells if proxy is in use
     * 
     * @return true if proxy host is defined
     */ 
    public boolean useProxy() {
        return !getProxyHost().equals("");
    }
    
    /**
     * Gets Proxy server credentials
     * 
     * @return Credentials or null if not in use
     */
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
