package com.googlecode.gmail4j.message;

import java.util.Date;

import com.googlecode.gmail4j.client.Address;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndPerson;

public class RssGmailMessage implements GmailMessage {

    private SyndEntry rssEntry;
    
    private Address from = null;
    
    private StringBuilder toString = null;
    
    public RssGmailMessage(final SyndEntry rssEntry) {
        this.rssEntry = rssEntry;
    }
    
    public String getTitle() {
        return rssEntry.getTitle();
    }
    
    public Address getFrom() {
        if (from == null) {
            SyndPerson author = (SyndPerson) rssEntry.getAuthors().get(0);
            from = new Address(author.getName(), author.getEmail());
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
