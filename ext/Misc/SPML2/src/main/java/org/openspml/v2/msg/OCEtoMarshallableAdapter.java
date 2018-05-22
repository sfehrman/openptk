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
package org.openspml.v2.msg;

import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ReflectiveDOMXMLUnmarshaller;
import org.openspml.v2.util.xml.ReflectiveXMLMarshaller;

/**
 * This class is placed around "open content" when we encounter XML
 * during unmarshalling that we don't recognize.
 */
public class OCEtoMarshallableAdapter implements OpenContentElementAdapter {

    private static final String code_id = "$Id: OCEtoMarshallableAdapter.java,v 1.4 2006/05/03 23:28:30 kas Exp $";

    private XMLUnmarshaller mUnmarshaller = new ReflectiveDOMXMLUnmarshaller();
    private XMLMarshaller mMarshaller = new ReflectiveXMLMarshaller();

    private final Marshallable mAdaptedMarshallable;

    public OCEtoMarshallableAdapter(Marshallable m) throws Spml2Exception {
        if (m == null) {
            throw new Spml2Exception("Cannot adapt a null as a Marshallable.");
        }
        mAdaptedMarshallable = m;
    }

    public Marshallable getMarshallable() {
        return mAdaptedMarshallable;
    }

    public Object getAdaptedObject() {
        return getMarshallable();
    }

    public String toXML() throws Spml2Exception {
        return toXML(0);
    }

    public String toXML(int indent) throws Spml2Exception {
        mMarshaller.setIndent(indent);
        return mAdaptedMarshallable.toXML(mMarshaller);
    }

    public OpenContentElement fromXML(Object xmlRep) throws Spml2Exception {
        if (xmlRep instanceof String) {
            return new OCEtoMarshallableAdapter(mUnmarshaller.unmarshall((String) xmlRep));
        }
        throw new Spml2Exception("Expected a String as the xmlRep.");
    }
}
