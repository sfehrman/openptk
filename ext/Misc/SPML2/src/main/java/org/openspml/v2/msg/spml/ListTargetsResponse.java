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

import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="ListTargetsResponseType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ResponseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="target" type="spml:TargetType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 8, 2006
 */
public class ListTargetsResponse extends Response {

    private static final String code_id = "$Id: ListTargetsResponse.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="target" type="spml:TargetType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_target = new ArrayListWithType(Target.class);

    public ListTargetsResponse() { ; }

    public ListTargetsResponse(String[] errorMessages,
                               StatusCode status,
                               String requestId,
                               ErrorCode errorCode,
                               Target[] targets) {
        super(errorMessages, status, requestId, errorCode);
        if (targets != null) {
            m_target.addAll(Arrays.asList(targets));
        }
    }

    public Target[] getTargets() {
        return (Target[]) m_target.toArray(new Target[m_target.size()]);
    }

    public void addTarget(Target target) {
        assert (target != null);
        m_target.add(target);
    }

    public boolean removeTarget(Target target) {
        assert (target != null);
        return m_target.remove(target);
    }

    public void clearTargets() {
        m_target.clear();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListTargetsResponse)) return false;
        if (!super.equals(o)) return false;

        final ListTargetsResponse listTargetsResponseType = (ListTargetsResponse) o;

        if (!m_target.equals(listTargetsResponseType.m_target)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_target.hashCode();
        return result;
    }
}
