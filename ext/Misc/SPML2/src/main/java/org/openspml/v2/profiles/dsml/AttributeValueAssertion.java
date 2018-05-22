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

import org.openspml.v2.util.xml.XmlBuffer;

import java.util.List;
import java.util.Map;


/**
 * From DSML 2.0 - this is the base of assertion classes like
 * EqualityMatch.
 *
 * <pre>
 * &lt;xsd:complexType name="AttributeValueAssertion"&gt;
 *     &lt;xsd:sequence&gt;
 *         &lt;xsd:element name="value" type="DsmlValue"/&gt;
 *     &lt;/xsd:sequence&gt;
 *     &lt;xsd:attribute name="name" type="AttributeDescriptionValue" use="required"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 */
abstract class AttributeValueAssertion extends NamedFilterItem {

    private static final String code_id = "$Id: AttributeValueAssertion.java,v 1.10 2006/08/30 18:02:59 kas Exp $";

    // <element name="value" type="DsmlValue"/>
    private DSMLValue mValue = null;

    protected AttributeValueAssertion() { ; }
    
    protected void addSubclassElements(XmlBuffer buffer) throws DSMLProfileException {
        super.addSubclassElements(buffer);
        if (mValue != null) {
            mValue.toXML(buffer);
        }
    }

    public void parseXml(DSMLUnmarshaller um, Object e) throws DSMLProfileException {
        um.visitAttributeValueAssertion(this, e);
    }

    public AttributeValueAssertion(String name, String value) throws DSMLProfileException {
        setName(name);
        if (value != null)
            mValue = new DSMLValue(value);
    }

    public AttributeValueAssertion(String name, DSMLValue value) throws DSMLProfileException {
        setName(name);
        if (value != null)
            mValue = value;
    }

    public DSMLValue getValue() {
        return mValue;
    }

    public void setValue(DSMLValue value) {
        if (value != null)
            mValue = value;
    }

    protected boolean valueAssertion(String thisValue, String testValue) {
        return false;
    }

    public boolean matches(Map attrs) throws DSMLProfileException {

        List values = (List) attrs.get(getName());
        if (values != null) {
            // does a value in there equal our value?
            String thisValue = getValue().toString();
            for (int k = 0; k < values.size(); k++) {
                String value = (String) values.get(k);
                if (valueAssertion(thisValue, value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeValueAssertion)) return false;
        if (!super.equals(o)) return false;

        final AttributeValueAssertion attributeValueAssertion = (AttributeValueAssertion) o;

        if (mValue != null ? !mValue.equals(attributeValueAssertion.mValue) : attributeValueAssertion.mValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (mValue != null ? mValue.hashCode() : 0);
        return result;
    }

}