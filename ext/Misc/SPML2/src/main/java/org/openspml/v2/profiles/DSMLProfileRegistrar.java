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
package org.openspml.v2.profiles;

import org.openspml.v2.profiles.dsml.DSMLUnmarshaller;
import org.openspml.v2.profiles.spmldsml.DSMLMarshallableCreator;
import org.openspml.v2.util.xml.ObjectFactory;
import org.openspml.v2.util.Spml2Exception;

import java.net.URI;
import java.net.URISyntaxException;

public class DSMLProfileRegistrar implements ObjectFactory.ProfileRegistrar {

    private static final String code_id = "$Id: DSMLProfileRegistrar.java,v 1.6 2006/10/04 01:06:12 kas Exp $";

    private ObjectFactory.OCEUnmarshaller mDsml = null;
    private ObjectFactory.MarshallableCreator mSpmlDsml = null;

    /**
     * If you extended DSMLUnmarshaller and want to use that
     * implementation, extend this class too and the new subclass
     * as your Registrar.
     * <p>
     * @return a new ObjectFactory.OCEUnmarshaller
     */
    protected ObjectFactory.OCEUnmarshaller createDSMLUnmarshaller() {
        return new DSMLUnmarshaller();
    }

    /**
     * If you implemented your own creator for DSML and want to
     * use that implementation; extend this class and override this method.
     * Use the new subclass as the Registrar.
     * <p>
     * @return a new DSMLUnmarshaller
     */
    protected ObjectFactory.MarshallableCreator createDSMLMarshallableCreator() {
        return new DSMLMarshallableCreator();
    }

    public DSMLProfileRegistrar() {
        mDsml = createDSMLUnmarshaller();
        mSpmlDsml = createDSMLMarshallableCreator();
    }

    public static final String PROFILE_URI_STRING = "urn:oasis:names:tc:SPML:2:0:DSML";
    public static final String PROFILE_ID = "SPMLv2 DSMLv2 Profile";

    public String getProfileId() {
        return PROFILE_ID;
    }

    public URI getProfileURI() throws Spml2Exception {
        try {
            return new URI(PROFILE_URI_STRING);
        }
        catch (URISyntaxException e) {
            throw new Spml2Exception(e);
        }
    }

    public void register(ObjectFactory of) {
        of.addOCEUnmarshaller(mDsml);
        of.addCreator(mSpmlDsml);
    }

    public void unregister(ObjectFactory of) {
        of.removeOCEUnmarshaller(mDsml);
        of.removeCreator(mSpmlDsml);
    }
}
