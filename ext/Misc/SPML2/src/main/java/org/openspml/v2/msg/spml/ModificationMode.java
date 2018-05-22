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

/**
 * <br>&lt;simpleType name="ModificationModeType"&gt;
 * <br>&lt;restriction base="string"&gt;
 * <br>&lt;enumeration value="add"/&gt;
 * <br>&lt;enumeration value="replace"/&gt;
 * <br>&lt;enumeration value="delete"/&gt;
 * <br>&lt;/restriction&gt;
 * <br>&lt;/simpleType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 7, 2006
 */
public class ModificationMode extends BasicStringEnumConstant {

    private static final String code_id = "$Id: ModificationMode.java,v 1.2 2006/05/18 00:21:44 kas Exp $";

    public static final ModificationMode ADD = new ModificationMode("add");
    public static final ModificationMode REPLACE = new ModificationMode("replace");
    public static final ModificationMode DELETE = new ModificationMode("delete");

    public static ModificationMode[] getConstants() {
        List temp = EnumConstant.getEnumConstants(ModificationMode.class);
        return (ModificationMode[]) temp.toArray(new ModificationMode[temp.size()]);
    }

    public static ModificationMode getConstant(String name) {
        return (ModificationMode) BasicStringEnumConstant.getConstant(ModificationMode.class, name);
    }

    private ModificationMode(String value) {
        super(value);
    }
}
