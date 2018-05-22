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

/**
 * <br>&lt;complexType name="ListTargetsRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;attribute name="profile" type="anyURI" use="optional" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 8, 2006
 */
public class ListTargetsRequest extends Request {

    private static final String code_id = "$Id: ListTargetsRequest.java,v 1.2 2006/04/21 23:09:02 kas Exp $";

    // <attribute name="profile" type="anyURI" use="optional" />
    private URI m_profile = null;

    public ListTargetsRequest() { ; }

    public ListTargetsRequest(String requestId,
                              ExecutionMode executionMode,
                              URI profile) {
        super(requestId, executionMode);
        m_profile = profile;
    }

    public URI getProfile() {
        return m_profile;
    }

    public void setProfile(URI profile) {
        m_profile = profile;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListTargetsRequest)) return false;
        if (!super.equals(o)) return false;

        final ListTargetsRequest listTargetsRequestType = (ListTargetsRequest) o;

        if (m_profile != null ? !m_profile.equals(listTargetsRequestType.m_profile) : listTargetsRequestType.m_profile != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_profile != null ? m_profile.hashCode() : 0);
        return result;
    }
}
