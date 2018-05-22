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
package org.openspml.v2.util.xml;

import org.openspml.v2.msg.MarshallableElement;
import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.util.Spml2Exception;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is a class that knows how to interrogate the 
 * objects under the msg package to get things like
 * all the attribute Fields, and all the element Fields
 * for an object.
 * 
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Mar 2, 2006
 */
class ReflectionUtilities {

    private static final String code_id = "$Id: ReflectionUtilities.java,v 1.4 2006/08/30 18:02:59 kas Exp $";

    static void getAttributeAndElementFields(MarshallableElement e,
                                             List attributes,
                                             List elements)
    {
        Class cls = e.getClass();
        List allClasses = new ArrayList();
        while (cls != null && MarshallableElement.class.isAssignableFrom(cls)) {
            allClasses.add(cls);
            cls = cls.getSuperclass();
        };
        // order matters
        Collections.reverse(allClasses);

        for (int k = 0; k < allClasses.size(); k++) {
            Class aClass = (Class) allClasses.get(k);
            Field[] fields = aClass.getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                String fieldName = field.getName();
                if (!Modifier.isStatic(field.getModifiers()) &&
                    !Modifier.isTransient(field.getModifiers()) &&
                    fieldName.startsWith("m_")) {
                    Class fieldType = field.getType();
                    if (!Extensible.class.isAssignableFrom(fieldType) &&
                        !Collection.class.isAssignableFrom(fieldType) &&
                        !fieldType.isArray()) {
                        attributes.add(field);
                    }
                    else {
                        elements.add(field);
                    }
                }
            }
        }
    }

    // for now, these are the same.
    public static String getAttributeNameFromField(Field f) {
        return getElementNameFromField(f);
    }

    public static String getElementNameFromField(Field field) {
        return field.getName().substring(2);
    }

    public static String getPreferredPrefix(MarshallableElement me) throws Spml2Exception {

        // If there is only one, it's from the default.
        PrefixAndNamespaceTuple[] tuples = me.getNamespacesInfo();

        // if the length is one, and the first one is the default;
        // return null.
        if (tuples.length == 1 && tuples[0].isDefault)
            return null;

        // otherwise, return the first one that is not the default.
        for (int k = 0; k < tuples.length; k++) {
            PrefixAndNamespaceTuple tuple = tuples[k];
            if (tuple.isDefault) {
                continue;
            }
            return tuple.prefix;
        }

        // we really should get here.
        throw new Spml2Exception(
                "The prefix and namespace declarations for the the MarshallableElement class " + me.getClass() + " are incorrect.");
    }

}
