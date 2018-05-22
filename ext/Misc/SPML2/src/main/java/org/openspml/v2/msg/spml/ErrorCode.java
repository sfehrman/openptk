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

//&lt;simpleType name="ErrorCode"&gt;
//    &lt;restriction base="string"&gt;
//        &lt;enumeration value="malformedRequest"/&gt;
//        &lt;enumeration value="unsupportedOperation"/&gt;
//        &lt;enumeration value="unsupportedIdentifierType"/&gt;
//        &lt;enumeration value="noSuchIdentifier"/&gt;
//        &lt;enumeration value="customError"/&gt;
//        &lt;enumeration value="unsupportedExecutionMode"/&gt;
//        &lt;enumeration value="invalidContainment"/&gt;
//        &lt;enumeration value="noSuchRequest"/&gt;
//        &lt;enumeration value="unsupportedSelectionType"/&gt;
//        &lt;enumeration value="resultSetToLarge"/&gt;
//        &lt;enumeration value="unsupportedProfile"/&gt;
//        &lt;enumeration value="invalidIdentifier"/&gt;
//        &lt;enumeration value="alreadyExists"/&gt;
//        &lt;enumeration value="containerNotEmpty"/&gt;
//    &lt;/restriction&gt;
//&lt;/simpleType&gt;

/**
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 1, 2006
 */
public final class ErrorCode extends BasicStringEnumConstant {

    private static final String code_id = "$Id: ErrorCode.java,v 1.1 2006/03/15 20:40:00 kas Exp $";

    public static final ErrorCode MALFORMED_REQUEST = new ErrorCode("malformedRequest");
    public static final ErrorCode UNSUPPORTED_OPERATION = new ErrorCode("unsupportedOperation");
    public static final ErrorCode UNSUPPORTED_IDENTIFIER_TYPE = new ErrorCode("unsupportedIdentifierType");
    public static final ErrorCode NO_SUCH_IDENTIFIER = new ErrorCode("noSuchIdentifier");
    public static final ErrorCode CUSTOM_ERROR = new ErrorCode("customError");
    public static final ErrorCode UNSUPPORTED_EXECUTION_MODE = new ErrorCode("unsupportedExecutionMode");
    public static final ErrorCode INVALID_CONTAINMENT = new ErrorCode("invalidContainment");
    public static final ErrorCode NO_SUCH_REQUEST = new ErrorCode("noSuchRequest");
    public static final ErrorCode UNSUPPORTED_SELECTION_TYPE = new ErrorCode("unsupportedSelectionType");
    public static final ErrorCode RESULT_SET_TOO_LARGE = new ErrorCode("resultSetToLarge");
    public static final ErrorCode UNSUPPORTED_PROFILE = new ErrorCode("unsupportedProfile");
    public static final ErrorCode INVALID_IDENTIFIER = new ErrorCode("invalidIdentifier");
    public static final ErrorCode ALREADY_EXISTS = new ErrorCode("alreadyExists");
    public static final ErrorCode CONTAINER_NOT_EMPTY = new ErrorCode("containerNotEmpty");

    public static ErrorCode[] getConstants() {
        List temp = EnumConstant.getEnumConstants(ErrorCode.class);
        return (ErrorCode[]) temp.toArray(new ErrorCode[temp.size()]);
    }

    private ErrorCode(String value) {
        super(value);
    }

}
