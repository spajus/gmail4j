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
package com.googlecode.gmail4j.rss;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.http.HttpGmailConnection;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Gmail RSS feed based {@link GmailClient} implementation.
 * <p>
 * It requires {@link HttpGmailConnection}. Functionality is limited to unread 
 * message retrieval.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailClient client = new RssGmailClient();
 *     client.setConnection(new HttpGmailConnection(gmailUser, gmailPass));
 *     for (GmailMessage message : client.getUnreadMessages()) {
 *         System.out.println(message.getFrom() + ": " + message.getTitle());
 *     }
 * </pre></blockquote>
 * 
 * @see RssGmailMessage
 * @see GmailClient
 * @see HttpGmailConnection
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
            //for ROME properties loader
            Thread.currentThread().setContextClassLoader(getClass()
                    .getClassLoader());
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
    
    /**
     * Gets {@link HttpGmailConnection} (casts it from parent 
     * {@link GmailConnection})
     * 
     * @return HttpGmailConnection if available
     * @throws GmailException if connection is not an instance of 
     *         HttpGmailConnection
     */
    private HttpGmailConnection getGmailConnection() {
        if (connection instanceof HttpGmailConnection) {
            return (HttpGmailConnection) connection;
        } 
        throw new GmailException("RssGmailClient requires HttpGmailConnection!");
    }

    @Override
    public void send(final GmailMessage message) {
        throw new UnsupportedOperationException("RssGmailClient is not " +
        		"capable of sending messages.");
    }
    
}
