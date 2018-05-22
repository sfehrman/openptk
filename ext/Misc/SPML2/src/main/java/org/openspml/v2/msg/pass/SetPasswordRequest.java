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
 * <br>&lt;complexType name="SetPasswordRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="psoID" type="spml:PSOIdentifierType" /&gt;
 * <br>&lt;element name="pass" type="string" /&gt;
 * <br>&lt;element name="currentPassword" type="string" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 13, 2006
 */
public class SetPasswordRequest extends BasicPasswordRequestWithPassword {

    private static final String code_id = "$Id: SetPasswordRequest.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="currentPassword" type="string" minOccurs="0" />
    // collection or array to be an element
    private String[] m_currentPassword = new String[1];  // collection or array to be an element

    public SetPasswordRequest() { ; }

    public SetPasswordRequest(String requestId,
                              ExecutionMode executionMode,
                              PSOIdentifier psoID,
                              String password,
                              String currentPassword) {
        super(requestId, executionMode, psoID, password);
        m_currentPassword[0] = currentPassword;
    }

    public String getCurrentPassword() {
        return m_currentPassword[0];
    }

    public void setCurrentPassword(String currentPassword) {
        m_currentPassword[0] = currentPassword;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SetPasswordRequest)) return false;
        if (!super.equals(o)) return false;

        final SetPasswordRequest setPasswordRequest = (SetPasswordRequest) o;

        if (m_currentPassword[0] != null ? !m_currentPassword[0].equals(setPasswordRequest.m_currentPassword[0]) : setPasswordRequest.m_currentPassword[0] != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_currentPassword[0] != null ? m_currentPassword[0].hashCode() : 0);
        return result;
    }
}
