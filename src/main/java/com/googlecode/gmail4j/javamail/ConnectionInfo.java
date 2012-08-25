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
package com.googlecode.gmail4j.javamail;

import java.io.Serializable;

/**
 * Connection event information object for {@link ImapConnectionHandler}.
 * <p>
 * ConnectionInfo object contains information related
 * to the current {@link Connection} service.
 * <p>
 *
 * @author Rajiv Perera &lt;rajivderas@gmail.com&gt;
 * @since 0.4
 */
public class ConnectionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Gmail Username for mail authentication.
     */
    private final String user;
    /**
     * Gmail SMTP/IMAP host for mail sending/receiving.
     */
    private final String host;
    /**
     * Gmail SMTP/IMAP port for mail sending/receiving.
     */
    private final int port;

    /**
     * Constructor with Gmail user, host and port.
     * 
     * @param user Gmail username
     * @param host Gmail SMTP/IMAP host for mail sending/receiving
     * @param port Gmail SMTP/IMAP port for mail sending/receiving
     */
    public ConnectionInfo(final String user, final String host, final int port) {
        this.user = user;
        this.host = host;
        this.port = port;
    }

    /**
     * Gets the {@link #user}
     *
     * @return username
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets the {@link #host}
     *
     * @return SMTP/IMAP host for mail sending/receiving
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the {@link #port}
     *
     * @return SMTP/IMAP port for mail sending/receiving
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder();
        try {
            toString.append("ConnectionInfo:{user:").append(getUser())
                    .append(";host:").append(getHost())
                    .append(";port:").append(getPort())
                    .append("} ");
            return toString.toString();
        } catch (final Exception e) {
            toString = null;
            return super.toString().concat("(e:")
                    .concat(e.getMessage()).concat(")");
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConnectionInfo other = (ConnectionInfo) obj;
        if ((this.user == null) ? (other.user != null)
                : !this.user.equals(other.user)) {
            return false;
        }
        if ((this.host == null) ? (other.host != null)
                : !this.host.equals(other.host)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.user != null ? this.user.hashCode() : 0);
        hash = 37 * hash + (this.host != null ? this.host.hashCode() : 0);
        hash = 37 * hash + this.port;
        return hash;
    }
}
