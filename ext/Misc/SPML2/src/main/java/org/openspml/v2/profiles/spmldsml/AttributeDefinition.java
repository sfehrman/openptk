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



// <xsd:complexType name="AttributeDefinitionType">
//    <complexContent>
//       <extension base="spml:ExtensibleType">
//           <xsd:attribute name="description" type="xsd:string" use="optional"/>
//           <xsd:attribute name="multivalued" type="xsd:boolean" use="optional" default="false"/>
//           <xsd:attribute name="type" type="xsd:string" use="optional" default="xsd:string"/>
//           <xsd:attribute name="name" type="xsd:string" use="required"/>
//       </extension>
//   </complexContent>
// </xsd:complexType>

public class AttributeDefinition extends ExtensibleElement {

    private static final String code_id = "$Id: AttributeDefinition.java,v 1.2 2006/05/15 23:31:00 kas Exp $";

    // <xsd:attribute name="description" type="xsd:string" use="optional"/>
    private String m_description;

    // <xsd:attribute name="multivalued" type="xsd:boolean" use="optional" default="false"/>
    private Boolean m_multivalued;

    // <xsd:attribute name="type" type="xsd:string" use="optional" default="xsd:string"/>
    private String m_type;

    // <xsd:attribute name="name" type="xsd:string" use="required"/>
    private String m_name; // required

    public AttributeDefinition() { ; }

    protected AttributeDefinition(String name, String type, String description, Boolean multivalued) {
        super();
        assert name != null;
        m_name = name;
        m_description = description;
        m_multivalued = multivalued;
        m_type = type;
    }

    public AttributeDefinition(String name, String type, String description, boolean multivalued) {
        this(name, type, description, new Boolean(multivalued));
    }

    public AttributeDefinition(String name, String type, String description) {
        this(name, type, description, null);
    }

    public AttributeDefinition(String name, String type) {
        this(name, type, null, null);
    }

    public AttributeDefinition(String name) {
        this(name, null, null, null);
    }

    public String getDescription() {
        return m_description;
    }

    public void setDescription(String description) {
        m_description = description;
    }

    public boolean isMultivalued() {
        if (m_multivalued == null)
            return false;
        return m_multivalued.booleanValue();
    }

    public void setMultivalued(boolean multivalued) {
        m_multivalued = new Boolean(multivalued);
    }

    public String getType() {
        return m_type;
    }

    public void setType(String type) {
        m_type = type;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        assert name != null;
        m_name = name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributeDefinition)) return false;
        if (!super.equals(o)) return false;

        final AttributeDefinition attributeDefinition = (AttributeDefinition) o;

        if (m_description != null ? !m_description.equals(attributeDefinition.m_description) : attributeDefinition.m_description != null) return false;
        if (m_multivalued != null ? !m_multivalued.equals(attributeDefinition.m_multivalued) : attributeDefinition.m_multivalued != null) return false;
        if (!m_name.equals(attributeDefinition.m_name)) return false;
        if (m_type != null ? !m_type.equals(attributeDefinition.m_type) : attributeDefinition.m_type != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_description != null ? m_description.hashCode() : 0);
        result = 29 * result + (m_multivalued != null ? m_multivalued.hashCode() : 0);
        result = 29 * result + (m_type != null ? m_type.hashCode() : 0);
        result = 29 * result + m_name.hashCode();
        return result;
    }
}
