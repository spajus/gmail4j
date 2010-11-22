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

/**
 * Imap Gmail folder label name for {@link Folder}.
 * <p>
 * {@code ImapGmailLabel} enum contains default folder label names related
 * to the {@link ImapGmailClient}.
 * 
 * Default Gmail Imap folder label names:
 * <p><blockquote><pre>
 *     ImapGmailLabel.ALL_MAIL, 
 *     ImapGmailLabel.DRAFTS, 
 *     ImapGmailLabel.SENT_MAIL, 
 *     ImapGmailLabel.SPAM, 
 *     ImapGmailLabel.STARRED, 
 *     ImapGmailLabel.INBOX, 
 *     ImapGmailLabel.TRASH
 * </pre></blockquote><p>
 * 
 * @author Rajiv Perera &lt;rajivderas@gmail.com&gt;
 * @version $Id: ImapGmailLabel.java 42 2010-11-22 ‏‎19:29:12Z rajivderas@gmail.com $
 * @since 0.4
 */
public enum ImapGmailLabel {
    /**
     * Imap Gmail folder label name for {@code [Gmail]/All Mail}
     */
    ALL_MAIL("[Gmail]/All Mail"),
    /**
     * Imap Gmail folder label name for {@code [Gmail]/Drafts}
     */
    DRAFTS("[Gmail]/Drafts"),
    /**
     * Imap Gmail folder label name for {@code [Gmail]/Sent Mail}
     */
    SENT_MAIL("[Gmail]/Sent Mail"),
    /**
     * Imap Gmail folder label name for {@code [Gmail]/Spam}
     */
    SPAM("[Gmail]/Spam"),
    /**
     * Imap Gmail folder label name for {@code [Gmail]/Starred}
     */
    STARRED("[Gmail]/Starred"),
    /**
     * Imap Gmail folder label name for {@code INBOX}
     */
    INBOX("INBOX"),
    /**
     * Imap Gmail folder label name for {@code [Gmail]/Trash}
     */
    TRASH("[Gmail]/Trash");
   
    /**
     * Imap Gmail folder label name
     */
    private String name;

    /**
     * Constructor with Imap Gmail label name
     *
     * @param name Gmail Imap folder label name
     */
    private ImapGmailLabel(String name) {
        this.name = name;
    }

    /**
     * Gets the {@link #name}
     *
     * @return name of the Gmail Imap folder label.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}