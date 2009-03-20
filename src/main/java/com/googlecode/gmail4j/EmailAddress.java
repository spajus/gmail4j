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
 * A class that represents email address, i.e. &quot;John Smith 
 * &lt;j.smith@example.com&gt;&quot;
 * <p>
 * Example #1:
 * <p><blockquote><pre>
 *     EmailAddress tomas = new EmailAddress("Tomas", "tomas.varaneckas@gmail.com");
 *
 *     //Tomas
 *     String name = tomas.getName();
 *     
 *     //tomas.varaneckas@gmail.com
 *     String email = tomas.getEmail();
 *     
 *     //Tomas &lt;tomas.varaneckas@gmail.com&gt;
 *     String full = tomas.toString(); 
 * </pre></blockquote><p>
 * Example #2:
 * <p><blockquote><pre>
 *     EmailAddress john = new EmailAddress("j.smith@example.com");
 *
 *     //j.smith
 *     String name = john.getName();
 *     
 *     //j.smith@example.com
 *     String email = john.getEmail();
 *     
 *     //j.smith@example.com
 *     String full = tomas.toString(); 
 * </pre></blockquote><p>
 * 
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.1
 */
public class EmailAddress {

    private String email;
    private String name;
    
    public EmailAddress(final String email) {
        this(null, email);
    }
    
    public EmailAddress(final String name, final String email) {
        if (name != null && name.length() > 0) {
            this.name = name;
        }
        this.email = email;
    }
    
    public String getName() {
        return name == null ? email.replaceFirst("@.*", "") : name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return name == null ? email : name.concat(" <").concat(email).concat(">");
    }
}
