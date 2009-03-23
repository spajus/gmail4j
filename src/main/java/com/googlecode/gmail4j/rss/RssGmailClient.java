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

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.HttpGmailConnection;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Gmail RSS feed based {@link GmailClient} implementation.
 * <p>
 * Functionality is limited to unread message retrieval.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailClient client = new RssGmailClient();
 *     client.setLoginCredentials(new Credentials("user", "pass".toCharArray()));
 *     client.init();
 *     for (GmailMessage message : client.getUnreadMessages()) {
 *         System.out.println(message.getFrom() + ": " + message.getTitle());
 *     }
 * </pre></blockquote>
 * 
 * @see RssGmailMessage
 * @see GmailClient
 * @see Credentials
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public class RssGmailClient extends GmailClient {
    
    /**
     * Gmail feed URL. It should remain as is, however you can set a new value.
     * 
     * @see #setGmailFeedUrl(String)
     */
    private String gmailFeedUrl = "https://mail.google.com/mail/feed/atom";

    /**
     * Gets {@link #gmailFeedUrl}
     * 
     * @return Gmail feed URL
     */
    public String getGmailFeedUrl() {
        return gmailFeedUrl;
    }

    /**
     * Sets {@link #gmailFeedUrl}
     * <p>
     * Use it only if you would like to use another Gmail feed URL than the 
     * default <a href="https://mail.google.com/mail/feed/atom">
     * https://mail.google.com/mail/feed/atom</a>.
     * 
     * @param gmailFeedUrl New Gmail feed URL
     * @throws GmailException if something goes wrong
     */
    public void setGmailFeedUrl(final String gmailFeedUrl) {
        this.gmailFeedUrl = gmailFeedUrl;
    }
    
    @Override
    public List<GmailMessage> getUnreadMessages() {
        final List<GmailMessage> messages = new ArrayList<GmailMessage>();
        try {
            HttpGmailConnection c = getGmailConnection();
            c.setUrl(gmailFeedUrl);
            final URLConnection con = c.openConnection();
            final SyndFeedInput feedInput = new SyndFeedInput();
            final SyndFeed gmail = feedInput.build(new XmlReader(con));
            for (final Object entry : gmail.getEntries()) {
                if (entry instanceof SyndEntry) {
                    messages.add(new RssGmailMessage((SyndEntry) entry));
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Got " + messages.size() + " new messages.");
            }
        } catch (final Exception e) {
            throw new GmailException("Failed getting unread messages", e);
        }
        return messages;
    }
    
    private HttpGmailConnection getGmailConnection() {
        if (connection instanceof HttpGmailConnection) {
            return (HttpGmailConnection) connection;
        } 
        throw new GmailException("RssGmailClient requires HttpGmailConnection!");
    }
    
}
