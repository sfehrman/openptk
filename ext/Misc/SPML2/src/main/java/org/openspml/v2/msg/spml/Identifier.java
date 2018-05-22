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


//&lt;complexType name="IdentifierType"&gt;
//    &lt;complexContent&gt;
//        &lt;extension base="spml:ExtensibleType"&gt;
//            &lt;attribute name="ID" type="string" use="optional"/&gt;
//        &lt;/extension&gt;
//    &lt;/complexContent&gt;
//&lt;/complexType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 26, 2006
 */
public abstract class Identifier extends Extensible {

    public static final String code_id = "$Id: Identifier.java,v 1.2 2006/04/21 23:09:02 kas Exp $";

    // <attribute name="ID" type="string" use="optional"/>
    private String m_ID = null;

    protected Identifier() {
    }

    protected Identifier(String anID) {
        m_ID = anID;
    }

    public String getID() {
        return m_ID;
    }

    public void setID(String anID) {
        m_ID = anID;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;
        if (!super.equals(o)) return false;

        final Identifier identifier = (Identifier) o;

        if (m_ID != null ? !m_ID.equals(identifier.m_ID) : identifier.m_ID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_ID != null ? m_ID.hashCode() : 0);
        return result;
    }
}
