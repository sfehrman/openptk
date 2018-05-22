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

import org.openspml.v2.msg.spml.QueryClause;
import org.openspml.v2.util.xml.XmlBuffer;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * We have one Filter that is a FilterItem, one that is not.  They
 * start inheriting/delegating from here...
 * <p/>
 * <pre>
 * &lt;!-- **** DSML Filter **** --&gt;
 * &lt;xsd:complexType name="Filter"&gt;
 *     &lt;xsd:group ref="FilterGroup"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 */
class BasicFilter extends QueryClause implements DSMLUnmarshaller.Parseable {

    private static final String code_id = "$Id: BasicFilter.java,v 1.8 2006/08/30 18:02:59 kas Exp $";

    protected FilterItem mItem = null;

    protected BasicFilter() { ; }

    protected BasicFilter(FilterItem item) {
        mItem = item;
    }

    // I'm betting we'll have a toXML in here... that
    // takes a subclass name like "filter" and "not"
    protected String toXML(String s, int indent, boolean addNSInfo)
            throws DSMLProfileException {


        XmlBuffer buffer = new XmlBuffer();

        if (addNSInfo) {
            try {
                buffer.setNamespace(new URI("urn:oasis:names:tc:DSML:2:0:core"));
            }
            catch (URISyntaxException e) {
                throw new DSMLProfileException(e.getMessage(), e);
            }
        }

        buffer.setPrefix("dsml");
        buffer.setIndent(indent);

         ////- OpenPTK prevent filter element from being in the outer wrapper
         if (!(s.equalsIgnoreCase("filter"))) {
            buffer.addStartTag(s);
         }
         //-
        buffer.incIndent();

        mItem.toXML(buffer);

        buffer.decIndent();
        
         ////- OpenPTK prevent filter element from being in the outer wrapper
         if (!(s.equalsIgnoreCase("filter"))) {
            buffer.addEndTag(s);
         }
        //-
        return buffer.toString();
    }

    public FilterItem getItem() {
        return mItem;
    }

    public void setItem(FilterItem item) {
        mItem = item;
    }

    public void parseXml(DSMLUnmarshaller um, Object e) throws DSMLProfileException {
        um.visitBasicFilter(this, e);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicFilter)) return false;

        final BasicFilter basicFilter = (BasicFilter) o;

        if (mItem != null ? !mItem.equals(basicFilter.mItem) : basicFilter.mItem != null) return false;

        return true;
    }

    public int hashCode() {
        return (mItem != null ? mItem.hashCode() : 0);
    }
}
