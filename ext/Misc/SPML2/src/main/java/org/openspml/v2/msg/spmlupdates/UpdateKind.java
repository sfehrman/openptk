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
package org.openspml.v2.msg.spmlupdates;

import org.openspml.v2.util.BasicStringEnumConstant;
import org.openspml.v2.util.EnumConstant;

import java.util.List;

/**
 * <br>&lt;simpleType name="UpdateKindType"&gt;
 * <br>&lt;restriction base="string"&gt;
 * <br>&lt;enumeration value="add"/&gt;
 * <br>&lt;enumeration value="modify"/&gt;
 * <br>&lt;enumeration value="delete"/&gt;
 * <br>&lt;enumeration value="capability"/&gt;
 * <br>&lt;/restriction&gt;
 * <br>&lt;/simpleType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class UpdateKind extends BasicStringEnumConstant {

    private static final String code_id = "$Id: UpdateKind.java,v 1.1 2006/03/15 20:40:00 kas Exp $";

    private UpdateKind(String val) {
        super(val);
    }

    //* <enumeration value="add"/>
    public static final UpdateKind ADD = new UpdateKind("add");
    //* <enumeration value="modify"/>
    public static final UpdateKind MODIFY = new UpdateKind("modify");
    //* <enumeration value="delete"/>
    public static final UpdateKind DELETE = new UpdateKind("delete");
    //* <enumeration value="capability"/>
    public static final UpdateKind CAPABILITY = new UpdateKind("capability");

    public static UpdateKind[] getConstants() {
        List temp = EnumConstant.getEnumConstants(UpdateKind.class);
        return (UpdateKind[]) temp.toArray(new UpdateKind[temp.size()]);
    }
}
