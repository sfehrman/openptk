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
 * <br>&lt;complexType name="AddRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType" minOccurs="0"/&gt;
 * <br>&lt;element name="containerID" type="spml:PSOIdentifierType" minOccurs="0"/&gt;
 * <br>&lt;element name="data" type="spml:ExtensibleType" /&gt;
 * <br>&lt;element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="targetID" type="string" use="optional"/&gt;
 * <br>&lt;attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class AddRequest extends BatchableRequest {

    private static final String code_id = "$Id: AddRequest.java,v 1.6 2006/06/29 21:01:21 rfrech Exp $";

    // <element name="psoID" type="spml:PSOIdentifierType" minOccurs="0"/>
    private PSOIdentifier m_psoID = null;

    // <element name="containerID" type="spml:PSOIdentifierType" minOccurs="0"/>
    private PSOIdentifier m_containerID = null;

    // <element name="data" type="spml:ExtensibleType" />
    private Extensible m_data = null; // required

    // <element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_capabilityData = new ArrayListWithType(CapabilityData.class);

    // <attribute name="targetID" type="string" use="optional"/>
    private String m_targetId = null;

    // <attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/>
    private ReturnData m_returnData = ReturnData.EVERYTHING;

    public AddRequest() {} ;

    public AddRequest(String requestId,
                      ExecutionMode executionMode,
                      PSOIdentifier type,
                      PSOIdentifier containerID,
                      Extensible data,
                      CapabilityData[] capabilityData,
                      String targetId,
                      ReturnData returnData) {
        super(requestId, executionMode);
        m_psoID = type;
        m_containerID = containerID;

        assert (data != null);
        m_data = data;

        if (capabilityData != null)
            m_capabilityData.addAll(Arrays.asList(capabilityData));

        m_targetId = targetId;
        m_returnData = returnData;
    }

    public PSOIdentifier getPsoID() {
        return m_psoID;
    }

    public void setPsoID(PSOIdentifier psoID) {
        m_psoID = psoID;
    }

    public PSOIdentifier getContainerID() {
        return m_containerID;
    }

    public void setContainerID(PSOIdentifier containerID) {
        m_containerID = containerID;
    }

    public Extensible getData() {
        return m_data;
    }

    public void setData(Extensible data) {
        assert (data != null);
        m_data = data;
    }

    public CapabilityData[] getCapabilityData() {
        return (CapabilityData[]) m_capabilityData.toArray(new CapabilityData[m_capabilityData.size()]);
    }

    public void addCapabilityData(CapabilityData capabilityData) {
        assert (capabilityData != null);
        m_capabilityData.add(capabilityData);
    }

    public boolean removeCapabilityData(CapabilityData capabilityData) {
        assert (capabilityData != null);
        return m_capabilityData.remove(capabilityData);
    }

    public void clearCapabilityData() {
        m_capabilityData.clear();
    }

    public String getTargetId() {
        return m_targetId;
    }

    public void setTargetId(String targetId) {
        m_targetId = targetId;
    }

    public ReturnData getReturnData() {
        return m_returnData;
    }

    public void setReturnData(ReturnData returnData) {
        m_returnData = returnData;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddRequest)) return false;
        if (!super.equals(o)) return false;

        final AddRequest addRequest = (AddRequest) o;

        if (!m_capabilityData.equals(addRequest.m_capabilityData)) return false;
        if (m_containerID != null ? !m_containerID.equals(addRequest.m_containerID) : addRequest.m_containerID != null) return false;
        if (!m_data.equals(addRequest.m_data)) return false;
        if (m_returnData != null ? !m_returnData.equals(addRequest.m_returnData) : addRequest.m_returnData != null) return false;
        if (m_targetId != null ? !m_targetId.equals(addRequest.m_targetId) : addRequest.m_targetId != null) return false;
        if (m_psoID != null ? !m_psoID.equals(addRequest.m_psoID) : addRequest.m_psoID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_psoID != null ? m_psoID.hashCode() : 0);
        result = 29 * result + (m_containerID != null ? m_containerID.hashCode() : 0);
        result = 29 * result + (m_data != null ? m_data.hashCode() : 0);
        result = 29 * result + m_capabilityData.hashCode();
        result = 29 * result + (m_targetId != null ? m_targetId.hashCode() : 0);
        result = 29 * result + (m_returnData != null ? m_returnData.hashCode() : 0);
        return result;
    }
}