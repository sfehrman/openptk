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
package org.openspml.v2.msg;

/**
 * SPML 2.0 is an open content model.  The content carried by the various
 * objects in our toolkit, can be XML of any type, and implementors can
 * use whatever extra attributes and elements that they like.
 * <p/>
 * This class, and the OpenContentElement, are meant to represent the notion
 * of open content.
 * <p/>
 * This is a class used to represent XMLAttributes (name, type, and value)
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 1, 2006
 */
public class BasicOpenContentAttr implements OpenContentAttr {

    private static final String code_id = "$Id: BasicOpenContentAttr.java,v 1.3 2006/05/23 18:00:57 kas Exp $";

    private final String mName;
    private final String mValue;

    /**
     * We want to ensure that the value is 'simple' - that
     * is, representable as a String.  It could be a boolean,
     * int, etc; but not a complexType (so not any POJO).
     *
     * @param name
     * @param value
     */
    public BasicOpenContentAttr(String name, String value) {
        assert(name != null);
        assert(value != null);
        this.mName = name;
        this.mValue = value;
    }

    public String getName() {
        return mName;
    }

    public String getValue() {
        return mValue;
    }

    // consider adding helper methods like this.
    public boolean getBooleanValue() {
        if (mValue == null) return false;
        return Boolean.valueOf(mValue).booleanValue();
    }

    // do we want the value in the .equals?
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicOpenContentAttr)) return false;

        final BasicOpenContentAttr basicOpenContentAttr = (BasicOpenContentAttr) o;

        if (mName != null ? !mName.equals(basicOpenContentAttr.mName) : basicOpenContentAttr.mName != null) return false;
        if (mValue != null ? !mValue.equals(basicOpenContentAttr.mValue) : basicOpenContentAttr.mValue != null) return false;

        return true;
    }

    // include the value?
    public int hashCode() {
        int result;
        result = (mName != null ? mName.hashCode() : 0);
        result = 29 * result + (mValue != null ? mValue.hashCode() : 0);
        return result;
    }
}