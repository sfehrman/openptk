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

/**
 * Just making it clear which namespace/version of spec we are binding to.
 * 
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Jan 26, 2006
 */
public class Spml2Exception extends Exception {

    private static final String code_id = "$Id: Spml2Exception.java,v 1.3 2006/08/29 23:42:03 kas Exp $";

    private static final String LEGAL_NOTICE =
    "Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,\n" +
    "California 95054, U.S.A. All rights reserved.\n" +
    "\n" +
    "U.S. Government Rights - Commercial software. Government users are subject\n" +
    "to the Sun Microsystems, Inc. standard license agreement and applicable\n" +
    "provisions of the FAR and its supplements.\n" +
    "\n" +
    "Use is subject to license terms.\n" +
    "\n" +
    "This distribution may include materials developed by third parties.\n" +
    "\n" +
    "Sun, Sun Microsystems, the Sun logo and Java are trademarks or registered\n" +
    "trademarks of Sun Microsystems, Inc. in the U.S. and other countries.\n";

    public Spml2Exception() {
        super();
    }

    public Spml2Exception(String message) {
        super(message);
    }

    public Spml2Exception(Throwable cause) {
        super(cause);
    }

    public Spml2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
