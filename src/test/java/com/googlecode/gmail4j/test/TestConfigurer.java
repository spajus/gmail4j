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
package com.googlecode.gmail4j.test;

import java.util.ResourceBundle;

import javax.swing.UIManager;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.util.LoginDialog;

/**
 * Test configurer.
 * <p>Reads src/test/resources/test.properties and uses them
 * for unit testing Gmail4J. Missing properties are queried in during execution
 * with modal dialogs.
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 */
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
