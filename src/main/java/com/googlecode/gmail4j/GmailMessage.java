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

import java.util.Date;
import java.util.List;

import com.googlecode.gmail4j.rss.RssGmailClient;

/**
 * Gmail message interface. 
 * <p>
 * Implementations that do not provide some functionality (i.e. 
 * {@link #getContentText()}) throw {@link UnsupportedOperationException}
 * for those methods by default. 
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailClient client = //...instantate
 *     //configure the client...
 *     for (GmailMessage message : client.getUnreadMessages()) {
 *         System.out.println(message.getFrom());
 *         System.out.println(message.getDate());
 *         System.out.println(message.getTitle());
 *         System.out.println(message.getPreview());
 *         System.out.println(message.getLink());
 *         System.out.println(message.getContentText());
 *     }
 * </pre></blockquote></p>
 * 
 * @see GmailMessage
 * @see RssGmailClient
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public abstract class GmailMessage {

    /**
     * Gets Gmail message subject
     * 
     * @return Message subject
     */
    public abstract String getSubject();

    /**
     * Gets the {@link Date} this message was sent
     * 
     * @return Send date
     */
    public abstract Date getSendDate();

    /**
     * Gets the sender {@link EmailAddress}
     * 
     * @return Sender email address
     */
    public abstract EmailAddress getFrom();
    
    /**
     * Gets a list of message "To:" recipient {@link EmailAddress}<code>es</code>
     * 
     * @return list of message "To:" recipient email addresses
     * @throws UnsupportedOperationException if implementation does not provide
     *         this functionality
     */
    public List<EmailAddress> getTo() {
        throw new UnsupportedOperationException("This GmailMessage " +
        		"implementation does not provide getTo()");
    }
    
    /**
     * Gets a list of message "Cc:" recipient {@link EmailAddress}<code>es</code>
     * 
     * @return list of message "Cc:" recipient email addresses
     * @throws UnsupportedOperationException if implementation does not provide
     *         this functionality
     */    
    public List<EmailAddress> getCc() {
        throw new UnsupportedOperationException("This GmailMessage " +
                "implementation does not provide getCc()");
    }

    /**
     * Gets a direct link to this Gmail message
     * 
     * @return Web link to this message
     * @throws UnsupportedOperationException if implementation does not provide
     *         this functionality
     */
    public String getLink() {
        throw new UnsupportedOperationException("This GmailMessage " +
                "implementation does not provide getLink()");
    }

    /**
     * Gets a content text preview for this message
     * 
     * @return Text preview for this message
     * @throws UnsupportedOperationException if implementation does not provide
     *         this functionality
     */    
    public String getPreview() {
        throw new UnsupportedOperationException("This GmailMessage " +
                "implementation does not provide getPreview()");
    }
    
    /**
     * Gets a content text for this message
     * 
     * @return Text content for this message
     * @throws UnsupportedOperationException if implementation does not provide
     *         this functionality
     */        
    public String getContentText() {
        throw new UnsupportedOperationException("This GmailMessage " +
                "implementation does not provide getContentText()");
    }

}