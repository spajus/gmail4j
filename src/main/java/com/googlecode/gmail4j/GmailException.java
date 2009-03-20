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
package com.googlecode.gmail4j;

/**
 * Gmail4j Exception
 * <p>
 * <code>GmailException</code> Extends {@link RuntimeException} to avoid 
 * unnecessary catching, so you have to catch it explicitly on demand.
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
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
