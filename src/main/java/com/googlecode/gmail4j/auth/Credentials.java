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
package com.googlecode.gmail4j.auth;

import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.util.LoginDialog;

/**
 * Login credentials containing username and password.
 * <p>
 * This class stores password as a <code>char[]</code> and allows you to dispose
 * the array via {@link #dispose()} to avoid sniffing.
 * <p>
 * There are several ways to construct your <code>Credentials</code>:
 * <p><blockquote><pre>
 *     String username = ...
 *     char[] password = ...
 *     Credentials login = new Credentials(username, password);
 *     //password char array is cleared automatically
 * </pre></blockquote><p>
 * Another approach:
 * <p><blockquote><pre>
 *     String username = ...
 *     char[] password = ...
 *     Credentials login = new Credentials();
 *     login.setUsername(username);
 *     login.setPassword(password)
 *     //password char array is cleared automatically
 * </pre></blockquote><p>
 * For testing purposes you can construct <code>Credentials</code> with help of 
 * {@link LoginDialog#show(String)}
 * <p><blockquote><pre>
 *     Credentials login = LoginDialog.show("Gmail Login");
 * </pre></blockquote><p>
 * There are more methods of interest: 
 * <ul><li>{@link #validate()} checks if
 * username and password were provided and throws {@link GmailException} in case
 * something is missing.</li>
 * <li>{@link #dispose()} clears the username and password to prevent further 
 * use.</li></ul>
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public final class Credentials {

    /**
     * Password stored as <code>char[]</code> to enable secure disposal 
     */
    private char[] password;

    /**
     * Username
     */
    private String username;

    /**
     * Argless constructor to enable setter based configuration.
     * <p>
     * You will have to manually call {@link #setUsername(String)} and 
     * {@link #setPassword(char[])} (nd optionally {@link #validate()}
     * afterwards;
     */
    public Credentials() {
        //nothing to do
    }

    /**
     * Constructor that sets {@link #username} and {@link #password}. Given
     * <code>password char[]</code> argument gets cleared for security purposes.
     * <p>
     * It also calls {@link #validate()} automatically to check if user/pass
     * is not empty. 
     * 
     * @param username Username
     * @param password Password
     */
    public Credentials(final String username, final char[] password) {
        setUsername(username);
        setPassword(password);
        validate();
    }
    
    /**
     * Validates existence of {@link #username} and {@link #password} properties.
     * 
     * @throws GmailException in case username and/or password are missing.
     */
    public void validate() {
        if (username == null || username.length() == 0 
                || password == null || password.length == 0) {
            throw new GmailException("No username and/or password provided");
        }
    }
 
    /**
     * Sets {@link #username}
     * 
     * @param username Username
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Sets {@link #password} and clears the given <code>char[]</code> argument
     * for security purposes.
     * 
     * @param password Password
     */
    public void setPassword(final char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
        disposePassword(password);
    }

    /**
     * Disposes a given <code>char[]</code>. A way to destroy password arguments.
     * 
     * @param password Password for disposal
     */
    private void disposePassword(final char[] password) {
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }
    }

    /**
     * Gets {@link #password}
     * @return A reference to password <code>char[]</code>
     */
    public char[] getPasword() {
        return this.password;
    }

    /**
     * Gets {@link #username}
     * 
     * @return Username
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Disposes current <code>Credentials</code> instance by clearing 
     * {@link #username} and {@link #password}.
     * <p>
     * Object can be reused again with new {@link #username} and 
     * {@link #password}
     * 
     * @see #disposePassword(char[])
     */
    public void dispose() {
        this.username = null;
        disposePassword(this.password);
        this.password = null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

}
