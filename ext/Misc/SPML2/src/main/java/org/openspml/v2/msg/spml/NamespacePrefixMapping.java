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


/**
 * <br>&lt;complexType name="NamespacePrefixMappingType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;attribute name="prefix" type="string" use="required"/&gt;
 * <br>&lt;attribute name="namespace" type="string" use="required"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class NamespacePrefixMapping extends Extensible {

    private static final String code_id = "$Id: NamespacePrefixMapping.java,v 1.3 2006/04/25 21:22:09 kas Exp $";

    // <attribute name="prefix" type="string" use="required"/>
    private String m_prefix;

    // <attribute name="namespace" type="string" use="required"/>
    private String m_namespace;

    public NamespacePrefixMapping() { ; }

    public NamespacePrefixMapping(String prefix,
                                  String namespace) {
        assert (prefix != null && namespace != null);
        m_prefix = prefix;
        m_namespace = namespace;
    }

    public String getPrefix() {
        return m_prefix;
    }

    public void setPrefix(String prefix) {
        assert (prefix != null);
        m_prefix = prefix;
    }

    public String getNamespace() {
        return m_namespace;
    }

    public void setNamespace(String namespace) {
        assert (namespace != null);
        m_namespace = namespace;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespacePrefixMapping)) return false;
        if (!super.equals(o)) return false;

        final NamespacePrefixMapping namespacePrefixMappingType = (NamespacePrefixMapping) o;

        if (!m_namespace.equals(namespacePrefixMappingType.m_namespace)) return false;
        if (!m_prefix.equals(namespacePrefixMappingType.m_prefix)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_prefix.hashCode();
        result = 29 * result + m_namespace.hashCode();
        return result;
    }
}
