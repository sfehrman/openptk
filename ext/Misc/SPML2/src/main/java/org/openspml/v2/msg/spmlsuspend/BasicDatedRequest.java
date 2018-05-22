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
package org.openspml.v2.msg.spmlsuspend;

import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.PSOIdentifier;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 15, 2006
 */
abstract class BasicDatedRequest extends BasicRequest {

    private static final String code_id = "$Id: BasicDatedRequest.java,v 1.3 2006/04/21 23:09:02 kas Exp $";

    // <attribute name="effectiveDate" type="dateTime" use="optional"/>
    private String m_effectiveDate = null; /*TODO type="dateTime"*/

    protected BasicDatedRequest() { ; }

    protected BasicDatedRequest(String requestId,
                                ExecutionMode executionMode, PSOIdentifier psoID) {
        super(requestId, executionMode, psoID);
    }

    public String getEffectiveDate() {
        return m_effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        m_effectiveDate = effectiveDate;
    }

    // TODO add some util methods so the users don't have to
    // format the date string themselves.

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicDatedRequest)) return false;
        if (!super.equals(o)) return false;

        final BasicDatedRequest basicDatedRequest = (BasicDatedRequest) o;

        if (m_effectiveDate != null ? !m_effectiveDate.equals(basicDatedRequest.m_effectiveDate) : basicDatedRequest.m_effectiveDate != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_effectiveDate != null ? m_effectiveDate.hashCode() : 0);
        return result;
    }
}
