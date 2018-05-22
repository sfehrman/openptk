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
package org.openspml.v2.msg.spml;

import org.openspml.v2.util.BasicStringEnumConstant;
import org.openspml.v2.util.EnumConstant;

import java.util.List;

//&lt;simpleType name="StatusCodeType"&gt;
//    &lt;restriction base="string"&gt;
//        &lt;enumeration value="success"/&gt;
//        &lt;enumeration value="failure"/&gt;
//        &lt;enumeration value="pending"/&gt;
//    &lt;/restriction&gt;
//&lt;/simpleType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 1, 2006
 */
public final class StatusCode extends BasicStringEnumConstant {

    private static final String code_id = "$Id: StatusCode.java,v 1.1 2006/03/15 20:40:00 kas Exp $";

    public static final StatusCode SUCCESS = new StatusCode("success");
    public static final StatusCode FAILURE = new StatusCode("failure");
    public static final StatusCode PENDING = new StatusCode("pending");

    public static StatusCode[] getConstants() {
        List temp = EnumConstant.getEnumConstants(StatusCode.class);
        return (StatusCode[]) temp.toArray(new StatusCode[temp.size()]);
    }

    private StatusCode(String value) {
        super(value);
    }
}
