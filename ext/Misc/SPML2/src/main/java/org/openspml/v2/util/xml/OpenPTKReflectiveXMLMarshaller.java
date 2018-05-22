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

import org.openspml.v2.util.xml.ReflectionUtilities;
import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.MarshallableElement;
import org.openspml.v2.msg.OpenContentAttr;
import org.openspml.v2.msg.OpenContentContainer;
import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.spml.Selection;
import org.openspml.v2.util.Spml2Exception;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class works in concert with the implementations of
 * in the msg.subpackages to marshall the values there into
 * XML.  We use a protocol for field names that make this possible.
 * This was more expedient than hand coding the marshalling,
 * but may not be as performant as one would like.
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 9, 2006
 */
public class OpenPTKReflectiveXMLMarshaller implements XMLMarshaller {

    private static final String code_id = "$Id: ReflectiveXMLMarshaller.java,v 1.12 2006/10/04 01:06:12 kas Exp $";

    // this will collect the XML
    private XmlBuffer mBuffer = null;
    private int mIndent = 0;
    ////-- openptk modification for add to control setting of data element
    private boolean addDataElement = false;

    protected String marshall(String elementName, Marshallable obj)
            throws Spml2Exception {
        return marshall(elementName, obj, mIndent);
    }

    // take the Marshallable object and redispatch it.
    // (top level objects)
    protected String marshall(String elementName, Marshallable m, int indent)
            throws Spml2Exception {

        try {

            if (elementName == null) {
                elementName = m.getElementName();
            }
            ////-- openptk modification for add to control setting of data element
            if (elementName.equalsIgnoreCase("addRequest"))
            {
               addDataElement = true;
            }
            // fresh buffer
            mBuffer = new XmlBuffer();

            if (indent != 0)
                mBuffer.setIndent(indent);

            // add the ns tuples to the top of the declaration
            PrefixAndNamespaceTuple[] tuples = m.getNamespacesInfo();
            if (tuples != null) {
                for (int k = 0; k < tuples.length; k++) {
                    PrefixAndNamespaceTuple tuple = tuples[k];
                    URI ns = new URI(tuple.namespaceURI);
                    if (tuple.isDefault) {
                        mBuffer.setNamespace(ns);
                    }
                    else {
                        mBuffer.addNamespace(tuple.prefix, ns);
                    }
                }
            }

            redispatch(m, elementName);
            return mBuffer.toString();
        }
        catch (URISyntaxException e) {
            throw new Spml2Exception(e);
        }
    }

    // This is for internal (not Marshallable) objects.
    //
    // Redispatch to a method that adds the object to the buffer,
    // wrapped in the elname element
    private void redispatch(MarshallableElement obj, String elname) throws Spml2Exception {

        // redispatch to the method that handles the class of m.
        try {
            Method method = getClass().getDeclaredMethod("marshall",
                                                         new Class[]{
                                                             obj.getClass(),
                                                             String.class
                                                         });
            method.invoke(this, new Object[]{obj, elname});
        }
        catch (NoSuchMethodException e) {
            default_marshall(obj, elname);
        }
        catch (IllegalAccessException e) {
            throw new Spml2Exception(e);
        }
        catch (InvocationTargetException e) {
            System.out.println("elname is " + elname);
            System.out.println("partial buffer:\n" + mBuffer);
            e.getCause().printStackTrace(System.err);
            throw new Spml2Exception(e);
        }
    }

    // walk the attributes and convert them to XML
    //
    private Field[] processAttributeFields(MarshallableElement me, String elName) throws Spml2Exception {

        String prefix = getPreferredPrefix(me);
        ////-- openptk modification for add to control setting of data element
        if (!(elName.equalsIgnoreCase("data")))
        {
            mBuffer.addOpenStartTag(prefix, elName);
        }
        else  if (addDataElement)
        {
            mBuffer.addOpenStartTag(prefix, elName);
        }
        //-

        if (me instanceof OpenContentContainer) {
            OpenContentAttr[] attrs = ((OpenContentContainer) me).getOpenContentAttrs();
            addOpenContentAttrs(attrs);
        }

        List attributes = new ArrayList();
        List nonAttributeFields = new ArrayList();
        ReflectionUtilities.getAttributeAndElementFields(me, attributes, nonAttributeFields);

        for (int k = 0; k < attributes.size(); k++) {
            Field field = (Field) attributes.get(k);
            String attrPrefix = null;
            addAttributeForField(me, attrPrefix, field);
        }

        return (Field[]) nonAttributeFields.toArray(new Field[nonAttributeFields.size()]);
    }

    private void addOpenContentAttrs(OpenContentAttr[] attrs) {
        for (int k = 0; k < attrs.length; k++) {
            OpenContentAttr attr = attrs[k];
            if (attr != null) {
                String name = attr.getName();
                String value = attr.getValue();
                if (name != null && value != null) {
                    mBuffer.addAttribute(name, value);
                }
            }
        }
    }

    private void addAttributeForField(MarshallableElement mar, String prefix, Field field) {
        try {
            String attrName = ReflectionUtilities.getAttributeNameFromField(field);
            field.setAccessible(true);
            Object val = field.get(mar);
            if (val != null) {
                mBuffer.addAttribute(attrName, prefix, val.toString());
            }
        }
        catch (IllegalAccessException e) {
            // this won't happen, we set the field as accessible
        }
    }

    // walk the elements, and do them.
    private void processElementFields(MarshallableElement me, Field[] fields, String name)
            throws Spml2Exception {

        boolean tagClosed = false;

        if (me instanceof OpenContentContainer) {
            OpenContentElement[] els = ((OpenContentContainer) me).getOpenContentElements();
            if (els != null && els.length != 0) {
                // we're going to add elements so close the start tag.
                ////-- OpenPTK Added for controling data element on Modify
                if (!(name.equalsIgnoreCase("data")))
                {
                     mBuffer.closeStartTag();
                }
                else  if (addDataElement)
                {
                  mBuffer.closeStartTag();

                }
                //-
                tagClosed = true;
                addOpenContentElements(els);
            }
        }

        // for each elemental field that is MarshallableElement,
        // redispatch back to this marshaller.  Otherwise write the element.
        try {
            for (int k = 0; k < fields.length; k++) {
                Field field = fields[k];
                Class fType = field.getType();
                String elname = ReflectionUtilities.getElementNameFromField(field);
                field.setAccessible(true);
                if (MarshallableElement.class.isAssignableFrom(fType)) {
                    MarshallableElement fVal = (MarshallableElement) field.get(me);
                    if (fVal != null) {
                        if (!tagClosed) {
                            mBuffer.closeStartTag();
                            tagClosed = true;
                        }
                        mBuffer.incIndent();
                        String prefix = getMarshallableElementPrefix(me);
                        if (fVal instanceof Marshallable) prefix = "";
                        this.redispatch(fVal, prefix + elname);
                        mBuffer.decIndent();
                    }
                }
                else if (ListWithType.class.isAssignableFrom(fType)) {
                    // we want it iterate over the collection and redispatch
                    ListWithType c = (ListWithType) field.get(me);
                    if (!c.isEmpty()) {
                        mBuffer.incIndent();
                        Iterator iter = c.iterator();
                        while (iter.hasNext()) {

                            if (!tagClosed) {
                                mBuffer.closeStartTag();
                                tagClosed = true;
                            }

                            String prefix = getMarshallableElementPrefix(me);
                            Object val = iter.next();
                            if (val instanceof Marshallable) {
                                Marshallable marVal = (Marshallable) val;
                                elname = marVal.getElementName();
                                prefix = "";
                            }
                            this.redispatch((MarshallableElement) val, prefix + elname);
                        }
                        mBuffer.decIndent();
                    }
                }
                else if (List.class.isAssignableFrom(fType)) {
                    // This is used for String objects only...
                    List list = (List) field.get(me);
                    for (int v = 0; v < list.size(); v++) {
                        if (!tagClosed) {
                            mBuffer.closeStartTag();
                            tagClosed = true;
                        }
                        Object val = list.get(v);
                        mBuffer.incIndent();
                        mBuffer.addElement(elname, val.toString());
                        mBuffer.decIndent();
                    }
                }
                else if (fType.isArray() && String.class.equals(fType.getComponentType())) {
                    // we want it iterate over the collection and redispatch
                    String[] array = (String[]) field.get(me);
                    String prefix = getPreferredPrefix(me);
                    for (int j = 0; array != null && j < array.length; j++) {
                        if (!tagClosed) {
                            mBuffer.closeStartTag();
                            tagClosed = true;
                        }
                        mBuffer.incIndent();
                        mBuffer.addElement(prefix, elname, array[j]);
                        mBuffer.decIndent();
                    }
                }
                else {
                    System.out.println("field not handled: " + field.getName() + ":" + field.getType());
                }
            }
        }
        catch (IllegalAccessException e) {
            // shouldn't happen
            throw new Spml2Exception(e);
        }

        if (!tagClosed) {
            mBuffer.closeEmptyElement();
        }
        else {
            String prefix = getPreferredPrefix(me);
            ////-- OpenPTK added to control setting of data element
            if (!(name.equalsIgnoreCase("data")))
            {
               mBuffer.addEndTag(prefix, name);
            }
            else if (addDataElement)
            {
               mBuffer.addEndTag(prefix, name);
               //addDataElement = false;
            }
            //-
        }
    }

    private String getPreferredPrefix(MarshallableElement me) throws Spml2Exception {
        return ReflectionUtilities.getPreferredPrefix(me);
    }

    private String getMarshallableElementPrefix(MarshallableElement me) throws Spml2Exception {
        String prefix = getPreferredPrefix(me);
        if (prefix != null) {
            prefix += ":";
        }
        else {
            prefix = "";
        }
        return prefix;
    }

    private boolean addOpenContentElements(OpenContentElement[] els)
            throws Spml2Exception {

        if (els.length > 0) {
            mBuffer.incIndent();
            for (int k = 0; k < els.length; k++) {
                int indent = mBuffer.getIndent();
                mBuffer.addAnyElement(els[k].toXML(indent), true);
            }
            mBuffer.decIndent();
            return true;
        }
        return false;
    }

    protected void default_marshall(MarshallableElement e, String elName)
            throws Spml2Exception {

        Field[] els = processAttributeFields(e, elName);
        processElementFields(e, els, elName);
    }

    // we add this as an example of the reflective dispatch.  This is not
    // strictly necessary - but if you need fine-grained control of marshalling
    // you can add methods.
    protected String marshall(Selection s, String element) throws Spml2Exception {
        default_marshall(s, "select");
        return mBuffer.toString();
    }

    ///////////////////////////////
    //  Public methods
    //////////////////////////////

    public OpenPTKReflectiveXMLMarshaller() {
    }

    public OpenPTKReflectiveXMLMarshaller(int indent) throws Spml2Exception {
        setIndent(indent);
    }

    public void setIndent(int indent) throws Spml2Exception {
        if (indent >= 0)
            mIndent = indent;
    }

    // So we don't let just anyone in here, we want top-level
    // objects to implement the Marshallable interface.
    public String marshall(Marshallable m) throws Spml2Exception {
        return marshall(null, m);
    }

}
