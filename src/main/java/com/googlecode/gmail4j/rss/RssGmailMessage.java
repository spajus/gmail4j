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
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.1
 */
public class RssGmailMessage extends GmailMessage {

    /**
     * <a href="https://rome.dev.java.net/">ROME</a> SyndEntry object with 
     * Gmail message content.
     */
    private final SyndEntry rssEntry;
    
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
            final SyndPerson author = (SyndPerson) rssEntry.getAuthors().get(0);
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

    @Override
    public int getMessageNumber() {
        throw new UnsupportedOperationException("RssGmailClient is not " +
        		"capable of getting message number.");
    }
}
