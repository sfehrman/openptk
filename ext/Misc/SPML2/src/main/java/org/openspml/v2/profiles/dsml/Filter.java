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
package org.openspml.v2.profiles.dsml;

import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.util.Spml2Exception;

import java.util.Map;
/**
 * This is from the DSML 2.0 Specification.
 *
 * <pre>
 * &lt;xsd:complexType name="Filter"&gt;
 *   &lt;xsd:group ref="FilterGroup"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 */
public class Filter extends BasicFilter implements OpenContentElement {

    private static final String code_id = "$Id: Filter.java,v 1.7 2006/06/29 22:31:46 kas Exp $";

    public Filter() { ; }

    public Filter(FilterItem item) {
        super(item);
    }

    public String toXML(int indent) throws DSMLProfileException {
        return super.toXML("filter", indent, true);
    }

    public String toXML() throws DSMLProfileException {
        return toXML(0);
    }

    final public boolean matches(DSMLAttr[] attrs) throws DSMLProfileException {
        return getItem().matches(attrs);
    }

    final public boolean matches(Map map) throws DSMLProfileException {
        return getItem().matches(map);
    }

    public void applyVisitor(FilterItemVisitor v) throws DSMLProfileException {
        getItem().accept(v);
    }

    /**
     * This method was introduced by the QueryClause and doesn't do
     * the right thing here as we use a different XML protocol - so
     * we map it into our system - ignoring the XMLMarhsaller.
     *
     * @param m
     * @return an XML representation of the object.
     * @throws Spml2Exception
     */
    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return toXML();
    }
}
