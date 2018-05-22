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

import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.msg.spml.PSOIdentifier;
import org.openspml.v2.msg.spml.QueryClause;

/**
 * <br>&lt;complexType name="HasReferenceType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:QueryClauseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="toPsoID" type="spml:PSOIdentifierType" minOccurs="0"/&gt;
 * <br>&lt;element name="referenceData" type="spml:ExtensibleType" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="typeOfReference" type="string" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 13, 2006
 */
public class HasReference extends QueryClause {

    private static final String code_id = "$Id: HasReference.java,v 1.4 2006/05/15 23:31:00 kas Exp $";

    // <element name="toPsoID" type="spml:PSOIdentifierType" minOccurs="0"/>
    private PSOIdentifier m_toPsoID = null; // optional

    // <element name="referenceData" type="spml:ExtensibleType" minOccurs="0" />
    private Extensible m_referenceData = null; // optional

    // <attribute name="typeOfReference" type="string" use="optional"/>
    private String m_typeOfReference = null; // optional

    public HasReference() { ; }

    public HasReference(PSOIdentifier toPsoID,
                        Extensible referenceData,
                        String typeOfReference) {
        m_toPsoID = toPsoID;
        m_referenceData = referenceData;
        m_typeOfReference = typeOfReference;
    }

    public PSOIdentifier getToPsoID() {
        return m_toPsoID;
    }

    public void setToPsoID(PSOIdentifier toPsoID) {
        m_toPsoID = toPsoID;
    }

    public Extensible getReferenceData() {
        return m_referenceData;
    }

    public void setReferenceData(Extensible referenceData) {
        m_referenceData = referenceData;
    }

    public String getTypeOfReference() {
        return m_typeOfReference;
    }

    public void setTypeOfReference(String typeOfReference) {
        m_typeOfReference = typeOfReference;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return PrefixAndNamespaceTuple.concatNamespacesInfo(
                super.getNamespacesInfo(),
                ExtensibleMarshallable.staticGetNamespacesInfo());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HasReference)) return false;
        if (!super.equals(o)) return false;

        final HasReference hasReference = (HasReference) o;

        if (m_referenceData != null ? !m_referenceData.equals(hasReference.m_referenceData) : hasReference.m_referenceData != null) return false;
        if (m_toPsoID != null ? !m_toPsoID.equals(hasReference.m_toPsoID) : hasReference.m_toPsoID != null) return false;
        if (m_typeOfReference != null ? !m_typeOfReference.equals(hasReference.m_typeOfReference) : hasReference.m_typeOfReference != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_toPsoID != null ? m_toPsoID.hashCode() : 0);
        result = 29 * result + (m_referenceData != null ? m_referenceData.hashCode() : 0);
        result = 29 * result + (m_typeOfReference != null ? m_typeOfReference.hashCode() : 0);
        return result;
    }
}
