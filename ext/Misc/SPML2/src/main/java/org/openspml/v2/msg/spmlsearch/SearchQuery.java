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

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.msg.spml.PSOIdentifier;
import org.openspml.v2.msg.spml.QueryClause;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * From the specification...
 *
 * <br>&lt;complexType name="SearchQueryType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;annotation&gt;
 * <br>&lt;documentation&gt;Open content is one or more instances of QueryClauseType (including SelectionType) or LogicalOperator.&lt;/documentation&gt;
 * <br>&lt;/documentation&gt;
 * <br>&lt;/annotation&gt;
 * <br>&lt;element name="basePsoID" type="spml:PSOIdentifierType"  minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="targetID" type="string" use="optional"/&gt;
 * <br>&lt;attribute name="scope" type="spmlsearch:ScopeType" use="optional"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 * 
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
abstract public class SearchQuery extends Extensible implements Marshallable {

    private static final String code_id = "$Id: SearchQuery.java,v 1.8 2006/08/30 18:02:59 kas Exp $";

    protected SearchQuery() {
    }

    // we have additional content that is not named (considered openContent)
    //   - as a list of QueryClause objects.
    private ListWithType m_queryClauses = new ArrayListWithType(QueryClause.class);

    // <element name="basePsoID" type="spml:PSOIdentifierType"  minOccurs="0" />
    private PSOIdentifier m_basePsoID = null; // optional

    // <attribute name="targetID" type="string" use="optional"/>
    private String m_targetID = null;

    // <attribute name="scope" type="spmlsearch:ScopeType" use="optional"/>
    private Scope m_scope = null;

    protected SearchQuery(QueryClause[] queryClauses,
                          PSOIdentifier basePsoID,
                          String targetID,
                          Scope scope) {
        assert (queryClauses != null);     // SMOKE spec
        assert (queryClauses.length != 0); // SMOKE spec
        for (int k = 0; k < queryClauses.length; k++) {
            if (queryClauses[k] != null) {
                m_queryClauses.add(queryClauses[k]);
            }
        }
        assert (!m_queryClauses.isEmpty());

        m_basePsoID = basePsoID;
        m_targetID = targetID;
        m_scope = scope;
    }


    public QueryClause[] getQueryClauses() {
        List temp = new ArrayList();
        temp.addAll(m_queryClauses);

        // we also want all the OpenContentElements that
        // are instanceof QueryClause... we should be able to
        // unmarshall them into the right container - but that's
        // difficult so we'll cheat.
        OpenContentElement[] oces = getOpenContentElements();
        for (int k = 0; k < oces.length; k++) {
            OpenContentElement oce = oces[k];
            if (oce instanceof QueryClause) {
                temp.add(oce);
            }
        }

        return (QueryClause[]) temp.toArray(new QueryClause[temp.size()]);
    }

    public void clearQueryClauses() {
        m_queryClauses.clear();
        OpenContentElement[] elements = getOpenContentElements();
        for (int k = 0; k < elements.length; k++) {
            OpenContentElement element = elements[k];
            if (element instanceof QueryClause) {
                removeOpenContentElement(element);
            }
        }
    }

    public void addQueryClause(QueryClause queryClause) {
        if (queryClause == null) return;
        if (queryClause instanceof OpenContentElement) {
            addOpenContentElement((OpenContentElement) queryClause);
        }
        else {
            m_queryClauses.add(queryClause);
        }
    }

    public boolean removeQueryClause(QueryClause queryClause) {
        if (queryClause == null) return false;
        if (queryClause instanceof OpenContentElement) {
            return removeOpenContentElement((OpenContentElement) queryClause);
        }
        else {
            return m_queryClauses.remove(queryClause);
        }
    }

    public PSOIdentifier getBasePsoID() {
        return m_basePsoID;
    }

    public void setBasePsoID(PSOIdentifier basePsoID) {
        m_basePsoID = basePsoID;
    }

    public String getTargetID() {
        return m_targetID;
    }

    public void setTargetID(String targetID) {
        m_targetID = targetID;
    }

    public Scope getScope() {
        return m_scope;
    }

    public void setScope(Scope scope) {
        m_scope = scope;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchQuery)) return false;
        if (!super.equals(o)) return false;

        final SearchQuery searchQuery = (SearchQuery) o;

        if (m_basePsoID != null ? !m_basePsoID.equals(searchQuery.m_basePsoID) : searchQuery.m_basePsoID != null) return false;
        if (!m_queryClauses.equals(searchQuery.m_queryClauses)) return false;
        if (m_scope != null ? !m_scope.equals(searchQuery.m_scope) : searchQuery.m_scope != null) return false;
        if (m_targetID != null ? !m_targetID.equals(searchQuery.m_targetID) : searchQuery.m_targetID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_queryClauses.hashCode();
        result = 29 * result + (m_basePsoID != null ? m_basePsoID.hashCode() : 0);
        result = 29 * result + (m_targetID != null ? m_targetID.hashCode() : 0);
        result = 29 * result + (m_scope != null ? m_scope.hashCode() : 0);
        return result;
    }

    // Marshallable stuff
    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        Object[] ours = PrefixAndNamespaceTuple.concatNamespacesInfo(
                super.getNamespacesInfo(),
                NamespaceDefinitions.getMarshallableNamespacesInfo());
        Set all = new LinkedHashSet(Arrays.asList(ours));
        Iterator iter = m_queryClauses.iterator();
        while (iter.hasNext()) {
            QueryClause qc = (QueryClause) iter.next();
            all.addAll(Arrays.asList(qc.getNamespacesInfo()));
        }
        return (PrefixAndNamespaceTuple[]) all.toArray(new PrefixAndNamespaceTuple[all.size()]);
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    public boolean isValid() {
        return true;
    }
}
