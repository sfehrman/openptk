/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.openptk.exception.QueryException;

/**
 * The Query class is used to define simple and complex queries.
 * Simple queries are of type:
 * <table border="1" cellpadding="1" cellspacing="1">
 * <tr><td align="right">NOOPERATOR</td><td>No Operator</td></tr>
 * <tr><td align="right">EQ</td><td>Equals</td></tr>
 * <tr><td align="right">NE</td><td>Not Equals</td></tr>
 * <tr><td align="right">BEGINS</td><td>Begins With</td></tr>
 * <tr><td align="right">ENDS</td><td>Ends With</td></tr>
 * <tr><td align="right">CONTAINS</td><td>Contains</td></tr>
 * <tr><td align="right">LT</td><td>Less Than</td></tr>
 * <tr><td align="right">LE</td><td>Less Than Or Equals To</td></tr>
 * <tr><td align="right">GT</td><td>Greater Than</td></tr>
 * <tr><td align="right">GE</td><td>Greater Than Or Equals To</td></tr>
 * <tr><td align="right">LIKE</td><td>Sounds Like</td></tr>
 * </table>
 *
 * <br>
 * <p><b>Examples:</b></p>
 * <pre>
 * Query q1 = new Query(Query.TYPE_EQUALS,"lastname","Smith");
 * Query q2 = new Query(Query.TYPE_CONTAINS,"firstname","Rob");
 * </pre>
 * <br>
 *
 * <p>Complex queries are of type:</p>
 *
 * <table border="1">
 * <tr><td>AND</td><td>And</td></tr>
 * <tr><td>OR</td><td>Or</td></tr>
 * </table>
 *
 * <br>
 * <p><b>Examples:</b></p>
 * <pre>
 * Query qTop = new Query(Query.TYPE_OR);
 * Query q1 = new Query(Query.TYPE_CONTAINS,"firstname","Scott");
 * Query q2 = new Query(Query.TYPE_CONTAINS,"lastname","Scott");
 * qTop.addQuery(q1);
 * qTop.addQuery(q2);
 * </pre>
 * <br>
 *
 * <p>
 * A complex query has at least two sub-queries.  Sub-queries can be either
 * simple or complex
 * </p>
 *
 * <table>
 * <tr valign="top">
 * <td align="right"><b>NOTICE:</b></td>
 * <td>
 * All getter methods return copies of the Query's
 * data.  If a any of the data from a Query is modified, after it has been
 * "gotten", then it MUST be "put back".
 * </td>
 * </tr>
 * </table>
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//
//===================================================================
public final class Query
//===================================================================
{
   public final String CLASS_NAME = this.getClass().getSimpleName();
   public static final String NAMESPACE = "openptk";

   public static enum Type
   {
      NOOPERATOR, AND, OR, EQ, NE, BEGINS, ENDS, CONTAINS, LT, LE, GT, GE, LIKE
   }
   private boolean _isInternal = false;
   private boolean _useNamespace = false;
   private Query.Type _type = null;
   private String _frameworkName = null;
   private String _serviceName = null;
   private String _value = null;
   private List<Query> _qlist = null;

   /**
    * Create a new complex Query with a default Type of "AND".
    *
    * The "Attribute" and "Value" must be set if this Query will be a
    * simple Query (non-AND and non-OR type).  If this will be a simple
    * query, then the type must be changed.
    */
   //
   //----------------------------------------------------------------
   public Query()
   //----------------------------------------------------------------
   {
      return;
   }

   /**
    * Create a new simple or complex Query from an existing Query
    * The specified Query will be copied and it's data will be added 
    * to the new Query.
    * 
    * @param query an existing Query
    */
   //
   //----------------------------------------------------------------
   public Query(final Query query)
   //----------------------------------------------------------------
   {
      _useNamespace = query.getUseNamespace();
      _type = query.getType();
      _frameworkName = query.getName();
      _serviceName = query.getServiceName();
      _value = query.getValue();
      _qlist = query.getQueryList();
      return;
   }

   /**
    * Create a new simple Query with the specified Type.
    *
    * @param type the Query Type [ AND | OR ]
    */
   //
   //----------------------------------------------------------------
   public Query(final Query.Type type)
   //----------------------------------------------------------------
   {
      _type = type;
      return;
   }

   /**
    * Create a new complex Query with the specified Type and adds the
    * provided Query.  This is only valid for Queries that are of
    * type "AND" or "OR" where they require two or more sub-queries.
    *
    * @param type the Query Type [ AND | OR ]
    * @param query the sub-query used for the "AND" or "OR" logic
    * @throws QueryException
    */
   //
   //----------------------------------------------------------------
   public Query(final Query.Type type, final Query query) throws QueryException
   //----------------------------------------------------------------
   {
      if (type == Query.Type.AND || type == Query.Type.OR)
      {
         _type = type;
         this.addQuery(query);
      }
      else
      {
         throw new QueryException("Invalid Query.Type for use with sub-queries.");
      }
      return;
   }

   /**
    * Create a new simple Query using the provided information.
    * 
    * @param type the Query Type
    * @param name the attributes name 
    * @param value the attributes value
    *
    * <br>
    * <p><b>Example:</b></p>
    * <tt>
    * Query q = new Query(Query.TYPE_EQUALS,"lastname","Smith");
    * </tt>
    * <br>
    */
   //
   //----------------------------------------------------------------
   public Query(final Query.Type type, final String name, final String value)
   //----------------------------------------------------------------
   {
      _type = type;
      _frameworkName = name;
      _value = value;

      return;
   }

   /**
    * Create a new simple Query using the provided information.
    * 
    * @param type the Query TYPE (String name)
    * @param name the attributes name 
    * @param value the attributes value
    * @throws QueryException
    *
    * <br>
    * <p><b>Example:</b></p>
    * <tt>
    * Query q = new Query("equals","lastname","Smith");
    * </tt>
    * <br>
    */
   //
   //----------------------------------------------------------------
   public Query(final String type, final String name, final String value) throws QueryException
   //----------------------------------------------------------------
   {
      this.setType(type);
      _frameworkName = name;
      _value = value;

      return;
   }

   /**
    * Create a new complex Query using the List of Queries.
    * 
    * @param type the Type of complex Query
    * @param qlist a List of Query objects
    */
   //
   //----------------------------------------------------------------
   public Query(final Query.Type type, final List<Query> qlist)
   //----------------------------------------------------------------
   {
      _type = type;
      _qlist = qlist;

      return;
   }

   /**
    * Creates a copy of the Query.
    *
    * @return Query a new Query that is a copy of this one
    */
   //----------------------------------------------------------------
   public Query copy()
   //----------------------------------------------------------------
   {
      return new Query(this);
   }

   /**
    * Get the Query Type.
    * 
    * @return Query.Type the Query Type
    */
   //
   //----------------------------------------------------------------
   public final Query.Type getType()
   //----------------------------------------------------------------
   {
      Type type = null;

      if (_type != null)
      {
         type = _type;
      }

      return type;
   }

   /**
    * Get the String representation of the type.
    * 
    * @return String the type
    */
   //
   //----------------------------------------------------------------
   public final String getTypeAsString()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_type != null)
      {
         str = _type.toString();
      }

      return (str);
   }

   /**
    * Set the Query Type, using the String representaion for the Type.
    * The type must be a valid Query type.
    *
    * @param type the String representation of Query Type
    * @throws QueryException
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setType(final String type) throws QueryException
   //----------------------------------------------------------------
   {
      boolean bError = true;
      Query.Type[] typeArray = null;

      typeArray = Query.Type.values();

      for (int i = 0; i < typeArray.length; i++)
      {
         if (type.equalsIgnoreCase(typeArray[i].toString()))
         {
            this.setType(typeArray[i]);
            bError = false;
            break;
         }
      }

      if (bError)
      {
         throw new QueryException("Invalid Query.Type : '" + type + "'");
      }

      return;
   }

   /**
    * Set the Query's Type.
    * 
    * @param type the Query Type
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setType(final Query.Type type)
   //----------------------------------------------------------------
   {
      if (type != null)
      {
         _type = type;
      }

      return;
   }

   /**
    * Get the attribute name (Framework Name) from the Query.
    * 
    * @return String the attribute name
    */
   //
   //----------------------------------------------------------------
   public final String getName()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_frameworkName != null)
      {
         str = new String(_frameworkName); // always return a copy
      }

      return str;
   }

   /**
    * Set the Service attribute name, as known by the Service, in the Query
    * This method is not to be used by OpenPTK applications.
    * It's only used by OpenPTK Framework operations.
    * It's interface and operation may NOT be consistent
    *
    * @param name the Service attribute name
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setServiceName(final String name)
   //----------------------------------------------------------------
   {
      _serviceName = name;
      return;
   }

   /**
    * Get the Service attribute name, as known by the Service, from the Query
    * This method is not to be used by OpenPTK applications.
    * It's only used by OpenPTK Framework operations.
    * It's interface and operation may NOT be consistent
    *
    * @return String the service attribute name
    */
   //
   //----------------------------------------------------------------
   public final String getServiceName()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_serviceName != null)
      {
         str = new String(_serviceName); // always return a copy
      }

      return str;
   }

   /**
    * Set the attribute name.
    * 
    * @param name the attributes name
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setName(final String name)
   //----------------------------------------------------------------
   {
      _frameworkName = name;
      return;
   }

   /**
    * Get the attribute value.
    * 
    * @return String the attributes value
    */
   //
   //----------------------------------------------------------------
   public final String getValue()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_value != null)
      {
         str = new String(_value); // always return a copy
      }

      return str;
   }

   /**
    * Set the attribute value.
    * 
    * @param value the attributes value
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setValue(final String value)
   //----------------------------------------------------------------
   {
      _value = value;
      return;
   }

   /**
    * Get the value [ true | false ] of the flag which determines
    * if the "namespace" feature should be used with the toXML() method.
    * 
    * @return boolean the value of this flag
    */
   //
   //----------------------------------------------------------------
   public final boolean getUseNamespace()
   //----------------------------------------------------------------
   {
      Boolean bool = true;

      bool = _useNamespace;

      return bool;
   }

   /**
    * Set / replace the List<Query> of sub-queries.
    * @param qlist
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setQueryList(List<Query> qlist)
   //----------------------------------------------------------------
   {
      _qlist = qlist;
      return;
   }

   /**
    * Set the value [ true | false ] for this flag.  If true, the toXML()
    * method will use a "namespace" feature.
    * @param bool true or false
    */
   //
   //----------------------------------------------------------------
   public final synchronized void setUseNamespace(final boolean bool)
   //----------------------------------------------------------------
   {
      _useNamespace = bool;
      return;
   }

   /**
    *
    * @return boolean is this Internal
    */
   //----------------------------------------------------------------
   public final boolean isInternal()
   //----------------------------------------------------------------
   {
      Boolean bool = true;

      bool = _isInternal;

      return bool;
   }

   /**
    * Flag the Query as internal [ true | false ].
    * 
    * @param bool 
    */
   //----------------------------------------------------------------
   public final synchronized void setInternal(final boolean bool)
   //----------------------------------------------------------------
   {
      _isInternal = bool;

      return;
   }

   /**
    * Gets a List of the sub-Queries, if the Query is complex the List
    * will contain two or more Queries.
    * 
    * @return List a List of sub-queries
    */
   //
   //----------------------------------------------------------------
   public final synchronized List<Query> getQueryList()
   //----------------------------------------------------------------
   {
      /*
       * NOTICE: this is a copy of the list
       */

      Query query = null;
      List<Query> list = null;
      Iterator<Query> iter = null;

      if (_qlist != null)
      {
         list = new LinkedList<Query>();
         iter = _qlist.iterator();
         while (iter.hasNext())
         {
            query = iter.next().copy();
            list.add(query);
         }
      }

      return list;
   }

   /**
    * Adds another Query (as a sub-query), making the Query complex.
    * 
    * @param query an existing Query
    */
   //
   //----------------------------------------------------------------
   public final synchronized void addQuery(final Query query)
   //----------------------------------------------------------------
   {
      if (_qlist == null)
      {
         _qlist = new LinkedList<Query>();
      }

      _qlist.add(query);

      return;
   }

   /**
    * Returns a String that represents the Query
    * Supports both simple and complex queries.
    * 
    * @return String the Query as a String
    */
   //
   //----------------------------------------------------------------
   @Override
   public String toString()
   //----------------------------------------------------------------
   {
      return getQueryDataString(0, this);
   }

   /**
    * Returns an XML structure that represents the Query
    * Supports both simple and complex queries.
    * 
    * @return String the XML data
    */
   //
   //----------------------------------------------------------------
   public final String toXML()
   //----------------------------------------------------------------
   {
      return this.getQueryDataXML(0, this);
   }

   /****************************************************************/
   /********************   PRIVATE    ******************************/
   /****************************************************************/
   //
   //----------------------------------------------------------------
   private synchronized String getQueryDataXML(final int iLevel, final Query query)
   //----------------------------------------------------------------
   {
      int iSpacing = 3;
      int iOffset = iSpacing * iLevel;
      int iNextLevel = iLevel + 1;
      StringBuilder buf = new StringBuilder();
      StringBuilder offset = new StringBuilder();
      Query subquery = null;
      Iterator<Query> iter = null;
      List<Query> subqlist = null;

      subqlist = query.getQueryList();

      for (int i = 0; i < iOffset; i++)
      {
         offset.append(" ");
      }

      buf.append(offset);

      if (_useNamespace)
      {
         buf.append("<" + NAMESPACE + ":Query");
      }
      else
      {
         buf.append("<Query");
      }

      buf.append(" type=\"").append(query.getTypeAsString()).append("\"");

      if (subqlist != null)
      {
         buf.append(">\n");

         iter = subqlist.iterator();
         while (iter.hasNext())
         {
            subquery = iter.next();
            subquery.setUseNamespace(this.getUseNamespace());
            buf.append(getQueryDataXML(iNextLevel, subquery));
         }

         if (_useNamespace)
         {
            buf.append("</").append(NAMESPACE).append(":Query>\n");
         }
         else
         {
            buf.append(offset).append("</Query>\n");
         }
      }
      else
      {
         buf.append(" name=\"").append(query.getName()).append("\"");
         buf.append(" serviceName=\"").append(query.getServiceName()).append("\"");
         buf.append(" value=\"").append(query.getValue()).append("\"");
         buf.append("/>\n");
      }

      return buf.toString();
   }

   //----------------------------------------------------------------
   private synchronized String getQueryDataString(final int iLevel, final Query query)
   //----------------------------------------------------------------
   {
      int iNextLevel = iLevel + 1;
      String service = null;
      StringBuilder buf = new StringBuilder();
      Query subquery = null;
      Iterator<Query> iter = null;
      List<Query> subqlist = null;

      subqlist = query.getQueryList();

      buf.append("Level=").append(iLevel).append(", Type=").append(query.getTypeAsString());

      if (subqlist != null)
      {
         buf.append(", SubQueries=").append(subqlist.size()).append("; ");
         iter = subqlist.iterator();
         while (iter.hasNext())
         {
            subquery = iter.next();
            buf.append(getQueryDataString(iNextLevel, subquery));
         }
      }
      else
      {
         service = query.getServiceName();
         buf.append(", name=").append(query.getName());
         if (service != null && service.length() > 0)
         {
            buf.append(", serviceName=").append(service);
         }
         buf.append(", value=").append(query.getValue()).append("; ");
      }

      return buf.toString();
   }
}
