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

import java.net.Authenticator;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.GmailHttpAuthenticator;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssGmailClient extends GmailClient {
    
    private String gmailFeedUrl = "https://mail.google.com/mail/feed/atom";

    public void init() {
        loginCredentials.validate();
        Authenticator.setDefault(new GmailHttpAuthenticator(loginCredentials));
    }

    public String getGmailFeedUrl() {
        return gmailFeedUrl;
    }

    public void setGmailFeedUrl(final String gmailFeedUrl) {
        this.gmailFeedUrl = gmailFeedUrl;
    }
    
    @Override
    public List<GmailMessage> getUnreadMessages() {
        final List<GmailMessage> messages = new ArrayList<GmailMessage>();
        try {
            final URL feedSource = new URL(gmailFeedUrl);
            final SyndFeedInput feedInput = new SyndFeedInput();
            final SyndFeed gmail = feedInput.build(new XmlReader(feedSource));
            for (Object entry : gmail.getEntries()) {
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

    @Override
    protected void finalize() throws Throwable {
        loginCredentials.dispose();
    }
    
}
