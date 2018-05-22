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
package org.openspml.v2.util;

import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.XMLUnmarshaller;
import org.openspml.v2.msg.spml.Request;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.provider.SPMLMarshaller;
import org.openspml.v2.util.xml.ReflectiveDOMXMLUnmarshaller;
import org.openspml.v2.util.xml.ReflectiveXMLMarshaller;

import java.util.Map;

/**
 * This is a class that we can use in the web.xml to marshall and
 * unmarshall requests and responses.  It's suitable for
 * use as a bootstrap.
 */
public class SimpleSPMLMarshaller implements SPMLMarshaller {

    private static final String code_id = "$Id: SimpleSPMLMarshaller.java,v 1.1 2006/03/15 23:56:51 kas Exp $";

    private static boolean _trace = false;

    public void init(Map map) throws Spml2Exception {

        Object trace = map.get("trace");
        if (trace != null) {
            Boolean b = new Boolean(trace.toString());
            _trace = b.booleanValue();
        }
    }

    public String getUniqueName() {
        return "SimpleSPMLMarshaller";
    }

    private XMLUnmarshaller _unmarshaller = new ReflectiveDOMXMLUnmarshaller();

    public Request unmarshallRequest(String s) throws Spml2Exception {
        try {
            if (_trace) {
                System.out.println("unmarshalling:\n " + s);
            }
            return (Request) _unmarshaller.unmarshall(s);
        }
        catch (ClassCastException cce) {
            throw new Spml2Exception(cce);
        }
    }

    private XMLMarshaller _marshaller = new ReflectiveXMLMarshaller();

    public String marshallResponse(Response response) throws Spml2Exception {
        String xml = response.toXML(_marshaller);
        if (_trace) {
            System.out.println("marshalled response to:\n" + xml);
        }
        return xml;
    }
}
