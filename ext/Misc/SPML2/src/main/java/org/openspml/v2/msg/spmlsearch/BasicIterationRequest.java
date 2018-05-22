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
package org.openspml.v2.msg.spmlsearch;

import org.openspml.v2.msg.spml.ExecutionMode;

/**
 * <br>&lt;pre&gt;
 * <br>&lt;complexType name="CloseIteratorRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="iterator" type="spmlsearch:ResultsIteratorType"/&gt;
 * ...
 * <br>&lt;/pre&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
abstract class BasicIterationRequest extends BasicRequest {

    private static final String code_id = "$Id: BasicIterationRequest.java,v 1.2 2006/04/21 23:09:02 kas Exp $";

    //* <element name="iterator" type="spmlsearch:ResultsIteratorType"/>
    private ResultsIterator m_iterator = null; // required

    protected BasicIterationRequest() { ; }

    public BasicIterationRequest(String requestId,
                                 ExecutionMode executionMode,
                                 ResultsIterator iterator) {
        super(requestId, executionMode);
        assert (iterator != null);
        m_iterator = iterator;
    }

    public ResultsIterator getIterator() {
        return m_iterator;
    }

    public void setIterator(ResultsIterator iterator) {
        assert (iterator != null);
        m_iterator = iterator;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicIterationRequest)) return false;
        if (!super.equals(o)) return false;

        final BasicIterationRequest closeIteratorRequest = (BasicIterationRequest) o;

        if (m_iterator != null ? !m_iterator.equals(closeIteratorRequest.m_iterator) : closeIteratorRequest.m_iterator != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_iterator != null ? m_iterator.hashCode() : 0);
        return result;
    }
}
