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
package org.openptk.context.actions;

import java.util.Iterator;
import java.util.List;

import org.openptk.common.AttrIF;
import org.openptk.common.BasicAttr;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.context.ContextIF;
import org.openptk.exception.ActionException;
import org.openptk.exception.ProvisionException;
import org.openptk.spi.ServiceIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Action extends Component implements ActionIF
//===================================================================
{
   private String CLASS_NAME = this.getClass().getSimpleName();
   protected ContextIF _context = null;
   protected ActionMode _mode = ActionMode.NONE;

   //----------------------------------------------------------------
   public Action()
   //----------------------------------------------------------------
   {
      super();
      this.setCategory(Category.ACTION);
      return;
   }


   /**
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws ActionException
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void shutdown() throws ActionException
   //----------------------------------------------------------------
   {
      return;
   }


   /**
    * @param service
    * @param request
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void preAction(final ServiceIF service, final RequestIF request) throws ActionException
   //----------------------------------------------------------------
   {
      this.handleError("preAction not implemented");
      return;
   }


   /**
    * @param service
    * @param response
    * @throws ActionException
    */
   //----------------------------------------------------------------
   @Override
   public void postAction(final ServiceIF service, final ResponseIF response) throws ActionException
   //----------------------------------------------------------------
   {
      this.handleError("postAction not implemented");
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
      _context = context;
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
    * @param mode
    */
   //----------------------------------------------------------------
   @Override
   public final synchronized void setMode(final ActionMode mode)
   //----------------------------------------------------------------
   {
      _mode = mode;
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final ActionMode getMode()
   //----------------------------------------------------------------
   {
      return _mode;
   }

   /*
    * =================
    * PROTECTED METHODS
    * =================
    */

   /**
    * @param uidSyntax
    * @param comp
    * @return
    * @throws ActionException
    */
   //----------------------------------------------------------------
   protected String deriveUniqueId(String uidSyntax, final ComponentIF comp) throws ActionException
   //----------------------------------------------------------------
   {
      boolean done = false;
      int varBegin = 0;
      int varEnd = 0;
      int len = 0;
      String METHOD_NAME = CLASS_NAME + ":deriveUniqueId(): ";
      String literal = null;
      String name = null;
      String value = null;
      StringBuffer buf = new StringBuffer();
      AttrIF attr = null;

      /*
       *
       * BEGIN = "${"
       * END   = "}"
       *
       *           1         2         3         4         5         6
       * 0123456789012345678901234567890123456789012345678901234567890
       * ${contextid}-${subjectid}-${relationshipid}
       *
       */

      if (uidSyntax == null || uidSyntax.length() < 1)
      {
         this.handleError(METHOD_NAME + "UniqueId Template is null");
      }

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
                  name = null;
                  value = null;
                  name = uidSyntax.substring(varBegin + VAR_BEGIN.length(), varEnd);
                  if (name != null && name.length() > 0)
                  {
                     attr = null;
                     attr = comp.getAttribute(name);
                     if (attr == null)
                     {
                        this.handleError(METHOD_NAME + "Variable '" + name +
                           "' does not have a matching attribute");
                     }
                     value = attr.getValueAsString();

                     if (value != null && value.length() > 0)
                     {
                        buf.append(value);
                     }
                  }
                  else
                  {
                     this.handleError(METHOD_NAME + "Variable is null, begin=" +
                        varBegin + ", end=" + varEnd);
                  }
                  uidSyntax = uidSyntax.substring(varEnd + 1);
               }
               else
               {
                  this.handleError(METHOD_NAME + "Invalid attribute syntax: '" +
                     uidSyntax + "'");
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
         this.handleError(METHOD_NAME + "UniqueId could not be derived");
      }

      return buf.toString();
   }


   /**
    * @param service
    * @param comp
    * @param operation
    * @throws ActionException
    */
   //----------------------------------------------------------------
   protected void includeOperationAttrs(final ServiceIF service, final ComponentIF comp, final Operation operation) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":includeOperationAttrs(): ";
      String name = null;
      List<String> availAttrNames = null;
      Iterator<String> iterString = null;
      AttrIF attr = null;

      if (comp == null)
      {
         this.handleError(METHOD_NAME + "Component is null");
      }

      if (operation == null)
      {
         this.handleError(METHOD_NAME + "Operation is null");
      }

      try
      {
         // Add the attributes to return for the operation
         availAttrNames = _context.getSubject().getAvailableAttributes(operation);
      }
      catch (ProvisionException ex)
      {
         this.handleError(METHOD_NAME + "context.getSubject().getAvailableAttributes(): " +
            ex.getMessage());
      }

      if (availAttrNames != null && !availAttrNames.isEmpty())
      {
         iterString = availAttrNames.iterator();
         while (iterString.hasNext())
         {
            name = iterString.next();
            attr = new BasicAttr(name);
            attr.setServiceName(service.getSrvcName(operation, name));
            comp.setAttribute(name, attr);
         }
      }
      else
      {
         this.handleError(METHOD_NAME + "Operation '" + operation.toString() +
            "' does not have any assigned Attributes.");
      }

      return;
   }


   /**
    * @param propName
    * @return
    * @throws ActionException
    */
   //----------------------------------------------------------------
   protected String getPropValue(final String propName) throws ActionException
   //----------------------------------------------------------------
   {
      String METHOD_NAME = CLASS_NAME + ":getPropValue(): ";
      String value = null;

      value = this.getProperty(propName);
      if (value == null || value.length() < 1)
      {
         this.handleError(METHOD_NAME + "Missing Required Property: '" +
            propName + "'");
      }

      return value;
   }


   /**
    * @param msg
    * @throws ActionException
    */
   //----------------------------------------------------------------
   protected void handleError(final String msg) throws ActionException
   //----------------------------------------------------------------
   {
      String str = null;

      if ( msg == null || msg.length() < 1)
      {
         str = "(null message)";
      }
      else
      {
         str = msg;
      }
      throw new ActionException(str);
   }
}
