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
package org.openspml.v2.transport;

import java.util.Map;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 20, 2006
 */
public interface RPCDispatcher {

    /**
     * Called during router initialization.
     * The map contains init parameters.  For example,
     * this might be a Map made from a ServletConfig.
     */
    public void init(Map config) throws Spml2TransportException;

    /**
     * Process a request.
     * <p/>
     * If the dispatcher does not recognize the message, it must return null.
     * If the handler recognizes the message, it must return a properly
     * formatted string of data containing the entire response; including
     * error responses.
     * <p/>
     * The dispatcher is allowed to throw any exception, it will be caught
     * by the router and converted into a generic http error response; that
     * will include the stacktrace, or a reference to where it can be found.
     */
    public String dispatchRequest(String message)  throws Spml2TransportException;

    /**
     * If you handle a request of a given type, you also need to
     * provide us with contentType string to return.
     *
     * @return String representing the type.
     */
    public String getContentType();
}

