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

import org.openspml.v2.util.xml.ArrayListWithType;
import org.openspml.v2.util.xml.ListWithType;

import java.net.URI;
import java.util.Arrays;

/**
 * <br>&lt;complexType name="CapabilityType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="appliesTo" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="namespaceURI" type="anyURI" /&gt;
 * <br>&lt;attribute name="location" type="anyURI" use="optional" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 8, 2006
 */
public class Capability extends Extensible {

    private static final String code_id = "$Id: Capability.java,v 1.4 2006/04/25 21:22:09 kas Exp $";

    // <element name="appliesTo" type="spml:SchemaEntityRefType" minOccurs="0" maxOccurs="unbounded"/>
    private ListWithType m_appliesTo = new ArrayListWithType(SchemaEntityRef.class);

    // <attribute name="namespaceURI" type="anyURI" />
    private URI m_namespaceURI = null; // required

    // <attribute name="location" type="anyURI" use="optional" />
    private URI m_location = null;

    public Capability() { ; }

    public Capability(SchemaEntityRef[] appliesTo,
                      URI namespaceURI,
                      URI location) {
        if (appliesTo != null)
            m_appliesTo.addAll(Arrays.asList(appliesTo));
        assert (namespaceURI != null);
        m_namespaceURI = namespaceURI;
        m_location = location;
    }

    public SchemaEntityRef[] getAppliesTo() {
        return (SchemaEntityRef[]) m_appliesTo.toArray(new SchemaEntityRef[m_appliesTo.size()]);
    }

    public void addAppliesTo(SchemaEntityRef appliesTo) {
        assert (appliesTo != null);
        m_appliesTo.add(appliesTo);
    }

    public boolean removeAppliesTo(SchemaEntityRef appliesTo) {
        assert (appliesTo != null);
        return m_appliesTo.remove(appliesTo);
    }

    public void clearAppliesTo(SchemaEntityRef appliesTo) {
        m_appliesTo.clear();
    }

    public URI getNamespaceURI() {
        return m_namespaceURI;
    }

    public void setNamespaceURI(URI namespaceURI) {
        assert (namespaceURI != null);
        m_namespaceURI = namespaceURI;
    }

    public URI getLocation() {
        return m_location;
    }

    public void setLocation(URI location) {
        m_location = location;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capability)) return false;
        if (!super.equals(o)) return false;

        final Capability capability = (Capability) o;

        if (m_appliesTo != null ? !m_appliesTo.equals(capability.m_appliesTo) : capability.m_appliesTo != null) return false;
        if (m_location != null ? !m_location.equals(capability.m_location) : capability.m_location != null) return false;
        if (!m_namespaceURI.equals(capability.m_namespaceURI)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (m_appliesTo != null ? m_appliesTo.hashCode() : 0);
        result = 29 * result + m_namespaceURI.hashCode();
        result = 29 * result + (m_location != null ? m_location.hashCode() : 0);
        return result;
    }
}
