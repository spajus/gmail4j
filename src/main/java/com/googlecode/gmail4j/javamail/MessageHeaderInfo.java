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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Message Header information object for {@link JavaMailGmailMessage}.
 * <p>
 * MessageHeaderInfo object contains information related
 * to the matching headers from the {@link JavaMailGmailMessage}.
 * <p>
 *
 * @author Rajiv Perera &lt;rajivderas@gmail.com&gt;
 * @version $Id: MessageHeaderInfo.java 42 2010-11-02 ‏‎10:51:15Z rajivderas@gmail.com $
 * @since 0.4
 */
public class MessageHeaderInfo implements Serializable {

    /**
     * Gmail Message-ID for unique mail identification.
     */
    private String messageId;
    /**
     * Gmail message Subject of the incoming message.
     */
    private String subject;
    /**
     * Gmail conversation Message-ID References for the incoming message.
     */
    private List<String> references;
    /**
     * Gmail conversation In-Reply-To Message-ID for the incoming message.
     */
    private String inReplyTo;

    /**
     * Constructor with Gmail messageId
     *
     * @param messageId Gmail Message-ID for unique mail identification.
     */
    public MessageHeaderInfo(String messageId) {
        this.references = new ArrayList<String>();
        this.messageId = messageId;
    }

    /**
     * Sets {@link #subject}.
     * <p>
     * @param subject Gmail message Subject of the incoming message.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Sets {@link #inReplyTo}.
     * <p>
     * @param inReplyTo Gmail conversation In-Reply-To Message-ID for the
     * incoming message.
     */
    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    /**
     * Adds {@link #references}.
     * <p>
     * @param reference Gmail conversation Message-ID Reference for the
     * incoming message.
     */
    public void addReferences(String reference) {
        references.add(reference);
    }

    /**
     * Gets the {@link #messageId}
     *
     * @return Message-ID
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the {@link #subject}
     *
     * @return message Subject of the incoming message.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the {@link #inReplyTo}
     *
     * @return conversation In-Reply-To Message-ID for the incoming message.
     */
    public String getInReplyTo() {
        return inReplyTo;
    }

    /**
     * Gets the {@link #references}
     *
     * @return conversation Message-ID References for the incoming message.
     */
    public List<String> getReferences() {
        return (references == null ? Collections.<String>emptyList()
                : new ArrayList<String>(references));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageHeaderInfo other = (MessageHeaderInfo) obj;
        if ((this.messageId == null) ? (other.messageId != null)
                : !this.messageId.equals(other.messageId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.messageId != null
                ? this.messageId.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "MessageHeaderInfo{" + "messageId=" + messageId
                + "subject=" + subject + "references=" + references
                + "inReplyTo=" + inReplyTo + '}';
    }
}
