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
package org.openspml.v2.util.xml;

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.OCEtoMarshallableAdapter;
import org.openspml.v2.msg.OCEtoXMLStringAdapter;
import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.XMLUnmarshaller;
import org.openspml.v2.util.Spml2Exception;

/**
 * Starter class.
 *
 * @author Kent Spaulding
 */
abstract public class BasicOCEAndMarshallable extends BasicMarshallable implements OpenContentElement {

    protected final XMLMarshaller mMarshaller = new ReflectiveXMLMarshaller();
    protected final XMLUnmarshaller mUnmarshaller = new ReflectiveDOMXMLUnmarshaller();

    protected BasicOCEAndMarshallable() {
    }

    // OpenContentElement implementation methods

    public String toXML(int indent) throws Spml2Exception {
        mMarshaller.setIndent(indent);
        String xml = mMarshaller.marshall(this);
        return xml;
    }

    public String toXML() throws Spml2Exception {
        return toXML(0);
    }

    /**
     * We know how to handle Strings that are passed in.
     * Other types will return a null.
     * <p/>
     * Override this if you want to support more types for xmlRep.
     *
     * @param xmlRep
     * @return  an object, in this case, an adapter.
     * @throws org.openspml.v2.util.Spml2Exception
     */
    public OpenContentElement fromXML(Object xmlRep) throws Spml2Exception {
        if (xmlRep instanceof String) {
            Marshallable m = null;
            try {
                m = mUnmarshaller.unmarshall(xmlRep.toString());
                if (m != null) {
                    return new OCEtoMarshallableAdapter(m);
                }
            }
            catch (UnknownSpml2TypeException e) {
                return new OCEtoXMLStringAdapter(xmlRep.toString());
            }
        }
        return null;
    }
}
