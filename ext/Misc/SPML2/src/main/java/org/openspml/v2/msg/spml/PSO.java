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
 * <br>&lt;complexType name="PSOType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType" /&gt;
 * <br>&lt;element name="data" type="spml:ExtensibleType" minOccurs="0" /&gt;
 * <br>&lt;element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class PSO extends Extensible {

    private static final String code_id = "$Id: PSO.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="psoID" type="spml:PSOIdentifierType" />
    private PSOIdentifier m_psoID = null;

    // <element name="data" type="spml:ExtensibleType" minOccurs="0" />
    private Extensible m_data = null;

    // <element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_capabilityData = new ArrayListWithType(CapabilityData.class);

    public PSO() { ; }

    public PSO(PSOIdentifier psoID,
               Extensible data,
               CapabilityData[] capabilityData) {
        assert (psoID != null);
        m_psoID = psoID;

        m_data = data;
        if (capabilityData != null)
            m_capabilityData.addAll(Arrays.asList(capabilityData));

    }

    public PSOIdentifier getPsoID() {
        return m_psoID;
    }

    public void setPsoID(PSOIdentifier psoID) {
        m_psoID = psoID;
        assert (m_psoID != null);
    }

    public Extensible getData() {
        return m_data;
    }

    public void setData(Extensible data) {
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PSO)) return false;
        if (!super.equals(o)) return false;

        final PSO psoType = (PSO) o;

        if (m_capabilityData != null ? !m_capabilityData.equals(psoType.m_capabilityData) : psoType.m_capabilityData != null) return false;
        if (m_data != null ? !m_data.equals(psoType.m_data) : psoType.m_data != null) return false;
        if (!m_psoID.equals(psoType.m_psoID)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_psoID.hashCode();
        result = 29 * result + (m_data != null ? m_data.hashCode() : 0);
        result = 29 * result + (m_capabilityData != null ? m_capabilityData.hashCode() : 0);
        return result;
    }
}
