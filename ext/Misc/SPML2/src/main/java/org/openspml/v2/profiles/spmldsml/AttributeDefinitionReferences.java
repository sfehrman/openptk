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
package org.openspml.v2.profiles.spmldsml;

import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

//<xsd:complexType name="AttributeDefinitionReferencesType">
//     <complexContent>
//        <extension base="spml:ExtensibleType">
//            <xsd:sequence>
//                <xsd:element name="attributeDefinitionReference" type="spmldsml:AttributeDefinitionReferenceType" minOccurs="0" maxOccurs="unbounded"/>
//            </xsd:sequence>
//        </extension>
//    </complexContent>
//</xsd:complexType>

public class AttributeDefinitionReferences extends ExtensibleElement {

    private static final String code_id = "$Id: AttributeDefinitionReferences.java,v 1.3 2006/08/30 18:02:59 kas Exp $";

    // <xsd:element name="attributeDefinitionReference" type="spmldsml:AttributeDefinitionReferenceType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_attributeDefinitionReference = new ArrayListWithType(AttributeDefinitionReference.class);

    public AttributeDefinitionReferences() { ; }

    public AttributeDefinitionReferences(AttributeDefinitionReference[] references) {
        if (references != null) {
            m_attributeDefinitionReference.addAll(Arrays.asList(references));
        }
    }

    public AttributeDefinitionReference[] getAttributeDefinitionReferences() {
        return (AttributeDefinitionReference[])
                m_attributeDefinitionReference.toArray(
                        new AttributeDefinitionReference[m_attributeDefinitionReference.size()]);
    }

    public void addAttributeDefinitionReference(AttributeDefinitionReference reference) {
        assert (reference != null);
        m_attributeDefinitionReference.add(reference);
    }

    public boolean removeAttributeDefinitionReference(AttributeDefinitionReference reference) {
        assert (reference != null);
        return m_attributeDefinitionReference.remove(reference);
    }

    public void clearAttributeDefinitionReferences() {
        m_attributeDefinitionReference.clear();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeDefinitionReferences)) return false;
        if (!super.equals(o)) return false;

        final AttributeDefinitionReferences attributeDefinitionReferences = (AttributeDefinitionReferences) o;

        if (m_attributeDefinitionReference != null ? !m_attributeDefinitionReference.equals(
                attributeDefinitionReferences.m_attributeDefinitionReference) : attributeDefinitionReferences.m_attributeDefinitionReference != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_attributeDefinitionReference != null ? m_attributeDefinitionReference.hashCode() : 0);
        return result;
    }
}
