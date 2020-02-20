/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wsspi.webcontainer.servlet;

import javax.servlet.http.Cookie;

import com.ibm.ws.session.SameSiteCookie;
/**
 *
 */
public class ExtendedCookie extends Cookie {
    private SameSite sameSite;
    
    public ExtendedCookie(String name, String value) {
        super(name,value);
    }

    public void setSameSite(SameSite sameSite) {
        this.sameSite = sameSite;
    }
    
    public SameSite getSameSite() {
        return this.sameSite;
    }
    
    public static enum SameSite {
        LAX("Lax"),
        NONE("None"),
        STRICT("Strict"),
        UNKNOWN("Unknown");
        
        SameSite(String name) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return this.name;
        }
        
        /**
         * Any value passed to this method is compared to the SameSite ignoring case and 
         * the appropriate SameSite is returned. If no matches are found then UNKNOWN is
         * returned.
         * 
         * @param value - The value to check against the SameSiteCookie.
         * @return - The SameSite that matches the value.
         */
        public static SameSite get(String value) {
            if (LAX.toString().equalsIgnoreCase(value)) {
                return LAX;
            } else if (STRICT.toString().equalsIgnoreCase(value)) {
                return STRICT;
            } else if (NONE.toString().equalsIgnoreCase(value)) {
                return NONE;           
            }

            return UNKNOWN;
        }
    }
}
