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

import org.openspml.v2.msg.spml.ErrorCode;
import org.openspml.v2.msg.spml.PSO;
import org.openspml.v2.msg.spml.StatusCode;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="SearchResponseType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ResponseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="pso" type="spml:PSOType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;element name="iterator" type="spmlsearch:ResultsIteratorType" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class SearchResponse extends BasicResponse {

    private static final String code_id = "$Id: SearchResponse.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <element name="pso" type="spml:PSOType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_pso = new ArrayListWithType(PSO.class);

    // <element name="iterator" type="spmlsearch:ResultsIteratorType" minOccurs="0" />
    private ResultsIterator m_iterator = null; // optional

    public SearchResponse() { ; }

    public SearchResponse(String[] errorMessages,
                          StatusCode status,
                          String requestId,
                          ErrorCode errorCode,
                          PSO[] pso,
                          ResultsIterator iterator) {
        super(errorMessages, status, requestId, errorCode);
        if (pso != null) {
            m_pso.addAll(Arrays.asList(pso));
        }
        m_iterator = iterator;
    }

    public void addPSO(PSO pso) {
        if (pso != null) {
            m_pso.add(pso);
        }
    }

    public boolean removePSO(PSO pso) {
        if (pso != null) {
            return m_pso.remove(pso);
        }
        return false;
    }

    public void clearPSOs() {
        m_pso.clear();
    }

    public PSO[] getPSOs() {
        return (PSO[]) m_pso.toArray(new PSO[m_pso.size()]);
    }

    public ResultsIterator getIterator() {
        return m_iterator;
    }

    public void setIterator(ResultsIterator iterator) {
        m_iterator = iterator;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResponse)) return false;
        if (!super.equals(o)) return false;

        final SearchResponse searchResponse = (SearchResponse) o;

        if (m_iterator != null ? !m_iterator.equals(searchResponse.m_iterator) : searchResponse.m_iterator != null) return false;
        if (!m_pso.equals(searchResponse.m_pso)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_pso.hashCode();
        result = 29 * result + (m_iterator != null ? m_iterator.hashCode() : 0);
        return result;
    }
}
