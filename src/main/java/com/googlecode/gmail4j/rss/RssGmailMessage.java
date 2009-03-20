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

public class RssGmailMessage implements GmailMessage {

    private SyndEntry rssEntry;
    
    private EmailAddress from = null;
    
    private StringBuilder toString = null;
    
    public RssGmailMessage(final SyndEntry rssEntry) {
        this.rssEntry = rssEntry;
    }
    
    public String getTitle() {
        return rssEntry.getTitle();
    }
    
    public EmailAddress getFrom() {
        if (from == null) {
            SyndPerson author = (SyndPerson) rssEntry.getAuthors().get(0);
            from = new EmailAddress(author.getName(), author.getEmail());
        }
        return from;
    }
    
    public String getLink() {
        return rssEntry.getLink();
    }
    
    public Date getDate() {
         return rssEntry.getPublishedDate();
    }
    
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
            .append(";date:").append(getDate())
            .append(";title:").append(getTitle())
            .append(";preview:").append(getPreview()).append(";}");
        return toString.toString();
    }

    public String getContentText() {
        throw new UnsupportedOperationException("RssGmailMessage has no " +
        		"support for getting full content text");
    }
     
}
