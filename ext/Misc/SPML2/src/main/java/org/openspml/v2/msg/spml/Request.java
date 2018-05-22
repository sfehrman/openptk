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

import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.util.Spml2Exception;

// &lt;complexType name="RequestType"&gt;
// 	&lt;complexContent&gt;
// 		&lt;extension base="spml:ExtensibleType"&gt;
// 			&lt;attribute name="requestID" type="xsd:ID" use="optional"/&gt;
// 			&lt;attribute name="executionMode" type="spml:ExecutionModeType" use="optional"/&gt;
// 		&lt;/extension&gt;
// 	&lt;/complexContent&gt;
// &lt;/complexType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 26, 2006
 */
public abstract class Request extends ExtensibleMarshallable {

    public static final String code_id = "$Id: Request.java,v 1.2 2006/04/21 23:09:02 kas Exp $";

    // <attribute name="requestID" type="xsd:ID" use="optional"/>
    private String m_requestID = null;

    // <attribute name="executionMode" type="spml:ExecutionModeType" use="optional"/>
    private ExecutionMode m_executionMode = null;

    protected Request() {};
    
    protected Request(String requestId,
                      ExecutionMode executionMode) {
        m_requestID = requestId;
        m_executionMode = executionMode;
    }

    public String getRequestID() {
        return m_requestID;
    }

    public void setRequestID(String requestID) {
        m_requestID = requestID;
    }

    public ExecutionMode getExecutionMode() {
        return m_executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        m_executionMode = executionMode;
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        if (!super.equals(o)) return false;

        final Request request = (Request) o;

        if (m_executionMode != null ? !m_executionMode.equals(request.m_executionMode) : request.m_executionMode != null) return false;
        if (m_requestID != null ? !m_requestID.equals(request.m_requestID) : request.m_requestID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_requestID != null ? m_requestID.hashCode() : 0);
        result = 29 * result + (m_executionMode != null ? m_executionMode.hashCode() : 0);
        return result;
    }
}
