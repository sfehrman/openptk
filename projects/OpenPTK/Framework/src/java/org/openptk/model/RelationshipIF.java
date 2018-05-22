/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009-2010 Sun Microsystems, Inc.
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
 * or https://openptk.dev.java.net/OpenPTK.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the reference to
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.model;

import org.openptk.api.ElementIF;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public interface RelationshipIF extends ComponentIF
//===================================================================
{

   public static final String VAR_BEGIN = "${";
   public static final String VAR_END = "}";
   public static final String VAR_SCOPE_ATTR = "attr:";
   public static final String VAR_SCOPE_PATH = "path:";
   public static final String PROP_UNIQUEID = "relationship.uniqueid";

   /**
    * @param query
    */
   public void setQuery(Query query);

   /**
    * @return
    */
   public Query getQuery();

   /**
    * @return
    */
   public RelationshipType getType();

   /**
    * @param context
    */
   public void setContext(ContextIF context);

   /**
    * @return
    */
   public ContextIF getContext();

   /**
    * @param operation
    * @param result
    * @param struct
    * @return
    * @throws Exception
    */
   public Output execute(Operation operation, ElementIF result, StructureIF struct) throws Exception;
}
