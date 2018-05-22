/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * 
 * This file is available and licensed under the following license:
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 
 * 
 * Neither the name of Sun Microsystems nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openspml.v2.profiles.dsml;



/**
 * This class is really a wrapper around String, but it
 * insists the String meets a regular expression.
 *
 * <pre>
 *  &lt;!-- ***** AttributeDescriptionValue ***** --&gt;
 *  &lt;xsd:simpleType name="AttributeDescriptionValue"&gt;
 *      &lt;xsd:restriction base="xsd:string"&gt;
 *          &lt;xsd:pattern value="((([0-2](\.[0-9]+)+)|([a-zA-Z]+([a-zA-Z0-9]|[-])*))(;([a-zA-Z0-9]|[-])+)*)"/&gt;
 *      &lt;/xsd:restriction&gt;
 *  &lt;/xsd:simpleType&gt;
 * </pre>
 */
class AttributeDescriptionValue {

    private static final String code_id = "$Id: AttributeDescriptionValue.java,v 1.1 2006/06/01 02:47:52 kas Exp $";

    private static final String REGEX = 
        "((([0-2](\\.[0-9]+)+)|([a-zA-Z]+([a-zA-Z0-9]|[-])*))(;([a-zA-Z0-9]|[-])+)*)";
            
    private String mName = null;

    protected AttributeDescriptionValue() {
        ;
    }
    
    protected AttributeDescriptionValue(String data) throws DSMLProfileException {
        setName(data);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) throws DSMLProfileException {
////-- OpenPTK:  Need to be able to selectively disable this check
////--        if (!name.matches(REGEX)) {
////--            throw new DSMLProfileException("The name, " + name + " is not valid. It must match:\n  " + REGEX);
////--        }
         mName = name;
    }

    public String toString() {
        return mName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeDescriptionValue)) return false;

        final AttributeDescriptionValue attributeDescriptionValue = (AttributeDescriptionValue) o;

        if (mName != null ? !mName.equals(attributeDescriptionValue.mName) : attributeDescriptionValue.mName != null) return false;

        return true;
    }

    public int hashCode() {
        return (mName != null ? mName.hashCode() : 0);
    }
}
