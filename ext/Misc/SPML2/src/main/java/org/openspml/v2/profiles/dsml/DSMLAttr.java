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
/*
 * Copyright 2006 Tripod Technology Group, Inc.
 * All Rights Reserved.
 * Use is subject to license terms.
 */
package org.openspml.v2.profiles.dsml;

import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.util.xml.XmlBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * From the DSML spec...
 * <p/>
 * <pre>
 * &lt;complexType name="Attr"&gt;
 * &lt;sequence&gt;
 * &lt;element name="value" type="Value" minOccurs="0" maxOccurs="unbounded"/&gt;
 * &lt;/sequence&gt;
 * &lt;attribute name="name" type="AttributeDescriptionValue" use="required"/&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 * @author Blaine Busler and Kent Spaulding
 */
public class DSMLAttr extends NamedItem implements DSMLUnmarshaller.Parseable, OpenContentElement {

    /**
     * The profile specifies that an attr with the name is reserved and
     * provides the name of the objectclass for a given set of attributes.
     */
    public static final String RESERVED_TYPE_ATTR_NAME = "objectclass";

    // <element name="value" type="DsmlValue" minOccurs="0" maxOccurs="unbounded"/>
    private List mValues = new ArrayList();

    // This is just to make the DSMLUnmarshaller a little cleaner
    protected DSMLAttr() { ; }

    public DSMLAttr(String name, String value) throws DSMLProfileException {
        setName(name);
        if (value != null)
            mValues.add(new DSMLValue(value));
    }

    public DSMLAttr(String name, DSMLValue[] values) throws DSMLProfileException {
        setName(name);
        if (values != null)
            mValues.addAll(Arrays.asList(values));
    }

    public DSMLValue[] getValues() {
        return (DSMLValue[]) mValues.toArray(new DSMLValue[mValues.size()]);
    }

    public void addValue(DSMLValue value) {
        if (value != null)
            mValues.add(value);
    }

    public void addValues(DSMLValue[] values) {
        for (int k = 0; k < values.length; k++) {
            DSMLValue value = values[k];
            addValue(value);
        }
    }

    public void setValues(DSMLValue[] values) {
        clearValues();
        addValues(values);
    }

    public void clearValues() {
        mValues.clear();
    }

    public int numValues() {
        return mValues.size();
    }

    protected void addSubclassElements(XmlBuffer buffer)
            throws DSMLProfileException {

        super.addSubclassElements(buffer);
        DSMLValue[] mVals = getValues();
        for (int k = 0; k < mVals.length; k++)
            mVals[k].toXML(buffer);
    }

    public String toXML(int indent) throws DSMLProfileException {
        return super.toXML("attr", indent);
    }

    public String toXML() throws DSMLProfileException {
        return toXML(0);
    }

    public void parseXml(DSMLUnmarshaller m, Object xmlObj)
            throws DSMLProfileException {
        m.visitDSMLAttr(this, xmlObj);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DSMLAttr)) return false;
        if (!super.equals(o)) return false;

        final DSMLAttr attr = (DSMLAttr) o;

        if (mValues != null ? !mValues.equals(attr.mValues) : attr.mValues != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (mValues != null ? mValues.hashCode() : 0);
        return result;
    }
}
