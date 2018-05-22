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

import org.openspml.v2.msg.spml.ErrorCode;
import org.openspml.v2.msg.spml.StatusCode;

/**
 * <br>&lt;complexType name="ResetPasswordResponseType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ResponseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="pass" type="string" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 13, 2006
 */
public class ResetPasswordResponse extends BasicPasswordResponse {

    private static final String code_id = "$Id: ResetPasswordResponse.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    //* <element name="pass" type="string" minOccurs="0" />
    // collection or array to be an element
    private String[] m_password = new String[1]; // optional

    public ResetPasswordResponse() { ; }

    // full
    public ResetPasswordResponse(String[] errorMessages,
                                 StatusCode status,
                                 String requestId,
                                 ErrorCode errorCode,
                                 String password) {
        super(errorMessages, status, requestId, errorCode);
        m_password[0] = password;
    }

    public String getPassword() {
        return m_password[0];
    }

    public void setPassword(String password) {
        m_password[0] = password;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResetPasswordResponse)) return false;
        if (!super.equals(o)) return false;

        final ResetPasswordResponse resetPasswordResponse = (ResetPasswordResponse) o;

        if (m_password[0] != null ? !m_password[0].equals(resetPasswordResponse.m_password[0]) : resetPasswordResponse.m_password[0] != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_password[0] != null ? m_password[0].hashCode() : 0);
        return result;
    }
}
