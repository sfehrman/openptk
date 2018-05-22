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
package org.openspml.v2.msg.spmlbulk;

import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Modification;
import org.openspml.v2.msg.spmlsearch.Query;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="BulkModifyRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element ref="spmlsearch:query" /&gt;
 * <br>&lt;element name="modification" type="spml:ModificationType" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class BulkModifyRequest extends BasicRequest {

    private static final String code_id = "$Id: BulkModifyRequest.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    private ListWithType m_modification = new ArrayListWithType(Modification.class);

    public BulkModifyRequest() { ; }

    public BulkModifyRequest(String requestId,
                             ExecutionMode executionMode,
                             Query ref,
                             Modification[] modifications) {
        super(requestId, executionMode, ref);
        assert (modifications != null);
        assert (modifications.length != 0);
        m_modification.addAll(Arrays.asList(modifications));
    }

    public Modification[] getModifications() {
        return (Modification[]) m_modification.toArray(new Modification[m_modification.size()]);
    }

    public void addModification(Modification modification) {
        if (modification != null) {
            m_modification.add(modification);
        }
    }

    public boolean removeModification(Modification modification) {
        return m_modification.remove(modification);
    }

    public void clearModifications() {
        m_modification.clear();
    }

    public boolean isValid() {
        return super.isValid() && !m_modification.isEmpty();
    }
}
