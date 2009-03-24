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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.rss.RssGmailClient;

/**
 * Abstract Gmail client.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailConnection connection = //...instance of GmailConnection
 *     GmailClient client = new RssGmailClient();
 *     client.setConnection(connection);
 *     for (GmailMessage message : client.getUnreadMessages()) {
 *         System.out.println(message.getFrom() + ": " + message.getTitle());
 *     }
 * </pre></blockquote>
 * 
 * @see RssGmailClient
 * @see GmailMessage
 * @see GmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public abstract class GmailClient {

    /**
     * Logger
     */
    protected final Log log = LogFactory.getLog(getClass());
    
    /**
     * Gmail Connection 
     * 
     * @see #setConnection(GmailConnection)
     */
    protected GmailConnection connection;  
    
    /**
     * Argless constructor for safe extending 
     */
    public GmailClient() {
        super();
    }
    
    /**
     * Sets Gmail #connection 
     * 
     * @param connection Gmail connection
     */
    public void setConnection(final GmailConnection connection) {
        this.connection = connection;
    }
    
    /**
     * Returns list of unread {@link GmailMessage} objects
     * 
     * @return List of unread messages
     */
    public abstract List<GmailMessage> getUnreadMessages();

}