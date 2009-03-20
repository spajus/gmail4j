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
package com.googlecode.gmail4j.rss;

import java.util.Date;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailMessage;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndPerson;

/**
 * Read-only {@link GmailMessage} implementation procuded by 
 * {@link RssGmailClient}.
 * <p>
 * This implementation is based on <a href="https://rome.dev.java.net/">ROME</a> 
 * {@link SyndEntry}.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailClient rssGmailClient = new RssGmailClient();
 *     //configure the client...
 *     for (GmailMessage message : rssGmailClient.getUnreadMessages()) {
 *         System.out.println(message.getFrom());
 *         System.out.println(message.getSendDate());
 *         System.out.println(message.getSubject());
 *         System.out.println(message.getPreview());
 *         System.out.println(message.getLink());
 *     }
 * </pre></blockquote></p>
 * 
 * @see GmailMessage
 * @see RssGmailClient
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public class RssGmailMessage extends GmailMessage {

    /**
     * <a href="https://rome.dev.java.net/">ROME</a> SyndEntry object with 
     * Gmail message content.
     */
    private SyndEntry rssEntry;
    
    /**
     * Sender's {@link EmailAddress}
     */
    private EmailAddress from = null;
    
    /**
     * Cache for {@link #toString()}
     */
    private StringBuilder toString = null;
    
    /**
     * Package-protected constructor with {@link SyndEntry} source
     * 
     * @param rssEntry Source object
     */
    RssGmailMessage(final SyndEntry rssEntry) {
        this.rssEntry = rssEntry;
    }
    
    @Override
    public String getSubject() {
        return rssEntry.getTitle();
    }

    @Override
    public EmailAddress getFrom() {
        if (from == null) {
            SyndPerson author = (SyndPerson) rssEntry.getAuthors().get(0);
            from = new EmailAddress(author.getName(), author.getEmail());
        }
        return from;
    }
    
    @Override
    public String getLink() {
        return rssEntry.getLink();
    }
    
    @Override
    public Date getSendDate() {
         return rssEntry.getPublishedDate();
    }
    
    @Override
    public String getPreview() {
        return rssEntry.getDescription().getValue();
    }
    
    @Override
    public String toString() {
        if (toString != null) {
            return toString.toString();
        }
        toString = new StringBuilder();
        toString.append("MailMessage:{from:").append(getFrom())
            .append(";sendDate:").append(getSendDate())
            .append(";subject:").append(getSubject())
            .append(";preview:").append(getPreview()).append(";}");
        return toString.toString();
    }

}
