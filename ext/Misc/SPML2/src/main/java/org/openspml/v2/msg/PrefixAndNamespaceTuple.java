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
/*
 * Copyright 2006 Sun Microsystems, Inc.  All rights reserved.
 *
 * Use is subject to license terms.
 */
package org.openspml.v2.msg;


/**
 * We need classes to return some info about the namespaces that they use.
 */
public class PrefixAndNamespaceTuple {

    public final String prefix;
    public final String namespaceURI;
    public final boolean isDefault;

    public PrefixAndNamespaceTuple(String prefix,
                                   String namespaceURI,
                                   boolean isDefault) {
        this.prefix = prefix;
        this.namespaceURI = namespaceURI;
        this.isDefault = isDefault;
    }

    public PrefixAndNamespaceTuple(String prefix, String namespaceURI) {
        this(prefix, namespaceURI, false);
    }

    // make these easy to make unique
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrefixAndNamespaceTuple)) return false;

        final PrefixAndNamespaceTuple prefixAndNamespaceTuple = (PrefixAndNamespaceTuple) o;

        if (!prefix.equals(prefixAndNamespaceTuple.prefix)) return false;

        return true;
    }

    public int hashCode() {
        return prefix.hashCode();
    }

    public static PrefixAndNamespaceTuple[] concatNamespacesInfo(PrefixAndNamespaceTuple[] first,
                                                                 PrefixAndNamespaceTuple[] second) {

        PrefixAndNamespaceTuple[] res = new PrefixAndNamespaceTuple[first.length + second.length];
        System.arraycopy(first, 0, res, 0, first.length);
        System.arraycopy(second, 0, res, first.length, second.length);
        return res;
    }
}
