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
 * <br>&lt;complexType name="CapabilitiesListType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="capability" type="spml:CapabilityType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 * <br><p/>
 * This should probably implement java.util.List
 *
 * @author kent.spaulding@sun.com
 *         &lt;p/&gt;
 *         Date: Feb 8, 2006
 */
public class CapabilitiesList extends Extensible {

    private static final String code_id = "$Id: CapabilitiesList.java,v 1.5 2006/04/25 21:22:09 kas Exp $";

    // <element name="capability" type="spml:CapabilityType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_capability = new ArrayListWithType(Capability.class);

    public CapabilitiesList() { ; }

    public CapabilitiesList(Capability[] capabilities) {
        if (capabilities != null) {
            m_capability.addAll(Arrays.asList(capabilities));
        }
    }

    public Capability[] getCapabilities() {
        return (Capability[]) m_capability.toArray(new Capability[m_capability.size()]);
    }

    public void clearCapabilities() {
        m_capability.clear();
    }

    public void addCapability(Capability capability) {
        assert (capability != null);
        m_capability.add(capability);
    }

    public boolean removeCapability(Capability capability) {
        assert (capability != null);
        return m_capability.remove(capability);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CapabilitiesList)) return false;
        if (!super.equals(o)) return false;

        final CapabilitiesList capabilitiesList = (CapabilitiesList) o;

        if (!m_capability.equals(capabilitiesList.m_capability)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_capability.hashCode();
        return result;
    }
}
