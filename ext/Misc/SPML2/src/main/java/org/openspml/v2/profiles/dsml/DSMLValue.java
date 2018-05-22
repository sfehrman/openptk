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
/*
 * Copyright 2006 Tripod Technology Group, Inc.
 * All Rights Reserved.
 * Use is subject to license terms.
 */
package org.openspml.v2.profiles.dsml;

import org.openspml.v2.util.xml.XmlBuffer;

/**
 * <code>
 * <xsd:simpleType name="Value">
 *    <xsd:union memberTypes="xsd:string xsd:base64Binary xsd:anyURI"/>
 * </xsd:simpleType>
 * </code>
 *
 * @author Blaine Busler and Kent Spaulding
 */
public class DSMLValue {

    // <xsd:union memberTypes="xsd:string xsd:base64Binary xsd:anyURI"/>
    private String mValue = null;

    public DSMLValue(String value) {
        this.mValue = value;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }

    protected void toXML(String name, XmlBuffer buffer) {
        buffer.addStartTag(name, false);
        ////-- OpenPTK - prevent setting of null values
        if (mValue!=null){
            buffer.addContent(mValue);
        }
        ////--
        buffer.addEndTag(name, false);
    }

    protected void toXML(XmlBuffer buffer) {
        ////-- OpenPTK changed from value to values, should be able to control this
        toXML("values", buffer);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DSMLValue)) return false;

        final DSMLValue value = (DSMLValue) o;

        if (mValue != null ? !mValue.equals(value.mValue) : value.mValue != null) return false;

        return true;
    }

    public int hashCode() {
        return (mValue != null ? mValue.hashCode() : 0);
    }

    public String toString() {
        return mValue;
    }
}
