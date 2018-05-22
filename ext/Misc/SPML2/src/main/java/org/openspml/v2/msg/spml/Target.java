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

/**
 * <br>&lt;complexType name="TargetType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;sequence&gt;
 * <br>&lt;element name="schema" type="spml:SchemaType" maxOccurs="unbounded"/&gt;
 * <br>&lt;element name="capabilities" type="spml:CapabilitiesListType" minOccurs="0" /&gt;
 * <br>&lt;/sequence&gt;
 * <br>&lt;attribute name="targetID" type="string" use="optional"/&gt;
 * <br>&lt;attribute name="profile" type="anyURI" use="optional" /&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 8, 2006
 */
public class Target extends Extensible {

    private static final String code_id = "$Id: Target.java,v 1.4 2006/04/26 19:54:55 kas Exp $";

    // <element name="schema" type="spml:SchemaType" maxOccurs="unbounded"/>
    private ListWithType m_schema = new ArrayListWithType(Schema.class);

    // <element name="capabilities" type="spml:CapabilitiesListType" minOccurs="0" />
    private CapabilitiesList m_capabilities = null;

    // <attribute name="targetID" type="string" use="optional"/>
    private String m_targetID = null;

    // <attribute name="profile" type="anyURI" use="optional" />
    private URI m_profile = null;

    public Target() { ; }

    public Target(Schema[] schema,
                  CapabilitiesList capabilities,
                  String targetID,
                  URI profile) {
        assert (schema != null);
        assert (schema.length > 0);
        for (int k = 0; k < schema.length; k++) {
            assert (schema[k] != null);
            m_schema.add(schema[k]);
        }

        m_capabilities = capabilities;
        m_targetID = targetID;
        m_profile = profile;
    }

    public Schema[] getSchemas() {
        return (Schema[]) m_schema.toArray(new Schema[m_schema.size()]);
    }

    public void addSchema(Schema schema) {
        assert (schema != null);
        m_schema.add(schema);
    }

    public boolean removeSchema(Schema schema) {
        assert (schema != null);
        return m_schema.remove(schema);
    }

    public void clearSchemas() {
        m_schema.clear();
    }

    public CapabilitiesList getCapabilities() {
        return m_capabilities;
    }

    public void setCapabilities(CapabilitiesList capabilities) {
        m_capabilities = capabilities;
    }

    public String getTargetID() {
        return m_targetID;
    }

    public void setTargetID(String targetID) {
        m_targetID = targetID;
    }

    public URI getProfile() {
        return m_profile;
    }

    public void setProfile(URI profile) {
        m_profile = profile;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Target)) return false;
        if (!super.equals(o)) return false;

        final Target target = (Target) o;

        if (m_capabilities != null ? !m_capabilities.equals(target.m_capabilities) : target.m_capabilities != null) return false;
        if (m_profile != null ? !m_profile.equals(target.m_profile) : target.m_profile != null) return false;
        if (!m_schema.equals(target.m_schema)) return false;
        if (m_targetID != null ? !m_targetID.equals(target.m_targetID) : target.m_targetID != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_schema.hashCode();
        result = 29 * result + (m_capabilities != null ? m_capabilities.hashCode() : 0);
        result = 29 * result + (m_targetID != null ? m_targetID.hashCode() : 0);
        result = 29 * result + (m_profile != null ? m_profile.hashCode() : 0);
        return result;
    }
}
