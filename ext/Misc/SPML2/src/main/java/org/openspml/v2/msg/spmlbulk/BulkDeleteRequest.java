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
import org.openspml.v2.msg.spmlsearch.Query;

/**
 * <br>&lt;complexType name="BulkDeleteRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element ref="spmlsearch:query" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="recursive" type="boolean" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class BulkDeleteRequest extends BasicRequest {

    private static final String code_id = "$Id: BulkDeleteRequest.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <attribute name="recursive" type="boolean" use="optional"/>
    private Boolean m_recursive = null;

    protected BulkDeleteRequest(String requestId,
                                ExecutionMode executionMode,
                                Query ref,
                                Boolean recursive) {
        super(requestId, executionMode, ref);
        m_recursive = recursive;
    }

    public BulkDeleteRequest() { ; }

    public BulkDeleteRequest(String requestId,
                             ExecutionMode executionMode,
                             Query ref) {
        this(requestId, executionMode, ref, null);
    }

    public BulkDeleteRequest(String requestId,
                             ExecutionMode executionMode,
                             Query ref,
                             boolean recursive) {
        this(requestId, executionMode, ref, new Boolean(recursive));
    }

    public boolean getRecursive() {
        return m_recursive == null ? false : m_recursive.booleanValue();
    }

    public void setRecursive(boolean recursive) {
        m_recursive = new Boolean(recursive);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BulkDeleteRequest)) return false;
        if (!super.equals(o)) return false;

        final BulkDeleteRequest bulkDeleteRequest = (BulkDeleteRequest) o;

        if (m_recursive != null ? !m_recursive.equals(bulkDeleteRequest.m_recursive) : bulkDeleteRequest.m_recursive != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_recursive != null ? m_recursive.hashCode() : 0);
        return result;
    }
}
