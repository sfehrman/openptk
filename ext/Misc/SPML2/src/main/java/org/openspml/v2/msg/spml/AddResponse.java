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
 * <br>&lt;complexType name="AddResponseType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ResponseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="pso" type="spml:PSOType" minOccurs="0"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class AddResponse extends Response {

    private static final String code_id = "$Id: AddResponse.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="pso" type="spml:PSOType" minOccurs="0"/>
    private PSO m_pso = null;

    public AddResponse() { ; }
    
    public AddResponse(String[] errorMessages,
                       StatusCode status,
                       String requestID,
                       ErrorCode error,
                       PSO pso) {
        super(errorMessages, status, requestID, error);
        m_pso = pso;
    }

    public PSO getPso() {
        return m_pso;
    }

    public void setPso(PSO pso) {
        m_pso = pso;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddResponse)) return false;
        if (!super.equals(o)) return false;

        final AddResponse addResponse = (AddResponse) o;

        if (m_pso != null ? !m_pso.equals(addResponse.m_pso) : addResponse.m_pso != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_pso != null ? m_pso.hashCode() : 0);
        return result;
    }
}
