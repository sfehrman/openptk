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

import org.openspml.v2.util.Spml2Exception;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;


public class XmlUtil {

    /**
     * This is not part of DOM - so we provide a helper method; this
     * one assumes xerces.
     */
    public static String serializeNode(Node node) throws Spml2Exception {

        String serialization = null;
        StringWriter writer = null;

        try {
            TransformerFactory transformerFactory =
                    TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source input = new DOMSource(node);
            writer = new StringWriter();
            Result output = new StreamResult(writer);

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            if (node.getNodeType() == Node.DOCUMENT_NODE) {
                DocumentType doctype = ((Document) node).getDoctype();
                String publicId = null;
                String systemId = null;

                if (doctype != null) {
                    publicId = doctype.getPublicId();
                    systemId = doctype.getSystemId();
                }

                if (systemId != null) {
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                                                  systemId);
                    if (publicId != null) {
                        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                                                      publicId);
                    }
                }
            }

            transformer.transform(input, output);
            serialization = writer.toString();
            return serialization;
        }
        catch (TransformerConfigurationException ex) {
            throw new Spml2Exception(ex);
        }
        catch (TransformerException ex) {
            throw new Spml2Exception(ex);
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            catch (java.io.IOException ioe) {
            }
        }
    }

    public static String makeXmlFragmentFromDoc(String xml) {
        String toRemove = "\\<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?\\>\n";
        StringBuffer buffer = new StringBuffer();
        String[] pieces = xml.split(toRemove);
        for (int k = 0; k < pieces.length; k++) {
            buffer.append(pieces[k]);
        }
        return buffer.toString();
    }

    public static Element getFirstChildElement(Node node) {

        // we want the first child element of the given node
        Element firstChildElement = null;
        if (node != null) {
            Node child = node.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    firstChildElement = (Element) child;
                    break;
                }
                child = child.getNextSibling();
            }
        }
        return firstChildElement;
    }

    public static String getSoapBodyContents(String message) throws Spml2Exception {

        Document doc = XmlParser.parse(message);

        // Get the first <soap:Body> element in the DOM
        NodeList list = doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/",
                                                   "Body");
        Node node = list.item(0);
        node = XmlUtil.getFirstChildElement(node);
        return XmlUtil.serializeNode(node);
    }
}
