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
package com.googlecode.gmail4j.javamail;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;

/**
 * <a href="http://java.sun.com/products/javamail/">JavaMail</a> implementation 
 * of {@link GmailMessage}
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class JavaMailGmailMessage extends GmailMessage {

    /**
     * Original JavaMail {@link Message}
     */
    final Message source;
    
    /**
     * Cache for {@link #toString()}
     */
    private StringBuilder toString;
    
    /**
     * Sender's email address
     */
    private EmailAddress from;
    
    /**
     * Constructor with source {@link Message}
     * 
     * @param source JavaMail message with data
     */
    public JavaMailGmailMessage(final Message source) {
        this.source = source;
    }
    
    /**
     * Constructor that creates a new empty JavaMail {@link MimeMessage}
     */
    public JavaMailGmailMessage() {
        this.source = new MimeMessage((Session) null);
    }

    @Override
    public EmailAddress getFrom() {
        if (from == null) {
            try {
                final InternetAddress f = (InternetAddress) source.getFrom()[0];
                from = new EmailAddress(f.getAddress(), f.getPersonal());
            } catch (final MessagingException e) {
                throw new GmailException("Failed getting from address", e);
            }
        }
        return from;
    }

    @Override
    public Date getSendDate() {
        try {
            return source.getSentDate();
        } catch (final MessagingException e) {
            throw new GmailException("Failed getting send date", e);
        }
    }

    @Override
    public String getSubject() {
        try {
            return source.getSubject();
        } catch (final MessagingException e) {
            throw new GmailException("Failed getting message subject", e);
        }
    }
    
    @Override
    public String getPreview() {
        try {
            return source.getContent().toString();
        } catch (final Exception e) {
            throw new GmailException("Failed getting message preview", e);
        }
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
