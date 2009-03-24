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
package com.googlecode.gmail4j.auth;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.http.HttpGmailConnection;

/**
 * Proxy aware HTTP {@link Authenticator} for {@link GmailConnection} 
 * implementations that require HTTP access to Gmail service. 
 * <p>
 * Activate it with {@link Authenticator#setDefault(Authenticator)}:
 * <p><blockquote><pre>
 *     Credentials login = new Credentials(username, password);
 *     Authenticator.setDefault(new GmailHttpAuthenticator(login));
 * </pre></blockquote><p>
 * When using with {@link Proxy}:
 * <p><blockquote><pre>
 *     Credentials login = new Credentials(username, password);
 *     Credentials proxyLogin = new Credentials(proxyUser, proxyPass);
 *     Authenticator.setDefault(new GmailHttpAuthenticator(login, proxyLogin));
 * </pre></blockquote><p>
 * {@link HttpGmailConnection} automatically activates this.
 * 
 * @see Authenticator
 * @see Credentials
 * @see HttpGmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public class GmailHttpAuthenticator extends Authenticator {
    
    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(GmailHttpAuthenticator.class);
    
    /**
     * Credentials given as response to <a href="http://mail.google.com">
     * mail.google.com</a> server request.
     */
    private final Credentials credentials;
    
    /**
     * Credentials given as response to {@link Proxy} authentication request.
     */
    private Credentials proxyCredentials;
    
    /**
     * Constructor that sets {@link #credentials}
     * 
     * @param credentials Gmail login
     */
    public GmailHttpAuthenticator(final Credentials credentials) {
        this.credentials = credentials;
    }
    
    /**
     * Constructor that sets {@link #credentials} and {@link #proxyCredentials}
     * 
     * @param credentials Gmail login
     * @param proxyCredentials Proxy authentication
     */
    public GmailHttpAuthenticator(final Credentials credentials, 
            final Credentials proxyCredentials) {
        this(credentials);
        this.proxyCredentials = proxyCredentials;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (log.isDebugEnabled()) {
            log.debug("Password authentication request: " 
                    + getRequestingPrompt());
        }
        if (getRequestorType().equals(RequestorType.PROXY) 
                && proxyCredentials != null) {
            log.debug("Proxy request detected, returning proxy credentials");
            return new PasswordAuthentication(proxyCredentials.getUsername(), 
                    proxyCredentials.getPasword());
        }
        if ("mail.google.com".equals(getRequestingHost())) {
            log.debug("Gmail request detected, returning login credentials");
            return new PasswordAuthentication(credentials.getUsername(), 
                    credentials.getPasword());
        }
        log.debug("Unknown authentication request, will return nothing");
        return null;
    }
    
}
