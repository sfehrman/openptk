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
import org.openspml.v2.msg.spml.ReturnData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <br>&lt;complexType name="SearchRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="query" type="spmlsearch:SearchQueryType" minOccurs="0" /&gt;
 * <br>&lt;element name="includeDataForCapability" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/&gt;
 * <br>&lt;attribute name="maxSelect" type="xsd:int" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class SearchRequest extends BasicRequest {

    private static final String code_id = "$Id: SearchRequest.java,v 1.5 2006/06/27 00:47:19 kas Exp $";

    // <element name="query" type="spmlsearch:SearchQueryType" minOccurs="0" />
    private Query m_query = null;

    // <element name="includeDataForCapability" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/>
    private List m_includeDataForCapability = new ArrayList();

    // <attribute name="returnData" type="spml:ReturnDataType" use="optional" default="everything"/>
    private ReturnData m_returnData = null;

    // <attribute name="maxSelect" type="xsd:int" use="optional"/>
    private Integer m_maxSelect = null;

    public SearchRequest() { ; }

    public SearchRequest(String requestId,
                         ExecutionMode executionMode,
                         Query query,
                         String[] includeDataForCapability,
                         ReturnData returnData,
                         Integer maxSelect) {
        super(requestId, executionMode);
        m_query = query;
        if (includeDataForCapability != null) {
            m_includeDataForCapability.addAll(Arrays.asList(includeDataForCapability));
        }
        m_returnData = returnData;
        m_maxSelect = maxSelect;
    }

    public SearchRequest(String requestId,
                         ExecutionMode executionMode,
                         Query query,
                         String[] includeDataForCapability,
                         ReturnData returnData,
                         int maxSelect) {
        this(requestId, executionMode, query,
             includeDataForCapability, returnData,
             new Integer(maxSelect));
    }

    public SearchRequest(String requestId,
                         ExecutionMode executionMode,
                         Query query,
                         String[] includeDataForCapability,
                         ReturnData returnData) {
        this(requestId, executionMode, query,
             includeDataForCapability, returnData,
             null);
    }

    public Query getQuery() {
        return m_query;
    }

    public void setQuery(Query query) {
        m_query = query;
    }

    public String[] getIncludeDataForCapability() {
        return (String[]) m_includeDataForCapability.toArray(new String[m_includeDataForCapability.size()]);
    }

    public void clearIncludeDataForCapability() {
        m_includeDataForCapability.clear();
    }

    public void addIncludeDataForCapability(String includeDataForCapability) {
        if (includeDataForCapability == null) return;
        m_includeDataForCapability.add(includeDataForCapability);
    }

    public boolean removeIncludeDataForCapability(String includeDataForCapability) {
        if (includeDataForCapability == null) return false;
        return m_includeDataForCapability.remove(includeDataForCapability);
    }

    public ReturnData getReturnData() {
        return m_returnData;
    }

    public void setReturnData(ReturnData returnData) {
        m_returnData = returnData;
    }

    public int getMaxSelect() {
        return m_maxSelect != null ? m_maxSelect.intValue() : 0;
    }

    public void setMaxSelect(int maxSelect) {
        m_maxSelect = new Integer(maxSelect);
    }

    public void useDefaultMaxSelect() {
        setMaxSelect(0);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchRequest)) return false;
        if (!super.equals(o)) return false;

        final SearchRequest searchRequest = (SearchRequest) o;

        if (!m_includeDataForCapability.equals(searchRequest.m_includeDataForCapability)) return false;
        if (m_maxSelect != null ? !m_maxSelect.equals(searchRequest.m_maxSelect) : searchRequest.m_maxSelect != null) return false;
        if (m_query != null ? !m_query.equals(searchRequest.m_query) : searchRequest.m_query != null) return false;
        if (m_returnData != null ? !m_returnData.equals(searchRequest.m_returnData) : searchRequest.m_returnData != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_query != null ? m_query.hashCode() : 0);
        result = 29 * result + m_includeDataForCapability.hashCode();
        result = 29 * result + (m_returnData != null ? m_returnData.hashCode() : 0);
        result = 29 * result + (m_maxSelect != null ? m_maxSelect.hashCode() : 0);
        return result;
    }
}
