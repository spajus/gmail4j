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

import java.net.Proxy;

import com.googlecode.gmail4j.auth.Credentials;

/**
 * Interface that provides way of setting and getting proxy and it's credentials.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailConnection conn = //some new GmailConnection
 *     if (conn instanceof ProxyAware) {
 *         ((ProxyAware) conn).setProxy("proxy.example.com", 8080);
 *         ((ProxyAware) conn).setProxyCredentials(proxyUser, proxyPass);
 *     }
 * </pre></blockquote></p>
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public interface ProxyAware {

    /**
     * Sets the {@link #proxy}
     * 
     * @param proxy Proxy for RSS connection
     */
    public void setProxy(final Proxy proxy);
    
    /**
     * Gets the {@link #proxy}
     * 
     * @return Proxy or null if unavailable
     */
    public Proxy getProxy();
    
    /**
     * A convenience method for setting HTTP {@link #proxy}
     * 
     * @param proxyHost Proxy host
     * @param proxyPort Proxy port
     */
    public void setProxy(final String proxyHost, final int proxyPort);
    
    /**
     * Sets {@link #proxyCredentials}
     * 
     * @param proxyCredentials Proxy authentication
     */
    public void setProxyCredentials(final Credentials proxyCredentials);
    
    /**
     * A convenience method for setting {@link #proxyCredentials}
     * 
     * @param username Proxy auth username
     * @param password Proxy auth password
     */
    public void setProxyCredentials(final String username, final char[] password);
}
