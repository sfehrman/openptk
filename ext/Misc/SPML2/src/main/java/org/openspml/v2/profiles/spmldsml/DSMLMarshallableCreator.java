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
package org.openspml.v2.profiles.spmldsml;

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.xml.ObjectFactory;
import org.openspml.v2.profiles.DSMLProfileRegistrar;

public class DSMLMarshallableCreator implements ObjectFactory.MarshallableCreator {

    private static final String code_id = "$Id: DSMLMarshallableCreator.java,v 1.5 2006/10/04 01:06:12 kas Exp $";

    private static final String DSML_PREFIX = "spmldsml";

    static PrefixAndNamespaceTuple[] staticGetNamespacesInfo() {
        return new PrefixAndNamespaceTuple[]{
            new PrefixAndNamespaceTuple(DSML_PREFIX, DSMLProfileRegistrar.PROFILE_URI_STRING),
        };
    }

    public Marshallable createMarshallable(String nameAndPrefix, String uri) throws Spml2Exception {

        if (DSMLProfileRegistrar.PROFILE_URI_STRING.equals(uri)) {
            int idx = nameAndPrefix.indexOf("schema");
            if (idx >= 0) {
                return new DSMLSchema();
            }
        }
        return null;
    }
}
