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
package org.openspml.v2.msg.spmlupdates;

import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spmlsearch.Query;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;
import java.util.List;

/**
 * <br>&lt;complexType name="UpdatesRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element ref="spmlsearch:query" minOccurs="0"/&gt;
 * <br>&lt;element name="updatedByCapability" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="updatedSince" type="xsd:dateTime" use="optional"/&gt;
 * <br>&lt;attribute name="token" type="xsd:string" use="optional"/&gt;
 * <br>&lt;attribute name="maxSelect" type="xsd:int" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class UpdatesRequest extends BasicRequest {

    private static final String code_id = "$Id: UpdatesRequest.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    //* <element ref="spmlsearch:query" minOccurs="0"/>
    private ListWithType m_query = new ArrayListWithType(Query.class);

    //* <element name="updatedByCapability" type="xsd:string" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_updatedByCapability = new ArrayListWithType(String.class);

    //* <attribute name="updatedSince" type="xsd:dateTime" use="optional"/>
    private String m_updatedSince = null; // TODO: dateTime

    //* <attribute name="token" type="xsd:string" use="optional"/>
    private String m_token = null;

    //* <attribute name="maxSelect" type="xsd:int" use="optional"/>
    private Integer m_maxSelect = null;

    public UpdatesRequest() { ; }

    public UpdatesRequest(String requestId,
                          ExecutionMode executionMode,
                          Query[] query,
                          String[] updatedByCapability,
                          String updatedSince,
                          String token,
                          Integer maxSelect) {
        super(requestId, executionMode);

        if (query != null) {
            m_query.addAll(Arrays.asList(query));
        }

        if (updatedByCapability != null) {
            m_updatedByCapability.addAll(Arrays.asList(updatedByCapability));
        }

        m_updatedSince = updatedSince;
        m_token = token;
        m_maxSelect = maxSelect;
    }

    public Query[] getQueries() {
        return (Query[]) m_query.toArray(new Query[m_query.size()]);
    }

    public void addQuery(Query query) {
        if (query != null) {
            m_query.add(query);
        }
    }

    public boolean removeQuery(Query query) {
        return m_query.remove(query);
    }

    public void clearQueries() {
        m_query.clear();
    }

    public String[] getUpdatedByCapabilities() {
        return (String[]) m_updatedByCapability.toArray(new String[m_updatedByCapability.size()]);
    }

    public void addUpdatedByCapability(List updatedByCapability) {
        if (updatedByCapability != null)
            m_updatedByCapability.add(updatedByCapability);
    }

    public boolean removeUpdatedByCapability(List updatedByCapability) {
        return m_updatedByCapability.remove(updatedByCapability);
    }

    public void clearUpdatedByCapabilities() {
        m_updatedByCapability.clear();
    }

    // TODO: dateTime
    public String getUpdatedSince() {
        return m_updatedSince;
    }

    public void setUpdatedSince(String updatedSince) {
        m_updatedSince = updatedSince;
    }

    public String getToken() {
        return m_token;
    }

    public void setToken(String token) {
        m_token = token;
    }

    public int getMaxSelect() {
        if (m_maxSelect == null) return 0;
        return m_maxSelect.intValue();
    }

    public void setMaxSelect(int maxSelect) {
        m_maxSelect = new Integer(maxSelect);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatesRequest)) return false;
        if (!super.equals(o)) return false;

        final UpdatesRequest updatesRequest = (UpdatesRequest) o;

        if (m_maxSelect != null ? !m_maxSelect.equals(updatesRequest.m_maxSelect) : updatesRequest.m_maxSelect != null) return false;
        if (!m_query.equals(updatesRequest.m_query)) return false;
        if (m_token != null ? !m_token.equals(updatesRequest.m_token) : updatesRequest.m_token != null) return false;
        if (!m_updatedByCapability.equals(updatesRequest.m_updatedByCapability)) return false;
        if (m_updatedSince != null ? !m_updatedSince.equals(updatesRequest.m_updatedSince) : updatesRequest.m_updatedSince != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_query.hashCode();
        result = 29 * result + m_updatedByCapability.hashCode();
        result = 29 * result + (m_updatedSince != null ? m_updatedSince.hashCode() : 0);
        result = 29 * result + (m_token != null ? m_token.hashCode() : 0);
        result = 29 * result + (m_maxSelect != null ? m_maxSelect.hashCode() : 0);
        return result;
    }
}


