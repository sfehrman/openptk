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
package org.openspml.v2.msg.spmlref;

import org.openspml.v2.msg.spml.SchemaEntityRef;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="ReferenceDefinitionType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br><p/>
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="schemaEntity" type="spml:SchemaEntityRefType"/&gt;
 * <br>&lt;element name="canReferTo" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;element name="referenceDataType" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="typeOfReference" type="string" use="required"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         &lt;p/&gt;
 *         Date: Feb 13, 2006
 */
public class ReferenceDefinition extends ExtensibleMarshallable {

    private static final String code_id = "$Id: ReferenceDefinition.java,v 1.6 2006/08/30 18:02:59 kas Exp $";

    // <element name="schemaEntity" type="spml:SchemaEntityRefType"/>
    private SchemaEntityRef m_schemaEntity = null; // required.

    // <element name="canReferTo" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_canReferTo = new ArrayListWithType(SchemaEntityRef.class);

    // <element name="referenceDataType" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_referenceDataType = new ArrayListWithType(SchemaEntityRef.class);

    // <attribute name="typeOfReference" type="string" use="required"/>
    private String m_typeOfReference = null; // required

    public ReferenceDefinition() { ;  }

    public ReferenceDefinition(SchemaEntityRef schemaEntity,
                               SchemaEntityRef[] canReferTo,
                               SchemaEntityRef[] referenceDataType,
                               String typeOfReference) {
        assert (schemaEntity != null);
        m_schemaEntity = schemaEntity;

        if (canReferTo != null)
            m_canReferTo.addAll(Arrays.asList(canReferTo));

        if (referenceDataType != null)
            m_referenceDataType.addAll(Arrays.asList(referenceDataType));

        assert (typeOfReference != null);
        m_typeOfReference = typeOfReference;
    }

    public SchemaEntityRef getSchemaEntity() {
        return m_schemaEntity;
    }

    public void setSchemaEntity(SchemaEntityRef schemaEntity) {
        assert (schemaEntity != null);
        m_schemaEntity = schemaEntity;
    }

    public SchemaEntityRef[] getCanReferTo() {
        return (SchemaEntityRef[]) m_canReferTo.toArray(new SchemaEntityRef[m_canReferTo.size()]);
    }

    public void addCanReferTo(SchemaEntityRef canReferTo) {
        if (canReferTo != null)
            m_canReferTo.add(canReferTo);
    }

    public boolean removeCanReferTo(SchemaEntityRef canReferTo) {
        if (canReferTo != null)
            return m_canReferTo.remove(canReferTo);
        return false;
    }

    public void clearCanReferTo() {
        m_canReferTo.clear();
    }

    public SchemaEntityRef[] getReferenceDataType() {
        return (SchemaEntityRef[]) m_referenceDataType.toArray(new SchemaEntityRef[m_referenceDataType.size()]);
    }

    public void addReferenceDataType(SchemaEntityRef referenceDataType) {
        if (referenceDataType != null)
            m_referenceDataType.add(referenceDataType);
    }

    public boolean removeReferenceDataType(SchemaEntityRef referenceDataType) {
        if (referenceDataType != null)
            return m_referenceDataType.remove(referenceDataType);
        return false;
    }

    public void clearReferenceDataType() {
        m_referenceDataType.clear();
    }

    public String getTypeOfReference() {
        return m_typeOfReference;
    }

    public void setTypeOfReference(String typeOfReference) {
        assert (typeOfReference != null);
        m_typeOfReference = typeOfReference;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceDefinition)) return false;
        if (!super.equals(o)) return false;

        final ReferenceDefinition referenceDefinition = (ReferenceDefinition) o;

        if (!m_canReferTo.equals(referenceDefinition.m_canReferTo)) return false;
        if (!m_referenceDataType.equals(referenceDefinition.m_referenceDataType)) return false;
        if (!m_schemaEntity.equals(referenceDefinition.m_schemaEntity)) return false;
        if (!m_typeOfReference.equals(referenceDefinition.m_typeOfReference)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_schemaEntity.hashCode();
        result = 29 * result + m_canReferTo.hashCode();
        result = 29 * result + m_referenceDataType.hashCode();
        result = 29 * result + m_typeOfReference.hashCode();
        return result;
    }
}
