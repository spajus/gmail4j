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
package com.googlecode.gmail4j.util;

/**
 * Common constants used for internel development.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     store.getFolder(CommonConstants.GMAIL_INBOX);
 * </pre></blockquote>
 *
 * @author Rajiv Perera &lt;rajivderas@gmail.com&gt;
 * @version $Id: CommonConstants.java 37 2010-10-20 13:15:32Z rajiv.perera $
 * @since 0.4
 */
public class CommonConstants {

    public static final String GMAIL_EXTENSION = "@gmail.com";
    // used for messsage header information extraction tags
    public static final String MESSAGE_ID = "Message-ID";
    public static final String MESSAGE_SUBJECT = "Subject";
    public static final String MESSAGE_IN_REPLY_TO = "In-Reply-To";
    public static final String MESSAGE_REFERENCES = "References";
}
