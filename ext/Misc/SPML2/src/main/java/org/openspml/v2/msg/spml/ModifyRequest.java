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

import java.util.Arrays;

/**
 * <br>&lt;complexType name="ModifyRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType"/&gt;
 * <br>&lt;element name="modification" type="spml:ModificationType" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class ModifyRequest extends BatchableRequest {

    private static final String code_id = "$Id: ModifyRequest.java,v 1.5 2006/06/29 21:01:21 rfrech Exp $";

    // <element name="psoID" type="spml:PSOIdentifierType"/>
    private PSOIdentifier m_psoID = null;

    // <element name="modification" type="spml:ModificationType" maxOccurs="unbounded"/>
    private ListWithType m_modification = new ArrayListWithType(Modification.class);

    // <attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/>
    private ReturnData m_returnData = ReturnData.EVERYTHING;

    public ModifyRequest() { ; }

    public ModifyRequest(String requestId,
                         ExecutionMode executionMode,
                         PSOIdentifier psoID,
                         Modification[] modifications,
                         ReturnData returnData) {
        super(requestId, executionMode);
        assert (psoID != null);
        m_psoID = psoID;
        if (modifications != null) {
            m_modification.addAll(Arrays.asList(modifications));
        }
        m_returnData = returnData;
    }

    public PSOIdentifier getPsoID() {
        return m_psoID;
    }

    public void setPsoID(PSOIdentifier psoID) {
        assert (psoID != null);
        m_psoID = psoID;
    }

    public Modification[] getModifications() {
        return (Modification[]) m_modification.toArray(new Modification[m_modification.size()]);
    }

    public void addModification(Modification modification) {
        assert (modification != null)  ;
        m_modification.add(modification);
    }

    public boolean removeModification(Modification modification) {
        assert (modification != null);
        return m_modification.remove(modification);
    }

    public void clearModifications() {
        m_modification.clear();
    }

    public ReturnData getReturnData() {
        return m_returnData;
    }

    public void setReturnData(ReturnData returnData) {
        m_returnData = returnData;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModifyRequest)) return false;
        if (!super.equals(o)) return false;

        final ModifyRequest modifyRequestType = (ModifyRequest) o;

        if (m_modification != null ? !m_modification.equals(modifyRequestType.m_modification) : modifyRequestType.m_modification != null) return false;
        if (!m_psoID.equals(modifyRequestType.m_psoID)) return false;
        if (m_returnData != null ? !m_returnData.equals(modifyRequestType.m_returnData) : modifyRequestType.m_returnData != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_psoID.hashCode();
        result = 29 * result + (m_modification != null ? m_modification.hashCode() : 0);
        result = 29 * result + (m_returnData != null ? m_returnData.hashCode() : 0);
        return result;
    }
}
