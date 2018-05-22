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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//&lt;complexType name="ResponseType"&gt;
//    &lt;complexContent&gt;
//        &lt;extension base="spml:ExtensibleType"&gt;
//            &lt;sequence&gt;
//                        &lt;element name="errorMessage" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/&gt;
//            &lt;/sequence&gt;
//            &lt;attribute name="status" type="spml:StatusCodeType" use="required"/&gt;
//            &lt;attribute name="requestID" type="xsd:ID" use="optional"/&gt;
//            &lt;attribute name="error" type="spml:ErrorCode" use="optional"/&gt;
//        &lt;/extension&gt;
//    &lt;/complexContent&gt;
//&lt;/complexType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 26, 2006
 */
public class Response extends ExtensibleMarshallable {

    public static final String code_id = "$Id: Response.java,v 1.6 2006/06/29 21:01:21 rfrech Exp $";

    //  <element name="errorMessage" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/>
    private List m_errorMessage = new ArrayList();

    // <attribute name="status" type="spml:StatusCodeType" use="required"/>
    private StatusCode m_status = null;

    // <attribute name="requestID" type="xsd:ID" use="optional"/>
    private String m_requestID = null;

    // <attribute name="error" type="spml:ErrorCode" use="optional"/>
    private ErrorCode m_error = null;

    public Response() { ; }

    protected Response(String[] errorMessages,
                       StatusCode status,
                       String requestId,
                       ErrorCode errorCode) {
        if (errorMessages != null)
            m_errorMessage.addAll(Arrays.asList(errorMessages));

        assert (status != null);
        m_status = status;

        m_requestID = requestId;
        m_error = errorCode;
    }

    public Response(StatusCode status) {
        this(null, status, null, null);
    }

    public Response(StatusCode status,
    		        String requestId) {
        this(null, status, requestId, null);
    }

    public String[] getErrorMessages() {
        return (String[]) m_errorMessage.toArray(new String[m_errorMessage.size()]);
    }

    public void addErrorMessage(String errorMessage) {
        assert (errorMessage != null);
        m_errorMessage.add(errorMessage);
    }

    public boolean removeErrorMessage(String errorMessage) {
        assert (errorMessage != null);
        return m_errorMessage.remove(errorMessage);
    }

    public void clearErrorMessages() {
        m_errorMessage.clear();
    }

    public StatusCode getStatus() {
        return m_status;
    }

    public void setStatus(StatusCode status) {
        m_status = status;
        assert (m_status != null);
    }

    public String getRequestID() {
        return m_requestID;
    }

    public void setRequestID(String requestID) {
        m_requestID = requestID;
    }

    public ErrorCode getError() {
        return m_error;
    }

    public void setError(ErrorCode error) {
        m_error = error;
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Response)) return false;
        if (!super.equals(o)) return false;

        final Response response = (Response) o;

        if (m_error != null ? !m_error.equals(response.m_error) : response.m_error != null) return false;
        if (m_errorMessage != null ? !m_errorMessage.equals(response.m_errorMessage) : response.m_errorMessage != null) return false;
        if (m_requestID != null ? !m_requestID.equals(response.m_requestID) : response.m_requestID != null) return false;
        if (m_status != null ? !m_status.equals(response.m_status) : response.m_status != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_errorMessage != null ? m_errorMessage.hashCode() : 0);
        result = 29 * result + (m_status != null ? m_status.hashCode() : 0);
        result = 29 * result + (m_requestID != null ? m_requestID.hashCode() : 0);
        result = 29 * result + (m_error != null ? m_error.hashCode() : 0);
        return result;
    }
}
