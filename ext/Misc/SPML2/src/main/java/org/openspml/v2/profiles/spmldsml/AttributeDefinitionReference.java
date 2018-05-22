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

import java.net.URI;

//<xsd:complexType name="AttributeDefinitionReferenceType">
//    <complexContent>
//       <extension base="spml:ExtensibleType">
//           <xsd:attribute name="schema" type="anyURI" use="optional" />
//           <xsd:attribute name="required" type="xsd:boolean" use="optional" default="false"/>
//           <xsd:attribute name="name" type="xsd:string" use="required"/>
//       </extension>
//   </complexContent>
//</xsd:complexType>

public class AttributeDefinitionReference extends ExtensibleElement {

    private static final String code_id = "$Id: AttributeDefinitionReference.java,v 1.2 2006/05/15 23:31:00 kas Exp $";

    // <xsd:attribute name="schema" type="anyURI" use="optional" />
    private URI m_schema;

    // <xsd:attribute name="required" type="xsd:boolean" use="optional" default="false"/>
    private Boolean m_required;

    // <xsd:attribute name="name" type="xsd:string" use="required"/>
    private String m_name;

    protected AttributeDefinitionReference(String name, URI schema, Boolean required) {
        assert name != null;
        m_schema = schema;
        m_required = required;
        m_name = name;
    }

    public AttributeDefinitionReference() { ; }

    public AttributeDefinitionReference(String name, URI schema, boolean required) {
        this(name, schema, new Boolean(required));
    }

    public AttributeDefinitionReference(String name, URI schema) {
        this(name, schema, null);
    }

    public AttributeDefinitionReference(String name) {
        this(name, null, null);
    }

    public URI getSchema() {
        return m_schema;
    }

    public void setSchema(URI schema) {
        m_schema = schema;
    }

    public Boolean getRequired() {
        return m_required;
    }

    public void setRequired(Boolean required) {
        m_required = required;
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
        if (!(o instanceof AttributeDefinitionReference)) return false;
        if (!super.equals(o)) return false;

        final AttributeDefinitionReference attributeDefinitionReference = (AttributeDefinitionReference) o;

        if (!m_name.equals(attributeDefinitionReference.m_name)) return false;
        if (m_required != null ? !m_required.equals(attributeDefinitionReference.m_required) : attributeDefinitionReference.m_required != null) return false;
        if (m_schema != null ? !m_schema.equals(attributeDefinitionReference.m_schema) : attributeDefinitionReference.m_schema != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_schema != null ? m_schema.hashCode() : 0);
        result = 29 * result + (m_required != null ? m_required.hashCode() : 0);
        result = 29 * result + m_name.hashCode();
        return result;
    }
}
