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

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Many thanks to JavaWorld.
 * http://www.javaworld.com/javaworld/javatips/jw-javatip133.html
 * <p/>
 * All subclasses of this class:
 * <li>Should be immutable, e.g. only final private fields in the instances.</li>
 * <li>Don't need to override hashCode - object refs suffice.</li>
 * <li>Should be careful with static fields that are not of this type.</li>
 * <li>Should be final because fieldnames (public static final) could collide.</li>
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 1, 2006
 */
public class EnumConstant implements Serializable {

    private static final String code_id = "$Id: EnumConstant.java,v 1.1 2006/03/15 20:40:00 kas Exp $";

    // The representation of the constant on our serialization stream.
    private transient String _fieldName;

    /**
     * Write the instance's field name to the stream.
     */
    private void writeObject(ObjectOutputStream out)
            throws IOException {

        Class cls = getClass();
        Field[] f = cls.getDeclaredFields();

        try {
            for (int i = 0; i < f.length; i++) {
                int mod = f[i].getModifiers();
                if (Modifier.isStatic(mod) &&
                    Modifier.isFinal(mod) &&
                    Modifier.isPublic(mod)) {

                    // is this object the object in the field?
                    if (this == f[i].get(null)) {
                        String fName = f[i].getName();
                        out.writeObject(fName);
                    }
                }
            }
        }
        catch (IllegalAccessException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * read the serialized field name and assign to
     * _fieldName
     */
    private void readObject(ObjectInputStream in)
            throws IOException {
        try {

            _fieldName = (String) in.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Replace the deserialized instance with the
     * local static instance to allow correct
     * usage of == operator
     */
    public Object readResolve()
            throws ObjectStreamException {

        try {

            Class clazz = getClass();
            Field f = clazz.getField(_fieldName);
            return f.get(null);
        }
        catch (Exception ex) {
            throw new InvalidObjectException("Failed to resolve enum constant.");
        }

    }

    /**
     * Return the constants, in the order declared.
     * <p/>
     * Subclasses, e.g. FooEnum with a constant named FOO, should implement:
     * <pre>
     * public static FooEnum[] getConstants() {
     *   List temp = FOO.getEnumConstants();
     *   return (FooEnum[]) temp.toArray(new FooEnum[temp.size()]);
     * }
     * </pre>
     *
     * @return an array of constants, as defined in the class.
     */
    protected static List getEnumConstants(Class cls) {

        Field[] f = cls.getDeclaredFields();

        List temp = new ArrayList(f.length);

        for (int i = 0; i < f.length; i++) {
            int mod = f[i].getModifiers();
            if (Modifier.isStatic(mod) &&
                Modifier.isFinal(mod) &&
                Modifier.isPublic(mod)) {
                try {
                    temp.add(f[i].get(null));
                }
                catch (IllegalAccessException e) {
                    // this shouldn't happen, we checked the access.
                }
            }
        }
        EnumConstant[] e = (EnumConstant[]) temp.toArray(new EnumConstant[temp.size()]);
        return temp;
    }
}

