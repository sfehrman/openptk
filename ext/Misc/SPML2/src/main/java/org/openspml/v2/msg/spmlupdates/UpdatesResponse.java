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

import org.openspml.v2.msg.spml.ErrorCode;
import org.openspml.v2.msg.spml.StatusCode;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="UpdatesResponseType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ResponseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="update" type="spmlupdates:UpdateType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;element name="iterator" type="spmlupdates:ResultsIteratorType" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="token" type="xsd:string" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class UpdatesResponse extends BasicResponse {

    private static final String code_id = "$Id: UpdatesResponse.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    //* <element name="update" type="spmlupdates:UpdateType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_update = new ArrayListWithType(Update.class);

    //* <element name="iterator" type="spmlupdates:ResultsIteratorType" minOccurs="0" />
    private ResultsIterator m_iterator = null; //optional

    //* <attribute name="token" type="xsd:string" use="optional"/>
    private String m_token = null;

    public UpdatesResponse() { ; }

    public UpdatesResponse(String[] errorMessages,
                           StatusCode status,
                           String requestId,
                           ErrorCode errorCode,
                           Update[] update,
                           ResultsIterator iterator, String token) {
        super(errorMessages, status, requestId, errorCode);
        if (update != null) {
            m_update.addAll(Arrays.asList(update));
        }
        m_iterator = iterator;
        m_token = token;
    }

    public Update[] getUpdates() {
        return (Update[]) m_update.toArray(new Update[m_update.size()]);
    }

    public void addUpdate(Update update) {
        if (update != null) {
            m_update.add(update);
        }
    }

    public boolean removeUpdate(Update update) {
        return m_update.remove(update);
    }

    public void clearUpdates() {
        m_update.clear();
    }

    public ResultsIterator getIterator() {
        return m_iterator;
    }

    public void setIterator(ResultsIterator iterator) {
        m_iterator = iterator;
    }

    public String getToken() {
        return m_token;
    }

    public void setToken(String token) {
        m_token = token;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatesResponse)) return false;
        if (!super.equals(o)) return false;

        final UpdatesResponse updatesResponse = (UpdatesResponse) o;

        if (m_iterator != null ? !m_iterator.equals(updatesResponse.m_iterator) : updatesResponse.m_iterator != null) return false;
        if (m_token != null ? !m_token.equals(updatesResponse.m_token) : updatesResponse.m_token != null) return false;
        if (!m_update.equals(updatesResponse.m_update)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_update.hashCode();
        result = 29 * result + (m_iterator != null ? m_iterator.hashCode() : 0);
        result = 29 * result + (m_token != null ? m_token.hashCode() : 0);
        return result;
    }
}
