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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a starter class that denotes a type to restrict
 * setters on this package.
 * <p/>
 * Each of the classes representing the typed elements of this this choice,
 * e.g. "and" - which is a descendant of "FilterSet", will implement this interface.
 * <pre>
 * &lt;xsd:group name="FilterGroup"&gt;
 *     &lt;xsd:sequence&gt;
 *         &lt;xsd:choice&gt;
 *             &lt;xsd:element name="and" type="FilterSet"/&gt;
 *             &lt;xsd:element name="or" type="FilterSet"/&gt;
 *             &lt;xsd:element name="not" type="Filter"/&gt;
 *             &lt;xsd:element name="equalityMatch" type="AttributeValueAssertion"/&gt;
 *             &lt;xsd:element name="substrings" type="SubstringFilter"/&gt;
 *             &lt;xsd:element name="greaterOrEqual" type="AttributeValueAssertion"/&gt;
 *             &lt;xsd:element name="lessOrEqual" type="AttributeValueAssertion"/&gt;
 *             &lt;xsd:element name="present" type="AttributeDescription"/&gt;
 *             &lt;xsd:element name="approxMatch" type="AttributeValueAssertion"/&gt;
 *             &lt;xsd:element name="extensibleMatch" type="MatchingRuleAssertion"/&gt;
 *         &lt;/xsd:choice&gt;
 *     &lt;/xsd:sequence&gt;
 * &lt;/xsd:group&gt;
 * <p/>
 * </pre>
 */

abstract public class FilterItem implements DSMLUnmarshaller.Parseable {

    private static final String code_id = "$Id: FilterItem.java,v 1.7 2006/08/30 18:02:59 kas Exp $";

    protected FilterItem() { }

    abstract protected void toXML(XmlBuffer buffer) throws DSMLProfileException;

    abstract public void parseXml(DSMLUnmarshaller um, Object e)
            throws DSMLProfileException;

    /**
     * We want to just deal with Maps of String to List of Strings.
     * You can override this to deal with other types if you'd like,
     * but the matches() methods are implemented expecting List
     * as the value in the Map.
     *
     * @param attrs
     * @return A Map of attrName->List of valueStrings (often 1)
     */
    protected Map convertAttrsToMap(DSMLAttr[] attrs) {

        Map result = new HashMap();
        if (attrs == null) return result;

        for (int k = 0; k < attrs.length; k++) {
            DSMLAttr attr = attrs[k];
            DSMLValue[] values = attr.getValues();
            if (values != null && values.length > 0) {
                List valuesList = new ArrayList();
                for (int j = 0; j < values.length; j++) {
                    valuesList.add(values[j].getValue());
                }
                result.put(attr.getName(), valuesList);
            }
        }

        return result;
    }

    final public boolean matches(DSMLAttr[] attrs)
            throws DSMLProfileException {
        Map attrsMap = convertAttrsToMap(attrs);
        return matches(attrsMap);
    }

    public boolean matches(Map attrs) throws DSMLProfileException {
        return false;
    }

    abstract public void accept(FilterItemVisitor visitor) throws DSMLProfileException;
}
