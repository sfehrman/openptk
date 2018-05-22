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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * There's only one SubstringFilter typed item, the Substring element, so
 * we're using a single class.
 * <p/>
 * <pre>
 * &lt;xsd:complexType name="SubstringFilter"&gt;
 *     &lt;xsd:sequence&gt;
 *         &lt;xsd:element name="initial" type="DsmlValue" minOccurs="0"/&gt;
 *         &lt;xsd:element name="any" type="DsmlValue" minOccurs="0" maxOccurs="unbounded"/&gt;
 *         &lt;xsd:element name="final" type="DsmlValue" minOccurs="0"/&gt;
 *     &lt;/xsd:sequence&gt;
 *     &lt;xsd:attribute name="name" type="AttributeDescriptionValue" use="required"/&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 */
public class Substrings extends NamedFilterItem {

    private static final String code_id = "$Id: Substrings.java,v 1.11 2006/08/30 18:02:59 kas Exp $";

    // xsd:element name="initial" type="DsmlValue" minOccurs="0"
    private DSMLValue mInitial = null;

    // xsd:element name="any" type="DsmlValue" minOccurs="0" maxOccurs="unbounded"
    private List mAny = new ArrayList();

    // xsd:element name="final" type="DsmlValue" minOccurs="0"
    private DSMLValue mFinal = null;

    public Substrings() {
    }

    public Substrings(String name) throws DSMLProfileException {
        super(name);
    }

    public Substrings(String name,
                      DSMLValue initial,
                      DSMLValue[] any,
                      DSMLValue aFinal) throws DSMLProfileException {
        super(name);
        mInitial = initial;
        mAny = Arrays.asList(any);
        mFinal = aFinal;
    }

    protected void toXML(XmlBuffer buffer) throws DSMLProfileException {
        super.toXML("substrings", buffer);
    }

    protected void addSubclassElements(XmlBuffer buffer) throws DSMLProfileException {
        if (mInitial != null) {
            mInitial.toXML("initial", buffer);
        }

        for (int k = 0; k < mAny.size(); k++) {
            DSMLValue value = (DSMLValue) mAny.get(k);
            value.toXML("any", buffer);
        }

        if (mFinal != null) {
            mFinal.toXML("final", buffer);
        }
    }

    public void parseXml(DSMLUnmarshaller um, Object e) throws DSMLProfileException {
        um.visitSubstrings(this, e);
    }

    public DSMLValue getInitial() {
        return mInitial;
    }

    public void setInitial(DSMLValue initial) {
        mInitial = initial;
    }

    public DSMLValue[] getAny() {
        return (DSMLValue[]) mAny.toArray(new DSMLValue[mAny.size()]);
    }

    public String[] getAnyStrings() {
        List strings = new ArrayList();
        for (Iterator i = mAny.iterator(); i.hasNext();) {
            DSMLValue dsmlValue = (DSMLValue) i.next();
            strings.add(dsmlValue.toString());
        }
        return (String[]) strings.toArray(new String[strings.size()]);
    }

    public void setAny(DSMLValue[] any) {
        if (any != null) {
            mAny.clear();
            mAny.addAll(Arrays.asList(any));
        }
    }

    public void addAny(DSMLValue any) {
        if (any != null) mAny.add(any);
    }

    public boolean removeAny(DSMLValue any) {
        if (any != null) return mAny.remove(any);
        return false;
    }

    public void clearAny() {
        mAny.clear();
    }

    public DSMLValue getFinal() {
        return mFinal;
    }

    public void setFinal(DSMLValue aFinal) {
        mFinal = aFinal;
    }

    /**
     * If more the one of initial, any, or final is set,
     * this does a logical or.
     * <p>
     * If you don't want that behavior, nest substrings
     * filters with one item in the logical operator
     * that you want.
     * <p>
     * @param attrs
     * @return true any of the fields match any of the values
     *  of the attribute of the same name as this filter from the
     *  attrs map.
     * @throws DSMLProfileException
     */
    public boolean matches(Map attrs) throws DSMLProfileException {

        if (attrs == null)
            return false;
        
        List valuesToTest = (List) attrs.get(getName());
        if (valuesToTest == null || valuesToTest.size() == 0) return false;

        String finalString = mFinal == null ? null : mFinal.toString();
        String initialString = mInitial == null ? null : mInitial.toString();
        String[] anyStrings = mAny.isEmpty() ? null : getAnyStrings();

        boolean matches = false;
        for (int k = 0; !matches && k < valuesToTest.size(); k++) {
            String dsmlValue = (String) valuesToTest.get(k);
            String valueString = dsmlValue.toString();
            if (mInitial != null) {
                if (valueString.startsWith(initialString)) matches = true;
            }
            else if (finalString != null) {
                if (valueString.endsWith(finalString)) matches = true;
            }
            else if (anyStrings != null) {
                for (int j = 0; j < anyStrings.length; j++) {
                    String anyString = anyStrings[j];
                    if (valueString.indexOf(anyString) >= 0) {
                        matches = true;
                        break;
                    }
                }
            }
        }
        return matches;
    }

    public void accept(FilterItemVisitor visitor) throws DSMLProfileException {
        visitor.visitSubstrings(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Substrings)) return false;
        if (!super.equals(o)) return false;

        final Substrings substrings = (Substrings) o;

        if (mAny != null ? !mAny.equals(substrings.mAny) : substrings.mAny != null) return false;
        if (mFinal != null ? !mFinal.equals(substrings.mFinal) : substrings.mFinal != null) return false;
        if (mInitial != null ? !mInitial.equals(substrings.mInitial) : substrings.mInitial != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (mInitial != null ? mInitial.hashCode() : 0);
        result = 29 * result + (mAny != null ? mAny.hashCode() : 0);
        result = 29 * result + (mFinal != null ? mFinal.hashCode() : 0);
        return result;
    }
}
