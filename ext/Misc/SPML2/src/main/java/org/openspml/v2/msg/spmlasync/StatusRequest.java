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
package org.openspml.v2.msg.spmlasync;

import org.openspml.v2.msg.spml.ExecutionMode;


/**
 * <br>&lt;complexType name="StatusRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;attribute name="returnResults" type="xsd:boolean" use="optional"  default="false" /&gt;
 * <br>&lt;attribute name="asyncRequestID" type="xsd:string" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 15, 2006
 */
public class StatusRequest extends BasicRequest {

    private static final String code_id = "$Id: StatusRequest.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    public StatusRequest() { ; }

    // <attribute name="returnResults" type="xsd:boolean" use="optional"  default="false" />
    private Boolean m_returnResults = null; // optional - false

    public StatusRequest(String requestId,
                         ExecutionMode executionMode,
                         String asyncRequestID,
                         boolean returnResults) {
        super(requestId, executionMode, asyncRequestID, false);
        m_returnResults = new Boolean(returnResults);
    }

    public boolean getReturnResults() {
        if (m_returnResults == null) return false;
        return m_returnResults.booleanValue();
    }

    public void setReturnResults(boolean returnResults) {
        m_returnResults = new Boolean(returnResults);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatusRequest)) return false;
        if (!super.equals(o)) return false;

        final StatusRequest statusRequest = (StatusRequest) o;

        if (m_returnResults != null ? !m_returnResults.equals(statusRequest.m_returnResults) : statusRequest.m_returnResults != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_returnResults != null ? m_returnResults.hashCode() : 0);
        return result;
    }
}

