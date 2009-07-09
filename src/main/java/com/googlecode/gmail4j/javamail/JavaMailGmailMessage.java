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
package com.googlecode.gmail4j.javamail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;

/**
 * <a href="http://java.sun.com/products/javamail/">JavaMail</a> implementation 
 * of {@link GmailMessage}
 * <p>
 * Example: Send a simple message:
 * <p><blockquote><pre>
 *     GmailClient client = new ImapGmailClient();
 *     //configure the client...
 *     GmailMessage message = new JavaMailGmailMessage();
 *     message.setSubject("Subject");
 *     message.setContentText("Content");
 *     message.addTo(new EmailAddress("j.smith@example.com");
 *     client.send(message);
 * </pre></blockquote></p> 
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

    /**
     * Gets the {@link #source} {@link Message}
     * 
     * @return source message
     */
    public Message getMessage() {
        return source;
    }
    
    @Override
    public void addTo(final EmailAddress to) {
        try {
            if (to.hasName()) {
                source.addRecipient(RecipientType.TO, 
                        new InternetAddress(to.getEmail(), to.getName()));
            } else {
                source.addRecipient(RecipientType.TO, 
                        new InternetAddress(to.getEmail()));
            }
        } catch (final Exception e) {
            throw new GmailException("Failed adding To recipient", e);
        }
    }
    
    @Override
    public List<EmailAddress> getTo() {
        try {
            return getAddresses(RecipientType.TO);
        } catch (final Exception e) {
            throw new GmailException("Failed getting List of To recipients", e);
        }
    }
    
    @Override
    public List<EmailAddress> getCc() {
        try {
            return getAddresses(RecipientType.CC);
        } catch (final Exception e) {
            throw new GmailException("Failed getting List of Cc recipients", e);
        }
    }    
    
    /**
     * Gets a {@link List} of {@link EmailAddress} by {@link RecipientType}
     * 
     * @param type Recipient type
     * @return List of Addresses
     * @throws MessagingException in case something is wrong
     */
    private List<EmailAddress> getAddresses(final RecipientType type) 
            throws MessagingException {
        final List<EmailAddress> addresses = new ArrayList<EmailAddress>();
        for (final Address addr : source.getRecipients(type)) {
            final InternetAddress temp = (InternetAddress) addr;
            addresses.add(new EmailAddress(temp.getPersonal(), temp.getAddress()));
        }
        return addresses;
    }

    @Override
    public void setFrom(final EmailAddress from) {
        try {
            if (from.hasName()) {
                source.setFrom(new InternetAddress(
                        from.getEmail(), from.getName()));
            } else {
                source.setFrom(new InternetAddress(from.getEmail()));
            }
        } catch (final Exception e) {
            throw new GmailException("Failed setting from address", e);
        }
    }
    
    @Override
    public EmailAddress getFrom() {
        if (from == null) {
            try {
                final InternetAddress f = (InternetAddress) source.getFrom()[0];
                from = new EmailAddress(f.getPersonal(), f.getAddress());
            } catch (final Exception e) {
                throw new GmailException("Failed getting from address", e);
            }
        }
        return from;
    }
    
    @Override
    public Date getSendDate() {
        try {
            return source.getSentDate();
        } catch (final Exception e) {
            throw new GmailException("Failed getting send date", e);
        }
    }
    
    @Override
    public void setSubject(final String subject) {
        try {
            source.setSubject(subject);
        } catch (final Exception e) {
            throw new GmailException("Failed setting subject", e);
        }
    }

    @Override
    public String getSubject() {
        try {
            return source.getSubject();
        } catch (final Exception e) {
            throw new GmailException("Failed getting message subject", e);
        }
    }
    
    @Override
    public void setContentText(final String contentText) {
        try {
            source.setText(contentText);
        } catch (final Exception e) {
            throw new GmailException("Failed settting content text", e);
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
        try {
            toString = new StringBuilder();
            toString.append("MailMessage:{from:").append(getFrom())
                .append(";sendDate:").append(getSendDate())
                .append(";subject:").append(getSubject())
                .append(";preview:").append(getPreview()).append(";}");
            return toString.toString();
        } catch (final Exception e) {
            toString = null;
            return super.toString().concat("(e:").concat(e.getMessage())
                    .concat(")");
        }
    }

}
