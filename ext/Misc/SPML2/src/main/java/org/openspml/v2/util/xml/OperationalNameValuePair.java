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
package org.openspml.v2.util.xml;

import org.openspml.v2.msg.PrefixAndNamespaceTuple;

/**
 * @author Kent Spaulding
 */
public class OperationalNameValuePair extends BasicOCEAndMarshallable {

    private String m_name = null;
    private String m_value = null;

    public OperationalNameValuePair() {
    }

    public OperationalNameValuePair(String name, String value) {
        m_name = name;
        m_value = value;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        if (name != null)
            m_name = name;
    }

    public String getValue() {
        return m_value;
    }

    public void setValue(String value) {
        m_value = value;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return new PrefixAndNamespaceTuple[]{
            new PrefixAndNamespaceTuple(OperationalNameValuePairCreator.OP_ATTR_PREFIX,
                                        OperationalNameValuePairCreator.OP_ATTR_URI),
        };
    }

    public boolean isValid() {
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationalNameValuePair)) return false;

        final OperationalNameValuePair operationalNameValuePair = (OperationalNameValuePair) o;

        if (m_name != null ? !m_name.equals(operationalNameValuePair.m_name) : operationalNameValuePair.m_name != null) return false;
        if (m_value != null ? !m_value.equals(operationalNameValuePair.m_value) : operationalNameValuePair.m_value != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (m_name != null ? m_name.hashCode() : 0);
        result = 29 * result + (m_value != null ? m_value.hashCode() : 0);
        return result;
    }
}
