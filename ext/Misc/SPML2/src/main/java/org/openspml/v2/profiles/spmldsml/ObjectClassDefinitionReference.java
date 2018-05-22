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

import java.net.URI;

//<xsd:complexType name="ObjectClassDefinitionReferenceType">
//    <complexContent>
//       <extension base="spml:ExtensibleType">
//           <xsd:attribute name="schemaref" type="anyURI" use="optional" />
//           <xsd:attribute name="name" type="xsd:string" use="required"/>
//       </extension>
//   </complexContent>
//</xsd:complexType>

public class ObjectClassDefinitionReference extends ExtensibleElement {

    private static final String code_id = "$Id: ObjectClassDefinitionReference.java,v 1.3 2006/08/30 18:02:59 kas Exp $";

    // <xsd:attribute name="name" type="xsd:string" use="required"/>
    private String m_name;

    // <xsd:attribute name="schemaref" type="anyURI" use="optional" />
    private URI m_schemaref;

    protected ObjectClassDefinitionReference() { ; }

    public ObjectClassDefinitionReference(String name, URI schemaref) {
        assert name != null;
        m_name = name;
        m_schemaref = schemaref;
    }

    public ObjectClassDefinitionReference(String name) {
        this(name, null);
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        assert name != null;
        m_name = name;
    }

    public URI getSchemaref() {
        return m_schemaref;
    }

    public void setSchemaref(URI schemaref) {
        m_schemaref = schemaref;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return DSMLMarshallableCreator.staticGetNamespacesInfo();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObjectClassDefinitionReference)) return false;

        final ObjectClassDefinitionReference objectClassDefinitionReference = (ObjectClassDefinitionReference) o;

        if (m_name != null ? !m_name.equals(objectClassDefinitionReference.m_name) : objectClassDefinitionReference.m_name != null) return false;
        if (m_schemaref != null ? !m_schemaref.equals(objectClassDefinitionReference.m_schemaref) : objectClassDefinitionReference.m_schemaref != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (m_name != null ? m_name.hashCode() : 0);
        result = 29 * result + (m_schemaref != null ? m_schemaref.hashCode() : 0);
        return result;
    }
}
