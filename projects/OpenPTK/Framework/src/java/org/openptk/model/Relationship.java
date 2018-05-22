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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Query;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.Operation;
import org.openptk.context.ContextIF;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ProvisionException;
import org.openptk.structure.StructureIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Relationship extends Component implements RelationshipIF
//===================================================================
{
   protected RelationshipType _type = RelationshipType.ENTRY;
   protected ContextIF _context = null;
   protected Query _query = null;
   private String CLASS_NAME = this.getClass().getSimpleName();

   //----------------------------------------------------------------
   public Relationship()
   //----------------------------------------------------------------
   {
      this.setCategory(Category.RELATIONSHIP);
      return;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   public Relationship(final Query query)
   //----------------------------------------------------------------
   {
      this.setCategory(Category.RELATIONSHIP);
      _query = query;
      return;
   }

   /**
    * @param context
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setContext(final ContextIF context)
   //----------------------------------------------------------------
   {
      if (context != null)
      {
         _context = context;
      }
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ContextIF getContext()
   //----------------------------------------------------------------
   {
      return _context;
   }

   /**
    * @param query
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setQuery(final Query query)
   //----------------------------------------------------------------
   {
      _query = query;
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Query getQuery()
   //----------------------------------------------------------------
   {
      return _query;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final RelationshipType getType()
   //----------------------------------------------------------------
   {
      return _type;
   }

   /**
    * @param input
    * @param element
    * @param structParamPath
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void deriveUniqueId(final Input input, final ElementIF element, final StructureIF structParamPath) throws Exception
   //----------------------------------------------------------------
   {
      boolean done = false;
      int varBegin = 0;
      int varEnd = 0;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":deriveUniqueId(): ";
      String origSyntax = null;
      String uidSyntax = null;
      String variable = null;
      String literal = null;
      String name = null;
      String value = null;
      StringBuilder buf = new StringBuilder();
      AttributeIF attr = null;
      StructureIF struct = null;

      /*
       * Get the Property for "this" Relationship that defines the "uniqueid".
       * The Property can contain a combination of literals and variables.
       * The variables are enclosed in the "${" and "}" strings.
       * The literals are ... everything else.
       * All of the literals and variable values are concatinated together.
       *
       * A variable can obtained from either the Subject (element) or from
       * a URI path parameter (structParamPath).  The variable needs to
       * contain the "scope" and "name".  The scope "attr:" is used to
       * get the value from an attribute in the Subject (element). The scope
       * "path:" is used to get the value from a path parameter.
       *
       * BEGIN = "${"
       * END   = "}"
       * 
       *           1         2         3         4         5         6
       * 0123456789012345678901234567890123456789012345678901234567890
       * ${attr:location}
       * ${path:contextid}-${path:subjectid}-${path:relationshipid}
       *
       */

      uidSyntax = this.getProperty(PROP_UNIQUEID);

      if (uidSyntax == null || uidSyntax.length() < 1)
      {
         this.handleError(METHOD_NAME + "Property '" + PROP_UNIQUEID + "' is null");
      }

      origSyntax = uidSyntax;
      
      while (!done)
      {
         varBegin = 0;
         varEnd = 0;
         len = 0;
         len = uidSyntax.length();
         if (len > 0)
         {
            varBegin = uidSyntax.indexOf(VAR_BEGIN);
            if (varBegin < 0) // no variables
            {
               buf.append(uidSyntax);
               done = true;
            }
            else if (varBegin == 0)
            {
               varEnd = uidSyntax.indexOf(VAR_END);
               if ((varBegin + VAR_BEGIN.length()) < varEnd)
               {
                  variable = null;
                  variable = uidSyntax.substring(varBegin + VAR_BEGIN.length(), varEnd);
                  if (variable != null && variable.length() > 0)
                  {
                     name = null;
                     value = null;
                     if (variable.startsWith(VAR_SCOPE_ATTR))
                     {
                        attr = null;
                        if (variable.length() <= VAR_SCOPE_ATTR.length())
                        {
                           this.handleError(METHOD_NAME + "Invalid attribute variable syntax: '"
                              + variable + "'");
                        }
                        name = variable.substring(VAR_SCOPE_ATTR.length());
                        attr = element.getAttribute(name);
                        if (attr == null)
                        {
                           this.handleError(METHOD_NAME + "Variable '" + name
                              + "' does not have a matching attribute");
                        }
                        value = attr.getValueAsString();
                     }
                     else if (variable.startsWith(VAR_SCOPE_PATH))
                     {
                        struct = null;
                        if (variable.length() <= VAR_SCOPE_PATH.length())
                        {
                           this.handleError(METHOD_NAME + "Invalid path parameter variable syntax: '"
                              + variable + "'");
                        }
                        name = variable.substring(VAR_SCOPE_ATTR.length());
                        struct = structParamPath.getChild(name);
                        if (struct == null)
                        {
                           this.handleError(METHOD_NAME + "Variable '" + name
                              + "' does not have a matching path parameter");
                        }
                        value = struct.getValueAsString();
                     }
                     else
                     {
                        this.handleError(METHOD_NAME + "Invalid variable scope: '"
                           + variable + "'");
                     }
                     
                     if (value != null && value.length() > 0)
                     {
                        buf.append(value);
                     }
                     else
                     {
                           this.handleError(METHOD_NAME + "Value for '" + origSyntax
                              + "' is null / empty");
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Variable is null, begin="
                        + varBegin + ", end=" + varEnd);
                  }
                  uidSyntax = uidSyntax.substring(varEnd + 1);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Invalid attribute syntax: '"
                     + uidSyntax + "'");
                  done = true;
               }
            }
            else // varBegin > 0
            {
               literal = uidSyntax.substring(0, varBegin);
               buf.append(literal);
               uidSyntax = uidSyntax.substring(varBegin);
            }
         }
         else
         {
            done = true;
         }
      }

      if (buf.length() < 1)
      {
         this.handleError(METHOD_NAME + "UniqueId could not be derived, syntax='"
            + origSyntax + "'");
      }

      input.setUniqueId(buf.toString());

      return;
   }

   /**
    * @param result
    * @param input
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void updateQuery(final ElementIF result, final Input input) throws Exception
   //----------------------------------------------------------------
   {
      Object uniqueId = null;
      Object contextId = null;
      String METHOD_NAME = CLASS_NAME + ":updateQuery(): ";
      String key = null;
      String name = null;
      String value = null;
      String attr = null;
      Query query = null;
      AttributeIF attribute = null;

      if (result == null)
      {
         this.handleError(METHOD_NAME + "Argument Result is null");
      }

      if (input == null)
      {
         this.handleError(METHOD_NAME + "Argument Input is null");
      }

      /*
       * Check the Query to see if the "value" is a "literal" or if it's
       * an attribute name.  An attribute name will begin-with "${" and
       * end-width "}".  If the attibute name is anything other than "uniqueid"
       * then the Subject needs to be read so that the attribute can be used.
       */

      if (_query != null)
      {
         query = new Query();
         query.setType(_query.getType());
         query.setName(_query.getName());
         query.setValue(_query.getValue());
         query.setInternal(_query.isInternal());

         uniqueId = result.getUniqueId();
         if (uniqueId == null)
         {
            this.handleError(METHOD_NAME + "UniqueId (from Result) is null");
         }

         contextId = _context.getUniqueId();
         if (contextId == null)
         {
            this.handleError(METHOD_NAME + "ContextId (from Result) is null");
         }

         key = _context.getProperty("key");
         if (key == null || key.length() < 1)
         {
            this.handleError(METHOD_NAME
               + "Context '" + contextId.toString() + "' does not have the Property 'key' configured");
         }

         name = query.getName();
         if (name == null || name.length() < 1)
         {
            this.handleError(METHOD_NAME
               + "Query for Relationship '"
               + _type.toString().toLowerCase() + "' does not have a Name");
         }

         value = query.getValue();
         if (value == null || value.length() < 1)
         {
            this.handleError(METHOD_NAME
               + "Query for Relationship '"
               + _type.toString().toLowerCase() + "' does not have a Value");
         }

         if (value.startsWith(VAR_BEGIN) && value.endsWith(VAR_END))
         {
            /*
             * BEGIN = "${"
             * END   = "}"
             * var   = "${uniqueid}"
             *          01234567890
             * value = var.substring(BEGIN.length(), var.length() - END.length());
             * value = var.substring(2, 11 - 1); // 10
             * value = "uniqueid"
             */

            attr = value.substring(VAR_BEGIN.length(), value.length() - VAR_END.length());

            if (attr.equalsIgnoreCase(key))
            {
               value = uniqueId.toString();
            }
            else
            {
               /*
                * The attribute is not the "key" (uniqueid)
                * get the attribute from the Result
                */

               attribute = result.getAttribute(attr);
               if (attribute == null)
               {
                  this.handleError(METHOD_NAME
                     + "Attribute '" + attr + "' for Subject '" + uniqueId.toString() + "' is null");
               }

               value = attribute.getValueAsString();
            }
            query.setValue(value);
         }
         input.setQuery(query);
      } // if ( _query != null )

      return;
   }

   /**
    * @param subject
    * @param input
    * @param operation
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void includeOperationAttributes(final SubjectIF subject, final Input input, final Operation operation) throws Exception
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":includeOperationAttributes(): ";
      List<String> availAttrNames = null;
      Iterator<String> iterString = null;

      // Add the attributes to return for the operation

      availAttrNames = subject.getAvailableAttributes(operation);
      if (availAttrNames != null && !availAttrNames.isEmpty())
      {
         iterString = availAttrNames.iterator();
         while (iterString.hasNext())
         {
            input.addAttribute(iterString.next());
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Operation '" + operation.toString()
            + "' does not have any assigned Attributes.");
      }

      return;
   }

   /**
    * @param ancestors
    * @param subject
    * @param child
    * @param input
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void getParent(final Map<String, ElementIF> ancestors, final SubjectIF subject,
      final ElementIF child, final Input input) throws Exception
   //----------------------------------------------------------------
   {
      Object uidParent = null;
      String METHOD_NAME = CLASS_NAME + ":getParent(): ";
      String qValue = null;
      Query query = null;
      Output output = null;
      List<ElementIF> results = null;
      ElementIF parent = null;

      try
      {
         this.updateQuery(child, input);
      }
      catch (Exception ex)
      {
         this.handleError(METHOD_NAME + "Could not update query: " + ex.getMessage());
      }

      query = input.getQuery();
      if (query != null)
      {
         qValue = query.getValue();
         input.removeQuery();
      }

      if (qValue != null && qValue.length() > 0)
      {
         input.setUniqueId(qValue);
         try
         {
            output = subject.execute(Operation.READ, input);
         }
         catch (ProvisionException ex)
         {
            this.handleError(METHOD_NAME + "Could not execute Search Operation: " + ex.getMessage());
         }

         if (output != null)
         {
            results = output.getResults();
            if (results != null && results.size() == 1)
            {
               parent = results.get(0);
               if (parent != null)
               {
                  uidParent = parent.getUniqueId();

                  /*
                   * If the "parent" is already in the Linked Map ...
                   * there could be a "loop" situation.
                   * do not add it to the Map and stop recursive method call
                   */

                  if (uidParent != null)
                  {
                     if (!ancestors.containsKey(uidParent.toString()))
                     {
                        ancestors.put(uidParent.toString(), parent);

                        this.getParent(ancestors, subject, parent, input);
                     }
                  }
               }
            }
         }
      }
      return;
   }

   /**
    * @param msg
    * @throws Exception
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws Exception
   //----------------------------------------------------------------
   {
      String str = null;

      if (msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }
      throw new Exception(str);
   }
}
