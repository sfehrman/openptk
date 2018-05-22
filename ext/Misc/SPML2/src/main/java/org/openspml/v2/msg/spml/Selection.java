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
package org.openspml.v2.msg.spml;

import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.util.Arrays;

/**
 * <br>&lt;complexType name="SelectionType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:QueryClauseType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="namespacePrefixMap" type="spml:NamespacePrefixMappingType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="path" type="string" use="required"/&gt;
 * <br>&lt;attribute name="namespaceURI" type="string" use="required"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class Selection extends QueryClause {

    private static final String code_id = "$Id: Selection.java,v 1.4 2006/06/27 17:23:56 kas Exp $";

    // <element name="namespacePrefixMap" type="spml:NamespacePrefixMappingType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_namespacePrefixMap = new ArrayListWithType(NamespacePrefixMapping.class);

    // <attribute name="path" type="string" use="required"/>
    private String m_path = null;

    // <attribute name="namespaceURI" type="string" use="required"/>
    private String m_namespaceURI = null;

    public Selection() { ; }
    
    public Selection(NamespacePrefixMapping[] prefixes,
                     String path,
                     String namespaceURI) {
        if (prefixes != null)
            m_namespacePrefixMap.addAll(Arrays.asList(prefixes));

        assert (path != null);
        assert (namespaceURI != null);
        m_path = path;
        m_namespaceURI = namespaceURI;
    }

    public String getElementName() {
        return "select";
    }

    public NamespacePrefixMapping[] getNamespacePrefixMaps() {
        return (NamespacePrefixMapping[]) m_namespacePrefixMap.toArray(
                new NamespacePrefixMapping[m_namespacePrefixMap.size()]);
    }

    public void addNamespacePrefixMap(NamespacePrefixMapping nspm) {
        assert (nspm != null);
        m_namespacePrefixMap.add(nspm);
    }

    public boolean removeNamespacePrefixMap(NamespacePrefixMapping nspm) {
        assert (nspm != null);
        return m_namespacePrefixMap.remove(nspm);
    }

    public void clearNamespacePrefixMap() {
        m_namespacePrefixMap.clear();
    }

    public String getPath() {
        return m_path;
    }

    public void setPath(String path) {
        assert (path != null);
        m_path = path;
    }

    public String getNamespaceURI() {
        return m_namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        assert (namespaceURI != null);
        m_namespaceURI = namespaceURI;
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Selection)) return false;
        if (!super.equals(o)) return false;

        final Selection selectionType = (Selection) o;

        if (!m_namespacePrefixMap.equals(selectionType.m_namespacePrefixMap)) return false;
        if (!m_namespaceURI.equals(selectionType.m_namespaceURI)) return false;
        if (!m_path.equals(selectionType.m_path)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_namespacePrefixMap.hashCode();
        result = 29 * result + m_path.hashCode();
        result = 29 * result + m_namespaceURI.hashCode();
        return result;
    }
}
