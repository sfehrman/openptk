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


/**
 * <br>&lt;complexType name="SchemaEntityRefType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;attribute name="targetID" type="string" use="optional"/&gt;
 * <br>&lt;attribute name="entityName" type="string" use="optional" /&gt;
 * <br>&lt;attribute name="isContainer" type="xsd:boolean" use="optional" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class SchemaEntityRef extends Extensible {

    private static final String code_id = "$Id: SchemaEntityRef.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    // <attribute name="targetID" type="string" use="optional"/>
    private String m_targetID = null;

    // <attribute name="entityName" type="string" use="optional" />
    private String m_entityName = null;

    // <attribute name="isContainer" type="xsd:boolean" use="optional" />
    private Boolean m_isContainer = null;

    protected SchemaEntityRef(String targetID,
                              String entityName,
                              Boolean isContainer) {
        m_targetID = targetID;
        m_entityName = entityName;
        m_isContainer = isContainer;
    }

    public SchemaEntityRef() { ;  }

    public SchemaEntityRef(String targetID,
                           String entityName,
                           boolean isContainer) {
        this(targetID, entityName, new Boolean(isContainer));
    }


    public SchemaEntityRef(String targetID,
                           String entityName) {
        this(targetID, entityName, null);
    }

    public String getTargetID() {
        return m_targetID;
    }

    public void setTargetID(String targetID) {
        m_targetID = targetID;
    }

    public String getEntityName() {
        return m_entityName;
    }

    public void setEntityName(String entityName) {
        m_entityName = entityName;
    }

    public boolean isContainer() {
        if (m_isContainer == null) return false;
        return m_isContainer.booleanValue();
    }

    public void setIsContainer(boolean isContainer) {
        m_isContainer = new Boolean(isContainer);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchemaEntityRef)) return false;
        if (!super.equals(o)) return false;

        final SchemaEntityRef schemaEntityRef = (SchemaEntityRef) o;

        if (m_entityName != null ? !m_entityName.equals(schemaEntityRef.m_entityName) : schemaEntityRef.m_entityName != null) return false;
        if (m_isContainer != null ? !m_isContainer.equals(schemaEntityRef.m_isContainer) : schemaEntityRef.m_isContainer != null) return false;
        if (m_targetID != null ? !m_targetID.equals(schemaEntityRef.m_targetID) : schemaEntityRef.m_targetID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_targetID != null ? m_targetID.hashCode() : 0);
        result = 29 * result + (m_entityName != null ? m_entityName.hashCode() : 0);
        result = 29 * result + (m_isContainer != null ? m_isContainer.hashCode() : 0);
        return result;
    }
}
