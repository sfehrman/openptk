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


//&lt;complexType name="PSOIdentifierType"&gt;
//    &lt;complexContent&gt;
//        &lt;extension base="spml:IdentifierType"&gt;
//            &lt;sequence&gt;
//                &lt;element name="containerID" type="spml:PSOIdentifierType" minOccurs="0" /&gt;
//            &lt;/sequence&gt;
//            &lt;attribute name="targetID" type="string" use="optional"/&gt;
//        &lt;/extension&gt;
//    &lt;/complexContent&gt;
//&lt;/complexType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 2, 2006
 */
public class PSOIdentifier extends Identifier {

    private static final String code_id = "$Id: PSOIdentifier.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="containerID" type="spml:PSOIdentifierType" minOccurs="0" />
    private PSOIdentifier m_containerID = null;

    // <attribute name="targetID" type="string" use="optional"/>
    private String m_targetID = null;

    public PSOIdentifier() { ; }

    public PSOIdentifier(String anID,
                         PSOIdentifier containerID,
                         String targetID) {
        super(anID);
        m_containerID = containerID;
        m_targetID = targetID;
    }

    public PSOIdentifier getContainerID() {
        return m_containerID;
    }

    public void setContainerID(PSOIdentifier containerID) {
        m_containerID = containerID;
    }

    public String getTargetID() {
        return m_targetID;
    }

    public void setTargetID(String targetID) {
        m_targetID = targetID;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PSOIdentifier)) return false;
        if (!super.equals(o)) return false;

        final PSOIdentifier psoIdentifierType = (PSOIdentifier) o;

        if (m_containerID != null ? !m_containerID.equals(psoIdentifierType.m_containerID) : psoIdentifierType.m_containerID != null) return false;
        if (m_targetID != null ? !m_targetID.equals(psoIdentifierType.m_targetID) : psoIdentifierType.m_targetID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_containerID != null ? m_containerID.hashCode() : 0);
        result = 29 * result + (m_targetID != null ? m_targetID.hashCode() : 0);
        return result;
    }
}
