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

//&lt;simpleType name="ExecutionModeType"&gt;
//    &lt;restriction base="string"&gt;
//        &lt;enumeration value="synchronous"/&gt;
//        &lt;enumeration value="asynchronous"/&gt;
//    &lt;/restriction&gt;
//&lt;/simpleType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 1, 2006
 */
public final class ExecutionMode extends BasicStringEnumConstant {

    private static final String code_id = "$Id: ExecutionMode.java,v 1.1 2006/03/15 20:40:00 kas Exp $";

    public static final ExecutionMode SYNCHRONOUS = new ExecutionMode("synchronous");
    public static final ExecutionMode ASYNCHRONOUS = new ExecutionMode("asynchronous");

    public static ExecutionMode[] getConstants() {
        List temp = EnumConstant.getEnumConstants(ExecutionMode.class);
        return (ExecutionMode[]) temp.toArray(new ExecutionMode[temp.size()]);
    }

    private ExecutionMode(String value) {
        super(value);
    }

}
