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
package org.openspml.v2.msg.spml;

import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.net.URI;
import java.util.Arrays;

/**
 * <br>&lt;complexType name="SchemaType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;annotation&gt;
 * <br>&lt;/documentation&gt;
 * <br>&lt;/annotation&gt;
 * <br>&lt;element name="supportedSchemaEntity" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="ref" type="anyURI" use="optional" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class Schema extends Extensible {

    private static final String code_id = "$Id: Schema.java,v 1.5 2006/06/27 00:47:19 kas Exp $";

    // <element name="supportedSchemaEntity" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_supportedSchemaEntity = new ArrayListWithType(SchemaEntityRef.class);

    // <attribute name="ref" type="anyURI" use="optional" />
    private URI m_ref = null; // optional

    public Schema() { ; }

    public Schema(SchemaEntityRef[] supportedSchemaEntity,
                  URI ref) {
        if (supportedSchemaEntity != null)
            m_supportedSchemaEntity.addAll(Arrays.asList(supportedSchemaEntity));
        m_ref = ref;
    }

    public SchemaEntityRef[] getSupportedSchemaEntities() {
        return (SchemaEntityRef[]) m_supportedSchemaEntity.toArray(new SchemaEntityRef[m_supportedSchemaEntity.size()]);
    }

    public void addSupportedSchemaEntity(SchemaEntityRef se) {
        assert (se != null);
        m_supportedSchemaEntity.add(se);
    }

    public boolean removeSupportedSchemaEntity(SchemaEntityRef se) {
        assert (se != null);
        return m_supportedSchemaEntity.remove(se);
    }

    public void clearSupportedSchemaEntities() {
        m_supportedSchemaEntity.clear();
    }

    public URI getRef() {
        return m_ref;
    }

    public void setRef(URI ref) {
        m_ref = ref;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schema)) return false;
        if (!super.equals(o)) return false;

        final Schema schemaType = (Schema) o;

        if (m_ref != null ? !m_ref.equals(schemaType.m_ref) : schemaType.m_ref != null) return false;
        if (!m_supportedSchemaEntity.equals(schemaType.m_supportedSchemaEntity)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_supportedSchemaEntity.hashCode();
        result = 29 * result + (m_ref != null ? m_ref.hashCode() : 0);
        return result;
    }
}
