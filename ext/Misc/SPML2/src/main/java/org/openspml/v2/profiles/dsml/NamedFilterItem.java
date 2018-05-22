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

/**
 * This is base class to capture the various objects that have names of Attributes.
 */
abstract class NamedFilterItem extends FilterItem  {

    private static final String code_id = "$Id: NamedFilterItem.java,v 1.3 2006/06/21 22:41:37 kas Exp $";

    /**
     * <pre> &lt;xsd:attribute name="name" type="AttributeDescriptionValue" use="optional"/&gt; </pre>
     */
    private AttributeDescriptionValue mName = new AttributeDescriptionValue();

    protected NamedFilterItem() { ; }

    protected NamedFilterItem(String name) throws DSMLProfileException {
        setName(name);
    }

    public void parseXml(DSMLUnmarshaller um, Object e) throws DSMLProfileException {
        um.visitNamedFilterItem(this, e);
    }

    public String getName() {
        return mName.toString();
    }

    public void setName(String name) throws DSMLProfileException {
       mName.setName(name);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedFilterItem)) return false;

        final NamedFilterItem namedFilterItem = (NamedFilterItem) o;

        if (mName != null ? !mName.equals(namedFilterItem.mName) : namedFilterItem.mName != null) return false;

        return true;
    }

    public int hashCode() {
        return (mName != null ? mName.hashCode() : 0);
    }

    protected void addSubclassAttributes(XmlBuffer buffer)
            throws DSMLProfileException {
        ;
    }

    protected void addSubclassElements(XmlBuffer buffer)
            throws DSMLProfileException {
         ;
    }

    protected void toXML(String s, XmlBuffer buffer)
            throws DSMLProfileException {

        buffer.addOpenStartTag(s);
        buffer.addAttribute("name", mName);
        addSubclassAttributes(buffer);
        buffer.closeStartTag();

        buffer.incIndent();
        addSubclassElements(buffer);
        buffer.decIndent();
        buffer.addEndTag(s);

    }
}
