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

import org.openspml.v2.util.xml.XmlBuffer;

import java.util.Map;

/**
 * Represents the &lt;not&gt; element in a filter.
 *
 * NOTE: Since the profile does not allow for Not at the top-level (just
 * attributes and filter elements are allowed) we are implementing Not with
 * that in mind.  This makes this unsuitable for use in a general DSML 2.0
 * implementation, but that's okay.
 *
 *  <pre> &lt;xsd:element name="not" type="Filter"/&gt; </pre>
 */
public class Not extends FilterItem {

    private static final String code_id = "$Id: Not.java,v 1.7 2006/06/29 22:31:46 kas Exp $";

    private BasicFilter mFilterDelegate = new BasicFilter();

    protected Not() {
        super();
    }

    protected void toXML(XmlBuffer buffer) throws DSMLProfileException {
        String xml = mFilterDelegate.toXML("not", buffer.getIndent(), false);
        buffer.addAnyElement(xml, true);
    }

    public Not(FilterItem item) {
        mFilterDelegate.setItem(item);
    }

    public void setItem(FilterItem item) {
        mFilterDelegate.setItem(item);
    }

    public FilterItem getItem() {
        return mFilterDelegate.getItem();
    }

    public void parseXml(DSMLUnmarshaller um, Object e)
            throws DSMLProfileException {
        um.visitNot(this, e);
    }

    public boolean matches(Map attrs) throws DSMLProfileException {
        return !mFilterDelegate.getItem().matches(attrs);
    }

    public void accept(FilterItemVisitor visitor) throws DSMLProfileException {
        visitor.visitNot(this);
    }
}
