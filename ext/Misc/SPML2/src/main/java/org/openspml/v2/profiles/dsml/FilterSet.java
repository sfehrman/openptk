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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is an abstract class for those FilterItems that
 * contain 0,m FilterItems.
 *
 * <pre>
 * &lt;xsd:complexType name="FilterSet"&gt;
 *      &lt;xsd:sequence&gt;
 *          &lt;xsd:group ref="FilterGroup" minOccurs="0" maxOccurs="unbounded"/&gt;
 *      &lt;/xsd:sequence&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 */
public abstract class FilterSet extends FilterItem {

    private static final String code_id = "$Id: FilterSet.java,v 1.6 2006/07/20 01:22:14 kas Exp $";

    private List mItems = new ArrayList();

    protected void toXML(String s, XmlBuffer buffer) throws DSMLProfileException {
        buffer.addStartTag(s);
        buffer.incIndent();
        for (int k = 0; k < mItems.size(); k++) {
            FilterItem filterItem = (FilterItem) mItems.get(k);
            ////-- OpenPTK Add filter element around each filter
            buffer.addStartTag("filter");
            filterItem.toXML(buffer);
            buffer.addEndTag("filter");
            //-
        }
        buffer.decIndent();
        buffer.addEndTag(s);
    }

    public void parseXml(DSMLUnmarshaller um, Object e)
            throws DSMLProfileException {
        um.visitFilterSet(this, e);
    }

    public FilterSet(FilterItem[] items) {
        setItems(items);
    }

    public FilterSet(FilterItem item) {
        mItems.add(item);
    }

    public FilterSet() { ; }

    public FilterItem[] getItems() {
        return (FilterItem[]) mItems.toArray(new FilterItem[mItems.size()]);
    }

    public void setItems(FilterItem[] items) {
        mItems.clear();
        mItems.addAll(Arrays.asList(items));
    }

    public void addItems(FilterItem[] items) {
        mItems.addAll(Arrays.asList(items));
    }

    public void addItem(FilterItem item) {
        mItems.add(item);
    }

    public boolean removeItem(FilterItem item) {
        return mItems.remove(item);
    }

    public void clearItems() {
        mItems.clear();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterSet)) return false;

        final FilterSet filterSet = (FilterSet) o;

        if (mItems != null ? !mItems.equals(filterSet.mItems) : filterSet.mItems != null) return false;

        return true;
    }

    public int hashCode() {
        return (mItems != null ? mItems.hashCode() : 0);
    }
}
