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
package org.openspml.v2.msg.spmlbatch;

import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.spml.BatchableRequest;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <br>&lt;complexType name="BatchRequestType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:RequestType"&gt;
 * <br>&lt;annotation&gt;
 * <br>&lt;documentation&gt;Elements that extend spml:RequestType&lt;/documentation&gt;
 * <br>&lt;/annotation&gt;
 * <br>&lt;attribute name="processing" type="spmlbatch:ProcessingType" use="optional" default="sequential"/&gt;
 * <br>&lt;attribute name="onError" type="spmlbatch:OnErrorType" use="optional" default="exit"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class BatchRequest extends Request {

    private static final String code_id = "$Id: BatchRequest.java,v 1.7 2006/08/30 18:02:59 kas Exp $";

    // TODO: Instead of Request.class -> BatchableRequest.class
    
    // These are the request objects... we ask them for namespace info....
    private ListWithType m_request = new ArrayListWithType(BatchableRequest.class);

    // <attribute name="processing" type="spmlbatch:ProcessingType" use="optional" default="sequential"/>
    private Processing m_processing = Processing.SEQUENTIAL;

    // <attribute name="onError" type="spmlbatch:OnErrorType" use="optional" default="exit"/>
    private OnError m_onError = OnError.EXIT;

    public BatchRequest() { ; }

    public BatchRequest(String requestId,
                        ExecutionMode executionMode,
                        Processing processing,
                        OnError onError) {
        super(requestId, executionMode);
        m_processing = processing;
        m_onError = onError;
    }

    // Take objects of type BatchableRequest
    public void addRequest(BatchableRequest req) {
        if (req != null)
            m_request.add(req);
    }

    public boolean removeRequest(BatchableRequest req) {
        return m_request.remove(req);
    }

    public void clearRequests() {
        m_request.clear();
    }

    /**
     * This returns a List (unmodifiable) of the
     * individual requests.
     * @return a List of BatchableRequest objects.
     */
    public List getRequests() {
        return Collections.unmodifiableList(m_request);
    }

    public Processing getProcessing() {
        return m_processing;
    }

    public void setProcessing(Processing processing) {
        m_processing = processing;
    }

    public OnError getOnError() {
        return m_onError;
    }

    public void setOnError(OnError onError) {
        m_onError = onError;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        PrefixAndNamespaceTuple[] supers = super.getNamespacesInfo();
        PrefixAndNamespaceTuple[] ours =
                PrefixAndNamespaceTuple.concatNamespacesInfo(supers,
                                                             NamespaceDefinitions.getMarshallableNamespacesInfo());

        Set all = null;
        if (ours!=null)
            all = new LinkedHashSet(Arrays.asList(ours));
        else
        	all = new LinkedHashSet();
        if (m_request!=null) {
            Iterator iter = m_request.iterator();
            while (iter.hasNext()) {
                Request r = (Request) iter.next();
                PrefixAndNamespaceTuple[] rqni = r.getNamespacesInfo();
                if (rqni!=null)
                	all.addAll(Arrays.asList(rqni));
            }
        }
        return (PrefixAndNamespaceTuple[]) all.toArray(new PrefixAndNamespaceTuple[all.size()]);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchRequest)) return false;
        if (!super.equals(o)) return false;

        final BatchRequest batchRequest = (BatchRequest) o;

        if (m_onError != null ? !m_onError.equals(batchRequest.m_onError) : batchRequest.m_onError != null) return false;
        if (m_processing != null ? !m_processing.equals(batchRequest.m_processing) : batchRequest.m_processing != null) return false;
        if (!m_request.equals(batchRequest.m_request)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_request.hashCode();
        result = 29 * result + (m_processing != null ? m_processing.hashCode() : 0);
        result = 29 * result + (m_onError != null ? m_onError.hashCode() : 0);
        return result;
    }
}
