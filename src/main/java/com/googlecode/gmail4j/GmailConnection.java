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
package com.googlecode.gmail4j;

import com.googlecode.gmail4j.auth.Credentials;

/**
 * Gmail service connection. 
 * <p>
 * It stores Gmail account {@link Credentials}. Can be extended to use certain
 * protocols, proxies, etc. 
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id: GmailException.java 6 2009-03-20 08:59:14Z tomas.varaneckas $
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
    }
    
    public GmailConnection(final Credentials loginCredentials) {
        loginCredentials.validate();
        this.loginCredentials = loginCredentials;
    }
    
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
    public void setLoginCredentials(final String username, final char[] password) {
        setLoginCredentials(new Credentials(username, password));
    }
    
}
