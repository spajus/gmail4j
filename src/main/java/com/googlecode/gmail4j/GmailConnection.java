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
package com.googlecode.gmail4j;

import com.googlecode.gmail4j.auth.Credentials;

/**
 * Gmail service connection. 
 * <p>
 * It stores Gmail account {@link Credentials}. Can be extended to use certain
 * protocols, proxies, etc.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailClient gmailClient = //.. some new instance of GmailClient
 *     GmailConnection conn = new GmailConnection();
 *     conn.setLoginCredentials("username", "password".toCharArray());
 *     gmailClient.setConnection(conn);
 * </pre></blockquote></p> 
 * 
 * @see GmailClient
 * @see Credentials
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.2
 */
public class GmailConnection {

    /**
     * Login credentials
     */
    protected Credentials loginCredentials;  
    
    /**
     * Argless constructor. 
     * <p>
     * You have to set {@link #loginCredentials} afterwards.
     * 
     * @see #setLoginCredentials(Credentials)
     * @see #setLoginCredentials(String, char[])
     */
    public GmailConnection() {
        //nothing to do
    }
    
    /**
     * Constructor that sets {@link #loginCredentials}
     * 
     * @param loginCredentials Gmail login credentials
     * @throws GmailException if credentials are not provided
     */
    public GmailConnection(final Credentials loginCredentials) {
        loginCredentials.validate();
        this.loginCredentials = loginCredentials;
    }
    
    /**
     * Convenience constructor that sets {@link #loginCredentials}
     * 
     * @param username Gmail username
     * @param password Gmail password
     * @throws GmailException if credentials are not provided
     */
    public GmailConnection(final String username, final char[] password) {
        this(new Credentials(username, password));
    }
    
    /**
     * Setter for {@link #loginCredentials}
     * 
     * @param loginCredentials Gmail login
     * @throws GmailException if provided {@link Credentials} are invalid
     */
    public void setLoginCredentials(final Credentials loginCredentials) {
        loginCredentials.validate();
        this.loginCredentials = loginCredentials;
    }

    /**
     * Wrapper method for setting {@link #loginCredentials}
     * 
     * @param username Gmail username
     * @param password Gmail password
     * @throws GmailException if provided {@link Credentials} are invalid
     */
    public void setLoginCredentials(final String username, 
            final char[] password) {
        setLoginCredentials(new Credentials(username, password));
    }
    
    @Override
    protected void finalize() throws Throwable {
        loginCredentials.dispose();
        super.finalize();
    }
    
}
