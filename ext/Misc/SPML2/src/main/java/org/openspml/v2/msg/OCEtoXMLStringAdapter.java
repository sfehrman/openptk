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

/**
 * We  are unmarshalling XML to objects and cannot convert
 * an Element to objects, we'll assume it is openContent and
 * put it into an adapter the maintains it in String form.
 * That should allow the client to do whatever additional parsing
 * is necessary.
 */
public class OCEtoXMLStringAdapter implements OpenContentElementAdapter {

    private static final String code_id = "$Id: OCEtoXMLStringAdapter.java,v 1.6 2006/05/05 15:58:30 kas Exp $";

    private static final boolean COMMENT_STRINGS = true;

    private final String mXml;

    public OCEtoXMLStringAdapter(String xml) throws Spml2Exception {
        if (xml == null) {
            throw new Spml2Exception("Cannot adapt a null String as an OpenContentElement.");
        }
        mXml = xml;
    }

    public String getXMLString() {
        return mXml;
    }

    public Object getAdaptedObject() {
        return getXMLString();
    }

    public String toXML(int indent) throws Spml2Exception {
        // TODO: we could indent each line, but we're not going to for now.
        return toXML();
    }

    public String toXML() throws Spml2Exception {
        String result = mXml;
        if (COMMENT_STRINGS) {
            String  begin = "<!-- Begin Adapted OpenContent String -->\n";
            String  end   = "<!-- End Adapted OpenContent String -->\n";
            result = begin + mXml + end;
        }
        return result;
    }

    public OpenContentElement fromXML(Object xmlRep) throws Spml2Exception {
        if (xmlRep instanceof String) {
            return new OCEtoXMLStringAdapter((String) xmlRep);
        }
        throw new Spml2Exception("Expected a String as the xmlRep.");
    }
}
