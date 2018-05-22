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

import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spmlsearch.Query;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * From both of the request types in this package.
 * // &lt;element ref="spmlsearch:query" /&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 15, 2006
 */
abstract class BasicRequest extends Request {

    private static final String code_id = "$Id: BasicRequest.java,v 1.5 2006/08/01 21:46:54 rfrech Exp $";

    // <element ref="spmlsearch:query" />
    private Query m_ref = null; // required

    protected BasicRequest() { ; }

    protected BasicRequest(String requestId,
                           ExecutionMode executionMode,
                           Query ref) {
        super(requestId, executionMode);
        assert (ref != null);
        m_ref = ref;
    }

    public Query getQuery() {
    	return m_ref;
    }
    
    public void setQuery(Query _ref) {
    	m_ref = _ref;
    }
    
    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        PrefixAndNamespaceTuple[] ours =
                PrefixAndNamespaceTuple.concatNamespacesInfo(super.getNamespacesInfo(),
                                                             NamespaceDefinitions.getMarshallableNamespacesInfo());

        // walk over all the items in ref and find the tuples...
        Set all = new LinkedHashSet(Arrays.asList(ours));
        if (m_ref != null) {
            all.addAll(Arrays.asList(m_ref.getNamespacesInfo()));
        }
        return (PrefixAndNamespaceTuple[]) all.toArray(new PrefixAndNamespaceTuple[all.size()]);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicRequest)) return false;
        if (!super.equals(o)) return false;

        final BasicRequest basicRequest = (BasicRequest) o;

        if (m_ref != null ? !m_ref.equals(basicRequest.m_ref) : basicRequest.m_ref != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_ref != null ? m_ref.hashCode() : 0);
        return result;
    }
}
