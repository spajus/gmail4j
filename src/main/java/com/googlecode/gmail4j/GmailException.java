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
package com.googlecode.gmail4j;

/**
 * Gmail4j Exception
 * <p>
 * <code>GmailException</code> Extends {@link RuntimeException} to avoid 
 * unnecessary catching, so you have to catch it explicitly on demand.
 * 
 * @see RuntimeException
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.1
 */
public class GmailException extends RuntimeException {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -2919776925807264765L;

    /**
     * Constructor with error message
     * 
     * @param message Error message
     */
    public GmailException(final String message) {
        super(message);
    }
    
    /**
     * Constructor with error message and cause
     * 
     * @param message Error message
     * @param cause Cause
     */
    public GmailException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
