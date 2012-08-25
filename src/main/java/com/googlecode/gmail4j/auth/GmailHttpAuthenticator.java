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
package com.googlecode.gmail4j.auth;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.GmailConnection;

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
 * 
 * @see Authenticator
 * @see Credentials
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
