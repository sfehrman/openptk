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

// The Original Copyright and license notice... note that
// Sun acquired Waveset in late 2003.

package org.openspml.v2.util.xml;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A utility class used to format XML strings.
 * <p/>
 * It wraps a StringBuffer and provides methods with awareness of
 * XML syntax.
 */
public class XmlBuffer {

    private static final String code_id = "$Id: XmlBuffer.java,v 1.2 2006/10/18 16:57:36 kas Exp $";

    public static final String XML_HEADER =
            "<?xml version='1.0' encoding='UTF-8'?>";
    /**
     * Inner string buffer.
     */
    private StringBuffer _buf;

    /**
     * The current indentation level.
     */
    private int _indent;

    /**
     * The namespace prefix for elements.
     */
    private String _prefix;

    /**
     * The namespace URI.
     * When this and _prefix are set, the first time an element is
     * added to the buffer, a namespace declaration is added.
     */
    private URI _namespace;

    /**
     * Set after the namespace declaration has been added.
     */
    private boolean _namespaceDeclared;

    /**
     * List of namespaces to include.
     */
    private Map _namespaces;

    public XmlBuffer() {
        _buf = new StringBuffer();
    }

    public XmlBuffer(int length) {
        _buf = new StringBuffer(length);
    }

    public int getIndent() {
        return _indent;
    }

    public void setIndent(int indent) {
        this._indent = indent;
    }

    public void setPrefix(String s) {
        _prefix = s;
    }

    public void setNamespace(URI uri) {
        _namespace = uri;
    }

    public void addNamespace(String name, URI uri) {
        if (name != null && uri != null) {
            if (_namespaces == null)
                _namespaces = new HashMap();
            _namespaces.put(name, uri);
        }
    }

    //////////////////////////////////////////////////////////////////////
    //
    // StringBuffer pass-through methods
    //
    //////////////////////////////////////////////////////////////////////

    public void append(String s) {
        _buf.append(s);
    }

    public void append(Integer i) {
        _buf.append(i);
    }

    public void append(char c) {
        _buf.append(c);
    }

    public void append(int i) {
        _buf.append(i);
    }

    public char charAt(int index) {
        return _buf.charAt(index);
    }

    public String toString() {
        return _buf.toString();
    }

    public int length() {
        return _buf.length();
    }

    public void setLength(int len) {
        _buf.setLength(len);
        _namespaceDeclared = false;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // XML aware methods
    //
    //////////////////////////////////////////////////////////////////////

    /**
     * Increment the indentation level.
     */
    public void incIndent(int i) {
        _indent += i;
    }

    public void incIndent() {
        _indent += 2;
    }

    /**
     * Decrement the indentation level.
     */
    public void decIndent(int i) {
        _indent -= i;
        if (_indent < 0)
            _indent = 0;
    }

    public void decIndent() {
        _indent -= 2;
        if (_indent < 0)
            _indent = 0;
    }

    /**
     * Adds an attribute name and value to the buffer.
     * <p/>
     * Performs any necessary escaping on the value.  This should
     * be used when you're building the XML for something, and
     * its possible for an attribute value to have any of the characters
     * &, ', or "
     */
    public void addAttribute(String name, String prefix, String value) {

       ////-- OpenPTK: allow attribute value to be an empty string
        if (value != null) {
            append(" ");

            //// assume that the prefix doesn't need to be escaped
            if (prefix != null) {
                append(prefix);
                append(":");
            }

            append(name);
            append("=");

            int doubles = 0;
            int singles = 0;
            char delim = '\'';

            // look for quotes
            int max = value.length();
            for (int i = 0; i < max; i++) {
                char ch = value.charAt(i);
                if (ch == '"')
                    doubles++;
                else if (ch == '\'')
                    singles++;
            }

            // if single quotes were detected, switch to double delimiters
            if (singles > 0)
                delim = '"';

            append(delim);

            //// The prefix goes before the name?
            //if (prefix != null) {
            //    append(prefix);
            //    append(":");
            //}

            // add the value, converting embedded quotes if necessary
            for (int i = 0; i < max; i++) {
                char ch = value.charAt(i);
                if (ch == '&')
                    append("&amp;");
                else if (ch == '<')
                    append("&lt;");
                else if (ch < ' ') {
                    // binary, ignore though could escape
                }
                else if (ch != delim)
                    append(ch);
                else if (delim == '\'')
                    append("&#39;");// convert '
                else
                    append("&#34;");// convert "
            }

            append(delim);
        }
    }

    public void addAttribute(String name, String value) {
        addAttribute(name, null, value);
    }

    /**
     * Add an an attribute value using an arbitrary object,
     * by calling its toString method.
     */
    public void addAttribute(String name, Object obj) {
        if (obj != null)
            addAttribute(name, obj.toString());
    }

    /**
     * Adds a boolean attribute to the buffer.
     * <p/>
     * If the value is false, the attribute is suppressed.
     */
    public void addAttribute(String name, boolean value) {
        if (value) {
            append(" ");
            append(name);
            append("='true'");
        }
    }

    /**
     * Adds an integer attribute to the buffer.
     */
    public void addAttribute(String name, int value) {
        _buf.append(" ");
        _buf.append(name);
        _buf.append("='");
        _buf.append(value);
        _buf.append("'");
    }

    /**
     * Adds a string of element content to the buffer.
     * <p/>
     * Replaces special characters in a string with XML character entities.
     * The characters replaced are '&' and '<'.
     * This should be when building strings intended to be the
     * values of XML attributes or XML element content.
     */
    public void addContent(String s) {
        if (s!=null) {
            int max = s.length();
            for (int i = 0; i < max; i++) {
                char ch = s.charAt(i);
                if (ch == '&')
                    _buf.append("&amp;");
                else if (ch == '<')
                    _buf.append("&lt;");
                else
                    _buf.append(ch);
            }
        }
    }

    /**
     * Add indentation to the buffer.
     */
    public void addIndent(int indent) {
        if (indent > 0) {
            for (int i = 0; i < indent; i++)
                _buf.append(" ");
        }
    }

    /**
     * Adds an open element start tag.
     */
    public void addOpenStartTag(String name) {

        addOpenStartTag(_prefix, name);
    }

    public void addOpenStartTag(String prefix, String name) {

        addIndent(_indent);
        append("<");
        if (prefix != null && name.indexOf(":") < 0) {
            append(prefix);
            append(":");
        }
        append(name);
        checkNamespace();
    }

    public void addOpenStartTagNS(String urn, String name) {

        addIndent(_indent);
        append("<");
        append(name);
        append(" xmlns='");
        append(urn);
        append("'");
    }

    private void checkNamespace() {

        if (!_namespaceDeclared) {

            if (_namespace != null) {
                append(" xmlns");
                if (_prefix != null) {
                    append(":");
                    append(_prefix);
                }
                append("='");
                append(_namespace.toString());
                append("'");
            }

            if (_namespaces != null) {
                Iterator it = _namespaces.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    append(" xmlns:");
                    append(entry.getKey().toString());
                    append("='");
                    append(entry.getValue().toString());
                    append("'");
                }
            }

            _namespaceDeclared = true;
        }
    }

    /**
     * Close an open start tag.
     */
    public void closeStartTag() {
        closeStartTag(true);
    }

    /**
     * Close an element with control over trailing newline.
     */
    public void closeStartTag(boolean newline) {
        append(">");
        if (newline)
            append("\n");
    }

    /**
     * Close an empty open start tag.
     */
    public void closeEmptyElement() {
        append("/>\n");
    }

    /**
     * Adds a closed element start tag followed by a newline.
     */
    public void addStartTag(String name) {
        addStartTag(_prefix, name, true);
    }

    public void addStartTag(String prefix, String name) {
        addStartTag(prefix, name, true);
    }

    /**
     * Kludge for .NET, add a tag with an unqualified
     * name, but a namespace declaration
     */
    public void addStartTagNS(String urn, String name, boolean newline) {
        addIndent(_indent);
        append("<");
        append(name);
        append(" xmlns='");
        append(urn);
        append("'>");
        if (newline)
            append("\n");
    }

    /**
     * Adds a closed element start tag with control over the
     * trailing newline.
     */
    public void addStartTag(String name, boolean newline) {

        addStartTag(_prefix, name, newline);
    }

    public void addStartTag(String prefix, String name, boolean newline) {

        addIndent(_indent);
        append("<");
        if (prefix != null && name.indexOf(":") < 0) {
            append(prefix);
            append(":");
        }
        append(name);
        checkNamespace();
        append(">");
        if (newline)
            append("\n");
    }

    /**
     * Adds an element end tag.
     */
    public void addEndTag(String name) {
        addEndTag(_prefix, name);
    }

    public void addEndTag(String prefix, String name) {
        addEndTag(prefix, name, true);
    }

    /**
     * Adds an element end tag, with control over indentation.
     */
    public void addEndTag(String name, boolean indent) {
        addEndTag(_prefix, name, indent);
    }

    public void addEndTag(String prefix, String name, boolean indent) {
        if (indent)
            addIndent(_indent);
        append("</");
        if (prefix != null && name.indexOf(":") < 0) {
            append(prefix);
            append(":");
        }
        append(name);
        append(">\n");
    }

    /**
     * Adds an element with content to the buffer, being careful
     * to escape content.
     */
    public void addElement(String element, String content) {

        addElement(_prefix, element, content);
    }

    public void addElement(String prefix, String element, String content) {

        if (content != null) {
            addStartTag(prefix, element, false);
            addContent(content);
            addEndTag(prefix, element, false);
        }
    }

    public void addAnyElement(String xml) {
        addAnyElement(xml, false);
    }

    public void addAnyElement(String xml, boolean noIndent) {
        if (!noIndent)
            addIndent(_indent);
        append(xml);
    }

}

