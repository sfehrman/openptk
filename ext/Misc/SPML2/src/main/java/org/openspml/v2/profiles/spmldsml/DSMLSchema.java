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

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;
import org.openspml.v2.util.xml.ReflectiveXMLMarshaller;

import java.util.Arrays;

//<xsd:complexType name="SchemaType">
//    <complexContent>
//       <extension base="spml:ExtensibleType">
//           <xsd:sequence>
//               <xsd:element name="objectClassDefinition" type="spmldsml:ObjectClassDefinitionType" minOccurs="0" maxOccurs="unbounded"/>
//               <xsd:element name="attributeDefinition" type="spmldsml:AttributeDefinitionType" minOccurs="0" maxOccurs="unbounded"/>
//           </xsd:sequence>
//       </extension>
//   </complexContent>
//</xsd:complexType>

public class DSMLSchema extends ExtensibleElement implements Marshallable, OpenContentElement {

    private static final String code_id = "$Id: DSMLSchema.java,v 1.5 2006/10/04 01:06:12 kas Exp $";

    // <xsd:element name="objectClassDefinition" type="spmldsml:ObjectClassDefinitionType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_objectClassDefinition = new ArrayListWithType(ObjectClassDefinition.class);

    // <xsd:element name="attributeDefinition" type="spmldsml:AttributeDefinitionType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_attributeDefinition = new ArrayListWithType(AttributeDefinition.class);

    public DSMLSchema() { ; }

    public DSMLSchema(ObjectClassDefinition[] objectClassDefs,
                  AttributeDefinition[] attributeDefs) {
        if (objectClassDefs != null) {
            m_objectClassDefinition.addAll(Arrays.asList(objectClassDefs));
        }
        if (attributeDefs != null) {
            m_attributeDefinition.addAll(Arrays.asList(attributeDefs));
        }
    }

    public String getElementName() {
        return "schema";
    }

    public ObjectClassDefinition[] getObjectClassDefinitions() {
        return (ObjectClassDefinition[])
                m_objectClassDefinition.toArray(new ObjectClassDefinition[m_objectClassDefinition.size()]);
    }

    public void addObjectClassDefinition(ObjectClassDefinition reference) {
        assert (reference != null);
        m_objectClassDefinition.add(reference);
    }

    public boolean removeObjectClassDefinition(ObjectClassDefinition reference) {
        assert (reference != null);
        return m_objectClassDefinition.remove(reference);
    }

    public void clearObjectClassDefinitions() {
        m_objectClassDefinition.clear();
    }

    public AttributeDefinition[] getAttributeDefinitions() {
        return (AttributeDefinition[])
                m_attributeDefinition.toArray(new AttributeDefinition[m_attributeDefinition.size()]);
    }

    public void addAttributeDefinition(AttributeDefinition reference) {
        assert (reference != null);
        m_attributeDefinition.add(reference);
    }

    public boolean removeAttributeDefinition(AttributeDefinition reference) {
        assert (reference != null);
        return m_attributeDefinition.remove(reference);
    }

    public void clearAttributeDefinitions() {
        m_attributeDefinition.clear();
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    /**
     * Most of the checks can be handled with setters/getters and assertions.
     *
     * @return   true if valid, false otherwise.
     */
    public boolean isValid() {
        return true;
    }

    public String toXML(int indent) throws Spml2Exception {
        XMLMarshaller m = new ReflectiveXMLMarshaller();
        m.setIndent(indent);
        return m.marshall(this);
    }

    public String toXML() throws Spml2Exception {
        return toXML(0);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DSMLSchema)) return false;
        if (!super.equals(o)) return false;

        final DSMLSchema schema = (DSMLSchema) o;

        if (m_attributeDefinition != null ? !m_attributeDefinition.equals(schema.m_attributeDefinition) : schema.m_attributeDefinition != null) return false;
        if (m_objectClassDefinition != null ? !m_objectClassDefinition.equals(schema.m_objectClassDefinition) : schema.m_objectClassDefinition != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_objectClassDefinition != null ? m_objectClassDefinition.hashCode() : 0);
        result = 29 * result + (m_attributeDefinition != null ? m_attributeDefinition.hashCode() : 0);
        return result;
    }

}
