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
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 13, 2006
 */
abstract class BasicPasswordRequestWithPassword extends BasicPasswordRequest {

    private static final String code_id = "$Id: BasicPasswordRequestWithPassword.java,v 1.2 2006/04/21 23:09:02 kas Exp $";

    // <element name="pass" type="string" />  - USED IN SUBCLASSES
    // collection or array type makes this be treated as an element
    private String[] m_password = new String[1]; // required

    protected BasicPasswordRequestWithPassword() {
        super();
    }

    protected BasicPasswordRequestWithPassword(String requestId,
                                               ExecutionMode executionMode,
                                               PSOIdentifier psoID,
                                               String password) {
        super(requestId, executionMode, psoID);
        assert (password != null);
        m_password[0] = password;
    }

    public String getPassword() {
        return m_password[0];
    }

    public void setPassword(String password) {
        assert (password != null);
        m_password[0] = password;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicPasswordRequestWithPassword)) return false;
        if (!super.equals(o)) return false;

        final BasicPasswordRequestWithPassword basicPasswordRequestWithPassword = (BasicPasswordRequestWithPassword) o;

        if (m_password[0] != null ? !m_password[0].equals(basicPasswordRequestWithPassword.m_password[0]) : basicPasswordRequestWithPassword.m_password[0] != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_password[0] != null ? m_password[0].hashCode() : 0);
        return result;
    }
}
