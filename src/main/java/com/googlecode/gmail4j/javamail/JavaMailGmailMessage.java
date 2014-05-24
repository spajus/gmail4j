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
package com.googlecode.gmail4j.javamail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailAttachment;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.util.Constants;
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
 * <p>
 * To get full power from JavaMail gmail message you should do this:
 * <p><blockquote><pre>
 *     JavaMailGmailMessage message = (JavaMailGmailMessage) gmailMessage; // cast from GmailMessage 
 *     Message rawJavaMailMessage = message.getMessage();
 *     // now use the full power of JavaMail to fit your needs
 * </pre></blockquote></p> 
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.3
 */
public class JavaMailGmailMessage extends GmailMessage {
      
    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(JavaMailGmailMessage.class);
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
    
    /**
     * Gets plain text version of the content. Has some limitations - won't 
     * handle nested attachments well.
     * 
     * @return String representation of the message
     */
    @Override
    public String getContentText() {
        try {
            Object content = source.getContent();
            StringBuilder result = new StringBuilder();
            if (content instanceof String) {
                result.append(content);
            } else if (content instanceof Multipart) {
                Multipart parts = (Multipart) content;
                for (int i = 0; i < parts.getCount(); i++) {
                    BodyPart part = parts.getBodyPart(i);
                    if (part.getContent() instanceof String) {
                        result.append(part.getContent());
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            throw new GmailException("Failed getting text content from " +
                    "JavaMailGmailMessage. You could try handling " +
                    "((JavaMailGmailMessage).getMessage()) manually", e);
        }
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
        String text = getContentText();
        if (text.length() > Constants.PREVIEW_LENGTH) {
            return text.substring(0, Constants.PREVIEW_LENGTH - 3) + "...";
        }
        return text;
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

    @Override
    public int getMessageNumber() {
        try {
            return source.getMessageNumber();
        } catch (final Exception e) {
            throw new GmailException("Failed getting message number", e);
        }
    }

    @Override
    public MessageHeaderInfo getMessageHeaderInfo() {
        MessageHeaderInfo headerInfo = null;
        try {
            Map<String, String> registry = new HashMap<String, String>();

            // message header tags used to get header information
            String[] headers = new String[]{
                Constants.MESSAGE_ID,
                Constants.MESSAGE_SUBJECT,
                Constants.MESSAGE_IN_REPLY_TO,
                Constants.MESSAGE_REFERENCES};

            @SuppressWarnings("unchecked")
            Enumeration<Header> matchingHeaders =
                    source.getMatchingHeaders(headers);

            while (matchingHeaders.hasMoreElements()) {
                Header header = matchingHeaders.nextElement();
                registry.put(header.getName(), header.getValue());
            }

            if (!registry.isEmpty()) {
                String messageId = registry.get(Constants.MESSAGE_ID);
                String subject = registry.get(Constants.MESSAGE_SUBJECT);
                String inReplyTo = registry.get(Constants.MESSAGE_IN_REPLY_TO);
                String references = registry.get(Constants.MESSAGE_REFERENCES);

                if (messageId != null) {
                    headerInfo = new MessageHeaderInfo(messageId);
                    headerInfo.setSubject(subject);
                    headerInfo.setInReplyTo(inReplyTo);

                    if (references != null) {
                        StringTokenizer st = new StringTokenizer(references, "\n");
                        while (st.hasMoreTokens()) {
                            String reference = st.nextToken().trim();
                            headerInfo.addReferences(reference);
                        }
                    }
                }
            }
        } catch (final Exception e) {
            throw new GmailException("Failed getting message header information", e);
        }
        return headerInfo;
    }
    
    @Override
    public void addAttachement(File file) {
        try {
            /* Check if email has already some contents. */
            Object content;
            try {
                content = this.source.getContent();
            } catch (IOException e) {
                /* If no content, then content is null.*/
                content = null;
                log.warn("Email content is empty.", e);
            }
            if (content != null) {
                if (content instanceof String) {
                    /* This message is not multipart yet. Change it to multipart. */
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText((String)this.source.getContent());
                    
                    Multipart multipart = new MimeMultipart();
                    multipart.addBodyPart(messageBodyPart);
                    this.source.setContent(multipart);
                }
            }
            else {
                /* No content, then create initial multipart content. */
                Multipart multipart = new MimeMultipart();
                this.source.setContent(multipart);
            }
            
            /* Get current content. */
            Multipart multipart = (Multipart)this.source.getContent();
            /* add attachment as a new part. */
            MimeBodyPart attachementPart = new MimeBodyPart();
            DataSource fileSource = new FileDataSource(file);
            DataHandler fileDataHandler = new DataHandler(fileSource);
            attachementPart.setDataHandler(fileDataHandler);
            attachementPart.setFileName(file.getName());
            multipart.addBodyPart(attachementPart);
        } catch (Exception e) {
            throw new GmailException("Failed to add attachement", e);
        }
    }

    @Override
    public List<GmailAttachment> getAttachements() {
        List<GmailAttachment> result = new ArrayList<GmailAttachment>(); 
        
        try {
            Object content = this.source.getContent();
             if (content instanceof Multipart) {
                 Multipart multipart = (Multipart)content;
                 for(int i = 0; i < multipart.getCount(); i++) {
                     Part bodyPart = multipart.getBodyPart(i);
                     if (bodyPart.getDisposition() != null) {
                         if (bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                             result.add(new GmailAttachment(i, bodyPart.getFileName(), bodyPart.getContentType(), bodyPart.getInputStream()));
                         }
                     }
                 } 
             }
        } catch (Exception e) {
            throw new GmailException("Failed to get attachements", e);
        }
        
        return result;
    }
    
    @Override
    public GmailAttachment getAttachment(int partIndex) {
        GmailAttachment result = null;
        
        try {
            Object content = this.source.getContent();
             if (content instanceof Multipart) {
                 Multipart multipart = (Multipart)content;
                 Part bodyPart = multipart.getBodyPart(partIndex);
                 if (bodyPart.getDisposition() != null) {
                     if (bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                         result = new GmailAttachment(partIndex, bodyPart.getFileName(), bodyPart.getContentType(), bodyPart.getInputStream());
                     }
                 } 
             }
             else {
                 throw new GmailException("Failed to get attachement with partIndex :" + partIndex);
             }
        } catch (Exception e) {
            throw new GmailException("Failed to get attachement with partIndex :" + partIndex, e);
        }
        
        return result;
    }
    
}
