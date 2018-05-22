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

import org.openspml.v2.msg.PrefixAndNamespaceTuple;

//<xsd:complexType name="ObjectClassDefinitionType">
//    <complexContent>
//       <extension base="spml:ExtensibleType">
//           <xsd:sequence>
//               <xsd:element name="memberAttributes" type="spmldsml:AttributeDefinitionReferencesType" minOccurs="0" maxOccurs="1"/>
//               <xsd:element name="superiorClasses" type="spmldsml:ObjectClassDefinitionReferencesType" minOccurs="0" maxOccurs="1"/>
//           </xsd:sequence>
//           <xsd:attribute name="name" type="xsd:string" use="required"/>
//           <xsd:attribute name="description" type="xsd:string" use="optional"/>
//       </extension>
//   </complexContent>
//</xsd:complexType>

public class ObjectClassDefinition extends ExtensibleElement {

    private static final String code_id = "$Id: ObjectClassDefinition.java,v 1.3 2006/08/30 18:02:59 kas Exp $";

    // <xsd:element name="memberAttributes" type="spmldsml:AttributeDefinitionReferencesType" minOccurs="0" maxOccurs="1"/>
    private AttributeDefinitionReferences m_memberAttributes;

    // <xsd:element name="superiorClasses" type="spmldsml:ObjectClassDefinitionReferencesType" minOccurs="0" maxOccurs="1"/>
    private ObjectClassDefinitionReferences m_superiorClasses;

    // <xsd:attribute name="name" type="xsd:string" use="required"/>
    private String m_name; // required

    // <xsd:attribute name="description" type="xsd:string" use="optional"/>
    private String m_description;

    public ObjectClassDefinition() { ; }

    public ObjectClassDefinition(String name,
                                 AttributeDefinitionReferences memberAttributes,
                                 ObjectClassDefinitionReferences superiorClasses,
                                 String description) {
        assert name != null;
        m_name = name;
        m_memberAttributes = memberAttributes;
        m_superiorClasses = superiorClasses;
        m_description = description;
    }

    public ObjectClassDefinition(String name,
                                 AttributeDefinitionReferences memberAttributes,
                                 ObjectClassDefinitionReferences superiorClasses) {
        this(name, memberAttributes, superiorClasses, null);
    }

    public ObjectClassDefinition(String name,
                                 AttributeDefinitionReferences memberAttributes) {
        this(name, memberAttributes, null, null);
    }

    public ObjectClassDefinition(String name) {
        this(name, null, null, null);
    }

    public AttributeDefinitionReferences getMemberAttributes() {
        return m_memberAttributes;
    }

    public void setMemberAttributes(AttributeDefinitionReferences memberAttributes) {
        m_memberAttributes = memberAttributes;
    }

    public ObjectClassDefinitionReferences getSuperiorClasses() {
        return m_superiorClasses;
    }

    public void setSuperiorClasses(ObjectClassDefinitionReferences superiorClasses) {
        m_superiorClasses = superiorClasses;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        assert name != null;
        m_name = name;
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return DSMLMarshallableCreator.staticGetNamespacesInfo();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectClassDefinition)) return false;
        if (!super.equals(o)) return false;

        final ObjectClassDefinition objectClassDefinition = (ObjectClassDefinition) o;

        if (m_description != null ? !m_description.equals(objectClassDefinition.m_description) : objectClassDefinition.m_description != null) return false;
        if (m_memberAttributes != null ? !m_memberAttributes.equals(objectClassDefinition.m_memberAttributes) : objectClassDefinition.m_memberAttributes != null) return false;
        if (m_name != null ? !m_name.equals(objectClassDefinition.m_name) : objectClassDefinition.m_name != null) return false;
        if (m_superiorClasses != null ? !m_superiorClasses.equals(objectClassDefinition.m_superiorClasses) : objectClassDefinition.m_superiorClasses != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_memberAttributes != null ? m_memberAttributes.hashCode() : 0);
        result = 29 * result + (m_superiorClasses != null ? m_superiorClasses.hashCode() : 0);
        result = 29 * result + (m_name != null ? m_name.hashCode() : 0);
        result = 29 * result + (m_description != null ? m_description.hashCode() : 0);
        return result;
    }
}
