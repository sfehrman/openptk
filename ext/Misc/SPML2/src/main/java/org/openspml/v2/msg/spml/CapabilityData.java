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

import java.net.URI;

//&lt;complexType name="CapabilityDataType"&gt;
//    &lt;complexContent&gt;
//        &lt;extension base="spml:ExtensibleType"&gt;
//            &lt;annotation&gt;
//                &lt;documentation&gt;Contains capability specific elements.&lt;/documentation&gt;
//            &lt;/annotation&gt;
//            &lt;attribute name="mustUnderstand" type="boolean" use="optional"/&gt;
//            &lt;attribute name="capabilityURI" type="anyURI" /&gt;
//        &lt;/extension&gt;
//    &lt;/complexContent&gt;
//&lt;/complexType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 2, 2006
 */
public class CapabilityData extends Extensible {

    private static final String code_id = "$Id: CapabilityData.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <attribute name="mustUnderstand" type="boolean" use="optional"/>
    private Boolean m_mustUnderstand = null;

    // <attribute name="capabilityURI" type="anyURI" />
    private URI m_capabilityURI;

    protected CapabilityData(Boolean mustUnderstand,
                             URI capabilityURI) {
        m_mustUnderstand = mustUnderstand;
        assert(capabilityURI != null);
        m_capabilityURI = capabilityURI;
    }

    public CapabilityData() { ; }

    public CapabilityData(boolean mustUnderstand,
                          URI capabilityURI) {
        this(new Boolean(mustUnderstand), capabilityURI);
    }

    public CapabilityData(URI capabilityURI) {
        this(null, capabilityURI);
    }

    public boolean isMustUnderstand() {
        if (m_mustUnderstand == null) return false;
        return m_mustUnderstand.booleanValue();
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        m_mustUnderstand = new Boolean(mustUnderstand);
    }

    public URI getCapabilityURI() {
        return m_capabilityURI;
    }

    public void setCapabilityURI(URI capabilityURI) {
        m_capabilityURI = capabilityURI;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CapabilityData)) return false;
        if (!super.equals(o)) return false;

        final CapabilityData capabilityData = (CapabilityData) o;

        if (m_capabilityURI != null ? !m_capabilityURI.equals(capabilityData.m_capabilityURI) : capabilityData.m_capabilityURI != null) return false;
        if (m_mustUnderstand != null ? !m_mustUnderstand.equals(capabilityData.m_mustUnderstand) : capabilityData.m_mustUnderstand != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_mustUnderstand != null ? m_mustUnderstand.hashCode() : 0);
        result = 29 * result + (m_capabilityURI != null ? m_capabilityURI.hashCode() : 0);
        return result;
    }
}
