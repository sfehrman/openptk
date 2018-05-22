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

import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Request;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 15, 2006
 */
abstract class BasicRequest extends Request {

    private static final String code_id = "$Id: BasicRequest.java,v 1.5 2006/05/15 23:31:00 kas Exp $";

    // from CancelRequest
    // <attribute name="asyncRequestID" type="xsd:string" use="required"/>
    // from StatusRequest
    // <attribute name="asyncRequestID" type="xsd:string" use="optional"/>
    private transient boolean mIdRequired = false; // transient and/or prefix will keep this out of xml

    private String m_asyncRequestID = null;

    protected BasicRequest() { ; }

    protected BasicRequest(String requestId,
                           ExecutionMode executionMode,
                           String asyncRequestID,
                           boolean idRequired) {
        super(requestId, executionMode);
        if (idRequired)
            assert (asyncRequestID != null);
        m_asyncRequestID = asyncRequestID;
        mIdRequired = idRequired;
    }

    public String getAsyncRequestID() {
        return m_asyncRequestID;
    }

    public void setAsyncRequestID(String asyncRequestID) {
        if (mIdRequired)
            assert (asyncRequestID != null);
        m_asyncRequestID = asyncRequestID;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return PrefixAndNamespaceTuple.concatNamespacesInfo(super.getNamespacesInfo(),
                                    NamespaceDefinitions.getMarshallableNamespacesInfo());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicRequest)) return false;
        if (!super.equals(o)) return false;

        final BasicRequest asyncResponse = (BasicRequest) o;

        if (!m_asyncRequestID.equals(asyncResponse.m_asyncRequestID)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_asyncRequestID.hashCode();
        return result;
    }
}
