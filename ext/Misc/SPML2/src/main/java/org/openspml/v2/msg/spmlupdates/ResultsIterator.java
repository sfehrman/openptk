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

import org.openspml.v2.msg.Marshallable;
import org.openspml.v2.msg.PrefixAndNamespaceTuple;
import org.openspml.v2.msg.XMLMarshaller;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.util.Spml2Exception;

/**
 * <br>&lt;complexType name="ResultsIteratorType"&gt;
 * <br>&lt;complexContent&gt;
 * <br>&lt;extension base="spml:ExtensibleType"&gt;
 * <br>&lt;attribute name="ID" type="xsd:ID"/&gt;
 * <br>&lt;/extension&gt;
 * <br>&lt;/complexContent&gt;
 * <br>&lt;/complexType&gt;
 *
 * @author kent.spaulding@sun.com
 *         <p/>
 *         Date: Feb 16, 2006
 */
public class ResultsIterator extends Extensible implements Marshallable {

    private static final String code_id = "$Id: ResultsIterator.java,v 1.5 2006/05/15 23:31:00 kas Exp $";

    private String m_ID = null; // required

    public ResultsIterator() { ; }

    public ResultsIterator(String ID) {
        assert (ID != null);
        m_ID = ID;
    }

    public String getID() {
        return m_ID;
    }

    public void setID(String ID) {
        assert (ID != null);
        m_ID = ID;
    }

    public PrefixAndNamespaceTuple[] getNamespacesInfo() {
        return PrefixAndNamespaceTuple.concatNamespacesInfo(
                super.getNamespacesInfo(),
                NamespaceDefinitions.getMarshallableNamespacesInfo());
    }

    public String toXML(XMLMarshaller m) throws Spml2Exception {
        return m.marshall(this);
    }

    public boolean isValid() {
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultsIterator)) return false;
        if (!super.equals(o)) return false;

        final ResultsIterator resultsIterator = (ResultsIterator) o;

        if (!m_ID.equals(resultsIterator.m_ID)) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 29 * result + m_ID.hashCode();
        return result;
    }
}
