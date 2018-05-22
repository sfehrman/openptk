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
// The Original Copyright notice... note that
// Sun acquired Waveset in late 2003.

// Copyright (C) 2003 Waveset Technologies, Inc..
// 6034 West Courtyard Drive, Suite 210, Austin, Texas 78730
// All rights reserved.
// 
package org.openspml.v2.util.xml;

import org.openspml.v2.util.FileUtil;
import org.openspml.v2.util.Spml2Exception;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * A wrapper around the Xerces parser that provides a simpler interface,
 * and maintains a pool of parser objects for better performance.
 * <p/>
 * This implements the Xerces ErrorHandler interface so we can intercept
 * parser errors and mutate them.
 * <p/>
 * This is a slimmed down version of the XmlParser from the 1.0 toolkit.
 * We do NOT do any validating.
 */
public class XmlParser implements ErrorHandler, EntityResolver {

    public static final String code_id = "$Id: XmlParser.java,v 1.5 2006/10/18 16:57:36 kas Exp $";

    /**
     * The global pool of non-validating parsers.
     */
    private static XmlParser _pool;

    /**
     * The global pool of validating parsers.
     */
    private static XmlParser _validatingPool;

    /**
     * Flag/File controlling whether we save xml strings that resulted
     * in parsing errors to a log file.
     */
    private static String _logFailuresFile = null;

    /**
     * Maximum number of times to allow a parser to be used before
     * removing it from the pool.
     * Avoids occasional Xerces memory leaks, at least with older
     * versions of the parser.
     */
    private static final int _maxUsages = 10000;

    /**
     * The underlying DOM parser.
     */
    private DocumentBuilderFactory _factory = null;
    private DocumentBuilder _parser;

    /**
     * The pool chain.
     */
    private XmlParser _next;

    /**
     * Flag indicating whether or not we're in the pool.
     * This allows us to detect attempts to pool the same object
     * twice without having to search the pool.
     */
    private boolean _inPool;

    /**
     * Keep a count of the number of times this has been used.
     */
    private int _usages;

    private static synchronized XmlParser getParser(boolean validating) {

        XmlParser p = null;

        if (!validating) {
            if (_pool != null) {
                p = _pool;
                _pool = p.getNext();
            }
        }
        else {
            if (_validatingPool != null) {
                p = _validatingPool;
                _validatingPool = p.getNext();
            }
        }

        if (p == null) {
            try {
                p = new XmlParser(validating);
            }
            catch (ParserConfigurationException e) {
            }
            catch (FactoryConfigurationError e) {
            }
        }

        if (p != null) {
            p.setPooled(false);
            p.incUsages();
        }

        return p;
    }

    /**
     * Return an object to the pool.
     */
    private static synchronized void poolParser(XmlParser p) {

        // guard against attempts to pool this more than once
        if (!p.isPooled()) {

            // if this parser has been handled too much,
            // don't return it to the pool, sometimes they go bad

            if (_maxUsages <= 0 || p.getUsages() < _maxUsages) {
                if (p._parser.isValidating()) {
                    p.setNext(_validatingPool);
                    _validatingPool = p;
                }
                else {
                    p.setNext(_pool);
                    _pool = p;
                }
                p.setPooled(true);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    //////////////////////////////////////////////////////////////////////

    /**
     * Construct a private utility object.
     */
    private XmlParser(boolean validating)
            throws ParserConfigurationException, FactoryConfigurationError {
        getDOMParser(this, this, validating);
    }

    private void getDOMParser(ErrorHandler eh, EntityResolver er,
                              boolean validating)
            throws ParserConfigurationException, FactoryConfigurationError {
        if (_factory == null) {
            _factory = DocumentBuilderFactory.newInstance();
        }

        _factory.setNamespaceAware(true);

        // The default for validation is 'false' so only set if the
        // request is 'true'
        if (validating) {
            _factory.setValidating(validating);
        }

        _parser = _factory.newDocumentBuilder();

        if (eh != null) {
            _parser.setErrorHandler(eh);
        }

        if (er != null) {
            _parser.setEntityResolver(er);
        }
    }

    private void getDOMParser(ErrorHandler eh, EntityResolver er)
            throws ParserConfigurationException, FactoryConfigurationError {
        getDOMParser(eh, er, false);
    }

    /**
     * Get the next parser in the chain for pool maintenance.
     */
    private XmlParser getNext() {
        return _next;
    }

    /**
     * Set the next parser in the chain for pool maintenance.
     */
    private void setNext(XmlParser p) {
        _next = p;
    }

    /**
     * Get the pooled status flag.
     */
    private boolean isPooled() {
        return _inPool;
    }

    /**
     * Set the pooled status flag.
     */
    private void setPooled(boolean b) {
        _inPool = b;
    }

    /**
     * Increment the usage count.
     */
    private void incUsages() {
        _usages++;
    }

    /**
     * Get the usage count.
     */
    private int getUsages() {
        return _usages;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Parsing
    //
    //////////////////////////////////////////////////////////////////////

    /**
     * Parses a string and returns the Document.
     * Validation may be on or off depending on how the parser is created.
     */
    private Document parseString(String xml) throws Spml2Exception {

        Document doc = null;

        // Hack, if validation is off, remove the DOCTYPE statement so
        // we can avoid DTD processing.  Even with validation off,
        // Xerces still does a lot of expensive DTD processing if
        // if sees a DOCTYPE.

        StringReader reader = null;
        if (_parser.isValidating())
            reader = new StringReader(xml);
        else {
            int index = xml.indexOf("DOCTYPE");
            if (index != -1) {
                for (; index < xml.length(); index++) {
                    if (xml.charAt(index) == '>') {
                        index++;
                        break;
                    }
                }
                if (index < xml.length())
                    xml = xml.substring(index);
            }

            // be smarter and try to reuse a StringBuffer?
            reader = new StringReader(xml);
        }

        InputSource is = new InputSource(reader);
        try {
            doc = _parser.parse(is);
        }
        catch (SAXParseException ex) {
            logFailure(xml);
            String msg = "XML Error " + getLocationString(ex) + ": " +
                         ex.getMessage();
            throw new Spml2Exception(msg, ex);
        }
        catch (SAXException ex) {
            // Assume this indicates a corruption within the parser,
            // create a new one.
            try {
                getDOMParser(this, this);
            }
            catch (ParserConfigurationException e) {
                _parser = null;
                throw new Spml2Exception(e);
            }
            catch (FactoryConfigurationError er) {
                _parser = null;
                throw new Spml2Exception(er);
            }

            logFailure(xml);
            wrapException(ex);
        }
        catch (IOException ex) {
            // should never get here during string parsing?
            throw new Spml2Exception(ex);
        }

        return doc;
    }

    /**
     * Store a string being parsed that resulted in an error to
     * a log file for diagnosis.
     */
    private void logFailure(String xml) {

        if (_logFailuresFile != null) {
            try {
                FileUtil.writeFile(_logFailuresFile, xml);
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    //////////////////////////////////////////////////////////////////////
    //
    // EntityResolver methods
    //
    //////////////////////////////////////////////////////////////////////

    /**
     * Attempt to resolve an entity reference to an absolute file path in
     * the user.dir.  We're not validating, so it doesn't matter yet.
     */
    public InputSource resolveEntity(String pubid, String sysid)
            throws SAXException, IOException {

        // System id seems to always come in as a fully qualified
        // pathname, even if it was only a leaf file name in the
        // DOCTYPE statement.

        // Sigh, we should be able to accept only a sysid, strip
        // off the directory, and try to locate that file relative
        // to the applicatio home directory.  For now, since we'll always
        // have a pubid, just use that.

        InputSource is = null;

        // look in the current working directory as defined
        // by the system property "user.dir"

        String cwd = System.getProperty("user.dir");
        if (cwd != null) {
            String path = cwd + File.separator + pubid;
            File f = new File(path);
            if (f.exists())
                is = new InputSource(new FileReader(f));
        }


        return is;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // ErrorHandler methods
    //
    //////////////////////////////////////////////////////////////////////

    /**
     * Parser warning callback handler.
     * <p/>
     * What should we do with warnings, collection them, barf to the console?
     * Might want a flag that treats warnings as errors.
     */
    public void warning(SAXParseException ex) {

        System.err.println("[Warning] " +
                           getLocationString(ex) + ": " +
                           ex.getMessage());
    }

    /**
     * Parser error callback handler
     * <p/>
     * We get here for things like DTD syntax errors, and DTD
     * validation errors.
     */
    public void error(SAXParseException ex) throws SAXParseException {

        throw ex;
    }

    /**
     * Parser fatal error callback handler.
     * <p/>
     * Convert this one to a Spml2Exception so we can capture
     * the full message text in one place.
     * We get here for things like DTD location failure.
     */
    public void fatalError(SAXParseException ex) throws SAXException {

        // don't print anything, just throw it and let parseString
        // convert it to a Spml2Exception
        throw ex;
    }

    /**
     * Returns a string describing the location of a parser error.
     */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();
    }

    /**
     * Check a SAXException we just caught for a wrapped exception.
     */
    private void wrapException(SAXException e) throws Spml2Exception {

        if (e instanceof SAXParseException) {

            SAXParseException spe = (SAXParseException) e;
            String msg = "XML Error" + getLocationString(spe) + ": " +
                         spe.getMessage();

            throw new Spml2Exception(msg, e);
        }
        else {
            // if this is wrapping something, return the wrapped exception
            Exception wrapped = e.getException();

            if (wrapped == null)
                throw new Spml2Exception(e);

            else if (wrapped instanceof Spml2Exception)
                throw(Spml2Exception) wrapped;

            else if (wrapped instanceof RuntimeException) {
                // unwrap these, as they often don't have messages and
                // we need to see the class name
                throw(RuntimeException) wrapped;
            }
            else {
                // wrap whatever it is in our wrapper
                throw new Spml2Exception(wrapped);
            }
        }
    }

    /**
     * Parses a string with or without validation and returns the Document.
     */
    public static Document parse(String xml)
            throws Spml2Exception {

        Document d = null;

        XmlParser p = XmlParser.getParser(false);
        try {
            d = p.parseString(xml);
        }
        finally {
            XmlParser.poolParser(p);
        }

        return d;
    }

    /**
     * Use this to turn on the logging of failures to the named file.
     * Set it to null to turn it off.
     *
     * @param file
     */
    public static void setLogFailuresFile(File file) {
        _logFailuresFile = file.toString();
    }
}
