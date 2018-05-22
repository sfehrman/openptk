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
 * This is the only item that implements the MatchingRuleAssertion so
 * we're doing this all here.
 * <p/>
 * <pre>
 *    &lt;xsd:complexType name="MatchingRuleAssertion"&gt;
 *        &lt;xsd:sequence&gt;
 *            &lt;xsd:element name="value" type="DsmlValue"/&gt;
 *        &lt;/xsd:sequence&gt;
 *        &lt;xsd:attribute name="dnAttributes" type="xsd:boolean" use="optional" default="false"/&gt;
 *        &lt;xsd:attribute name="matchingRule" type="xsd:string" use="optional"/&gt;
 *        &lt;xsd:attribute name="name" type="AttributeDescriptionValue" use="optional"/&gt;
 *    &lt;/xsd:complexType&gt;
 * </pre>
 */
public class ExtensibleMatch extends NamedFilterItem {

    private static final String code_id = "$Id: ExtensibleMatch.java,v 1.8 2006/06/29 22:31:46 kas Exp $";

    /**
     * <pre> &lt;xsd:element name="value" type="DsmlValue"/&gt;  </pre>
     */
    private DSMLValue mValue = null;

    /**
     * <pre> &lt;xsd:attribute name="dnAttributes" type="xsd:boolean" use="optional" default="false"/&gt;  </pre>
     */
    private Boolean mDnAttributes = null;

    /**
     * <pre> &lt;xsd:attribute name="matchingRule" type="xsd:string" use="optional"/&gt; </pre>
     */
    private String mMatchingRule = null;

    protected ExtensibleMatch(String name, DSMLValue value, String matchingRule, Boolean dnAttributes)
            throws DSMLProfileException {
        super(name);
        this.mValue = value;
        this.mDnAttributes = dnAttributes;
        this.mMatchingRule = matchingRule;
    }

    protected void toXML(XmlBuffer buffer) throws DSMLProfileException {
        super.toXML("extensibleMatch", buffer);
    }

    protected void addSubclassAttributes(XmlBuffer buffer) throws DSMLProfileException {
        super.addSubclassAttributes(buffer);

        if (mDnAttributes != null) {
            buffer.addAttribute("dnAttributes", mDnAttributes);
        }
        else {
            buffer.addAttribute("dnAttributes", "false");
        }

        if (mMatchingRule != null) {
            buffer.addAttribute("matchingRule", mMatchingRule);
        }
    }

    protected void addSubclassElements(XmlBuffer buffer) throws DSMLProfileException {
        super.addSubclassElements(buffer);
        if (mValue != null) {
            mValue.toXML(buffer);
        }
    }

    protected ExtensibleMatch() {
    }

    public void parseXml(DSMLUnmarshaller um, Object e) throws DSMLProfileException {
        um.visitExtensibleMatch(this, e);
    }

    public ExtensibleMatch(String name, DSMLValue value) throws DSMLProfileException {
        this(name, value, null, null);
    }

    public ExtensibleMatch(String name, String value) throws DSMLProfileException {
        this(name, new DSMLValue(value), null, null);
    }

    public ExtensibleMatch(String name, String value, String matchingRule)
            throws DSMLProfileException {
        this(name, new DSMLValue(value), matchingRule, null);
    }

    public ExtensibleMatch(String name, String value, String matchingRule, boolean dnAttributes)
            throws DSMLProfileException {
        this(name, new DSMLValue(value), matchingRule, new Boolean(dnAttributes));
    }

    public DSMLValue getValue() {
        return mValue;
    }

    public void setValue(DSMLValue value) throws DSMLProfileException {
        if (value != null) {
            mValue = value;
        }
        else {
            throw new DSMLProfileException("Value cannot be null; it's a required element.");
        }
    }

    public boolean getDnAttributes() {
        return mDnAttributes != null ? mDnAttributes.booleanValue() : false;
    }

    public void setDnAttributes(boolean dnAttributes) {
        // we use null to indicate that this should not be put in the XML.
        this.mDnAttributes = new Boolean(dnAttributes);
    }

    public String getMatchingRule() {
        return mMatchingRule;
    }

    public void setMatchingRule(String matchingRule) {
        mMatchingRule = matchingRule;
    }

     /**
      * Servers will likely provide their own implementation of this -
      * See the DSMLProfileRegistar and DSMLUnmarshaller for details on
      * what to extend and override.
      * <p>
      * @param attrs
      * @return   true if it matches.
      * @throws DSMLProfileException
      */
    public boolean matches(Map attrs) throws DSMLProfileException {
        throw new DSMLProfileException("Not implemented.");
    }

    public void accept(FilterItemVisitor visitor) throws DSMLProfileException {
        visitor.visitExtensibleMatch(this);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtensibleMatch)) return false;
        if (!super.equals(o)) return false;

        final ExtensibleMatch extensibleMatch = (ExtensibleMatch) o;

        if (mDnAttributes != null ? !mDnAttributes.equals(extensibleMatch.mDnAttributes) : extensibleMatch.mDnAttributes != null) return false;
        if (mMatchingRule != null ? !mMatchingRule.equals(extensibleMatch.mMatchingRule) : extensibleMatch.mMatchingRule != null) return false;
        if (mValue != null ? !mValue.equals(extensibleMatch.mValue) : extensibleMatch.mValue != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (mValue != null ? mValue.hashCode() : 0);
        result = 29 * result + (mDnAttributes != null ? mDnAttributes.hashCode() : 0);
        result = 29 * result + (mMatchingRule != null ? mMatchingRule.hashCode() : 0);
        return result;
    }
}
