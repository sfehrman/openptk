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
package org.openspml.v2.msg.spmlupdates;

import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.PSOIdentifier;

/**
 * <br>&lt;complexType name="UpdateType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="timestamp" type="xsd:dateTime" use="required"/&gt;
 * <br>&lt;attribute name="updateKind" type="spmlupdates:UpdateKindType" use="required"/&gt;
 * <br>&lt;attribute name="wasUpdatedByCapability" type="xsd:string" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class Update extends BasicRequest {

    private static final String code_id = "$Id: Update.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    //* <element name="psoID" type="spml:PSOIdentifierType" />
    private PSOIdentifier m_psoID = null; // required

    //* <attribute name="timestamp" type="xsd:dateTime" use="required"/>
    private String m_timestamp = null; // required ; TODO DateTime

    //* <attribute name="updateKind" type="spmlupdates:UpdateKindType" use="required"/>
    private UpdateKind m_updateKind = null;

    //* <attribute name="wasUpdatedByCapability" type="xsd:string" use="optional"/>
    private String m_wasUpdatedByCapability = null;

    public Update() { ; }

    public Update(String requestId,
                  ExecutionMode executionMode,
                  PSOIdentifier psoID,
                  String timestamp,
                  UpdateKind updateKind,
                  String wasUpdatedByCapability) {
        super(requestId, executionMode);
        assert (psoID != null);
        m_psoID = psoID;
        assert (timestamp != null);
        m_timestamp = timestamp;
        assert (updateKind != null);
        m_updateKind = updateKind;

        m_wasUpdatedByCapability = wasUpdatedByCapability;
    }

    public PSOIdentifier getPsoID() {
        return m_psoID;
    }

    public void setPsoID(PSOIdentifier psoID) {
        assert (psoID != null);
        m_psoID = psoID;
    }

    public String getTimestamp() {
        return m_timestamp;
    }

    public void setTimestamp(String timestamp) {
        assert (timestamp != null);
        m_timestamp = timestamp;
    }

    public UpdateKind getUpdateKind() {
        return m_updateKind;
    }

    public void setUpdateKind(UpdateKind updateKind) {
        assert (updateKind != null);
        m_updateKind = updateKind;
    }

    public String getWasUpdatedByCapability() {
        return m_wasUpdatedByCapability;
    }

    public void setWasUpdatedByCapability(String wasUpdatedByCapability) {
        m_wasUpdatedByCapability = wasUpdatedByCapability;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Update)) return false;
        if (!super.equals(o)) return false;

        final Update update = (Update) o;

        if (!m_psoID.equals(update.m_psoID)) return false;
        if (!m_timestamp.equals(update.m_timestamp)) return false;
        if (!m_updateKind.equals(update.m_updateKind)) return false;
        if (m_wasUpdatedByCapability != null ? !m_wasUpdatedByCapability.equals(update.m_wasUpdatedByCapability) : update.m_wasUpdatedByCapability != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_psoID.hashCode();
        result = 29 * result + m_timestamp.hashCode();
        result = 29 * result + m_updateKind.hashCode();
        result = 29 * result + (m_wasUpdatedByCapability != null ? m_wasUpdatedByCapability.hashCode() : 0);
        return result;
    }
}
