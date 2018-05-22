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
import org.openspml.v2.util.xml.XmlBuffer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a list of attributes...
 * <p/>
 * <pre>
 *  &lt;!-- This is from the Search portion - the profile says these are used to filter attributes --&gt;
 *  &lt;xsd:complexType name="AttributeDescriptions"&gt;
 *      &lt;xsd:sequence minOccurs="0" maxOccurs="unbounded"&gt;
 *          &lt;xsd:element name="attribute" type="AttributeDescription"/&gt;
 *      &lt;/xsd:sequence&gt;
 *  &lt;/xsd:complexType&gt;
 * </pre>
 */
public class AttributeDescriptions implements DSMLUnmarshaller.Parseable, OpenContentElement {

    private static final String code_id = "$Id: AttributeDescriptions.java,v 1.3 2006/06/21 22:41:37 kas Exp $";

    private List mAttributeDescriptions = new ArrayList();

    public AttributeDescriptions() { ; }

    public AttributeDescriptions(AttributeDescription[] descs) throws DSMLProfileException {
        if (descs != null)
            mAttributeDescriptions.addAll(Arrays.asList(descs));
    }

    public AttributeDescription[] getAttributeDescriptions() {
        return (AttributeDescription[]) mAttributeDescriptions.toArray(
                new AttributeDescription[mAttributeDescriptions.size()]);
    }

    public void addAttributeDescription(AttributeDescription desc) {
        if (desc != null)
            mAttributeDescriptions.add(desc);
    }

    public void addAttributeDescriptions(AttributeDescription[] descs) {
        for (int k = 0; k < descs.length; k++) {
            AttributeDescription desc = descs[k];
            addAttributeDescription(desc);
        }
    }

    public void setAttributeDescriptions(AttributeDescription[] descs) {
        clearAttributeDescriptions();
        addAttributeDescriptions(descs);
    }

    public void clearAttributeDescriptions() {
        mAttributeDescriptions.clear();
    }

    public String toXML(int indent) throws DSMLProfileException {
        try {
            XmlBuffer buffer = new XmlBuffer();
            buffer.setNamespace(new URI("urn:oasis:names:tc:DSML:2:0:core"));
            buffer.setPrefix("dsml");
            buffer.setIndent(indent);

            buffer.addStartTag("attributes");

            buffer.incIndent();
            for (int k = 0; k < mAttributeDescriptions.size(); k++) {
                AttributeDescription desc = (AttributeDescription) mAttributeDescriptions.get(k);
                desc.toXML(buffer);
            }
            buffer.decIndent();
            buffer.addEndTag("attributes");
            return buffer.toString();
        }
        catch (URISyntaxException e) {
            throw new DSMLProfileException(e);
        }
    }

    public String toXML() throws DSMLProfileException {
        return toXML(0);
    }

    public void parseXml(DSMLUnmarshaller um, Object xmlObj)
            throws DSMLProfileException {
        um.visitAttributeDescriptions(this, xmlObj);
    }

}
