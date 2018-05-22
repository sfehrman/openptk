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
 * <br>&lt;complexType name="ModificationType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="component" type="spml:SelectionType" minOccurs="0" /&gt;
 * <br>&lt;element name="data" type="spml:ExtensibleType" minOccurs="0" /&gt;
 * <br>&lt;element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="modificationMode" type="spml:ModificationModeType" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class Modification extends Extensible {

    private static final String code_id = "$Id: Modification.java,v 1.6 2006/08/30 18:02:59 kas Exp $";

    // <element name="component" type="spml:SelectionType" minOccurs="0" />
    private Selection m_component = null;

    // <element name="data" type="spml:ExtensibleType" minOccurs="0" />
    private Extensible m_data = null;

    // <element name="capabilityData" type="spml:CapabilityDataType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_capabilityData = new ArrayListWithType(CapabilityData.class);

    // <attribute name="modificationMode" type="spml:ModificationModeType" use="optional"/>
    private ModificationMode m_modificationMode = null;

    public Modification() { ; }

    public Modification(Selection component,
                        Extensible data,
                        CapabilityData[] capabilityData,
                        ModificationMode modificationMode) {
        m_component = component;
        m_data = data;
        if (capabilityData != null) {
            m_capabilityData.addAll(Arrays.asList(capabilityData));
        }
        m_modificationMode = modificationMode;
    }

    public Selection getComponent() {
        return m_component;
    }

    public void setComponent(Selection component) {
        m_component = component;
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

    public ModificationMode getModificationMode() {
        return m_modificationMode;
    }

    public void setModificationMode(ModificationMode modificationMode) {
        m_modificationMode = modificationMode;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Modification)) return false;
        if (!super.equals(o)) return false;

        final Modification modificationType = (Modification) o;

        if (!m_capabilityData.equals(modificationType.m_capabilityData)) return false;
        if (m_component != null ? !m_component.equals(modificationType.m_component) : modificationType.m_component != null) return false;
        if (m_data != null ? !m_data.equals(modificationType.m_data) : modificationType.m_data != null) return false;
        if (m_modificationMode != null ? !m_modificationMode.equals(modificationType.m_modificationMode) : modificationType.m_modificationMode != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_component != null ? m_component.hashCode() : 0);
        result = 29 * result + (m_data != null ? m_data.hashCode() : 0);
        result = 29 * result + m_capabilityData.hashCode();
        result = 29 * result + (m_modificationMode != null ? m_modificationMode.hashCode() : 0);
        return result;
    }
}
