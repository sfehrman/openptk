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

import org.openspml.v2.msg.spml.ModificationMode;
import org.openspml.v2.util.xml.XmlBuffer;

/**
 * From the DSML spec...
 *
 * <pre>
 * &lt;xsd:complexType name="Modification"&gt;
 * &lt;xsd:sequence&gt;
 *     &lt;xsd:element name="value" type="DsmlValue" minOccurs="0" maxOccurs="unbounded"/&gt;
 * &lt;/xsd:sequence&gt;
 * &lt;xsd:attribute name="name" type="AttributeDescriptionValue" use="required"/&gt;
 * &lt;xsd:attribute name="operation" use="required"&gt;
 *     &lt;xsd:simpleType&gt;
 *         &lt;xsd:restriction base="xsd:string"&gt;
 *             &lt;xsd:enumeration value="add"/&gt;
 *             &lt;xsd:enumeration value="delete"/&gt;
 *             &lt;xsd:enumeration value="replace"/&gt;
 *         &lt;/xsd:restriction&gt;
 *     &lt;/xsd:simpleType&gt;
 * &lt;/xsd:attribute&gt;
 * &lt;/xsd:complexType&gt;
 * </pre>
 *
 * @author Blaine Busler and Kent Spaulding
 */
public class DSMLModification extends DSMLAttr {

    protected DSMLModification() { ; }

    // <xsd:simpleType>
    // <xsd:restriction base="xsd:string">
    // <xsd:enumeration value="add"/>
    // <xsd:enumeration value="delete"/>
    // <xsd:enumeration value="replace"/>
    // </xsd:restriction>
    // </xsd:simpleType>

    private ModificationMode mOperation = null;

    public DSMLModification(String name, DSMLValue[] values, ModificationMode operation)
            throws DSMLProfileException {
        super(name, values);
        mOperation = operation;
    }

    public DSMLModification(String name, String value, ModificationMode operation)
            throws DSMLProfileException {
        super(name, value);
        mOperation = operation;
    }

    public ModificationMode getOperation() {
        return mOperation;
    }

    public void setOperation(ModificationMode operation) {
        mOperation = operation;
    }

    public void parseXml(DSMLUnmarshaller um, Object xmlObj)
            throws DSMLProfileException {
        um.visitDSMLModification(this, xmlObj);
    }

    protected void addSubclassAttributes(XmlBuffer buffer) throws DSMLProfileException {
        super.addSubclassAttributes(buffer);
        if (mOperation != null) {
            buffer.addAttribute("operation", mOperation.toString());
        }
    }

    public String toXML(int indent) throws DSMLProfileException {
        return super.toXML("modification", indent);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DSMLModification)) return false;
        if (!super.equals(o)) return false;

        final DSMLModification modification = (DSMLModification) o;

        if (mOperation != null ? !mOperation.equals(modification.mOperation) : modification.mOperation != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + (mOperation != null ? mOperation.hashCode() : 0);
        return result;
    }
}
