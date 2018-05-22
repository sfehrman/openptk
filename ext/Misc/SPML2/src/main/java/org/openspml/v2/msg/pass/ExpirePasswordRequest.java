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
package org.openspml.v2.msg.pass;

import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.PSOIdentifier;

/**
 * <br>&lt;complexType name="ExpirePasswordRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="remainingLogins" type="int" use="optional" default="1" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 13, 2006
 */
public class ExpirePasswordRequest extends BasicPasswordRequest {

    private static final String code_id = "$Id: ExpirePasswordRequest.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <attribute name="remainingLogins" type="int" use="optional" default="1" />
    private Integer m_remainingLogins = null; // optional

    public ExpirePasswordRequest() { ; }

    public ExpirePasswordRequest(String requestId,
                                 ExecutionMode executionMode,
                                 PSOIdentifier psoID,
                                 Integer remainingLogins) {
        super(requestId, executionMode, psoID);
        m_remainingLogins = remainingLogins;
    }

    public int getRemainingLogins() {
        return m_remainingLogins != null ? m_remainingLogins.intValue() : 1;
    }

    public void setRemainingLogins(int remainingLogins) {
        m_remainingLogins = new Integer(remainingLogins);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpirePasswordRequest)) return false;
        if (!super.equals(o)) return false;

        final ExpirePasswordRequest expirePasswordRequest = (ExpirePasswordRequest) o;

        if (m_remainingLogins != null ? !m_remainingLogins.equals(expirePasswordRequest.m_remainingLogins) : expirePasswordRequest.m_remainingLogins != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_remainingLogins != null ? m_remainingLogins.hashCode() : 0);
        return result;
    }
}
