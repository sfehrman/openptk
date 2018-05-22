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
package org.openptk.plugin.mimeutil;

import java.util.Collection;
import java.util.Iterator;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

import org.openptk.api.State;
import org.openptk.exception.PluginException;
import org.openptk.plugin.Plugin;
import org.openptk.structure.BasicStructure;
import org.openptk.structure.StructureIF;
import org.openptk.structure.StructureType;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class MimeUtilPlugin extends Plugin
//===================================================================
{

   public static final String PROP_DETECTOR = "mimeutil.detector";
   public static final String PROP_MIMETYPE = "structure.mimetype";
   private final String CLASS_NAME = this.getClass().getSimpleName();
   private MimeUtil2 _util = null;

   //----------------------------------------------------------------
   public MimeUtilPlugin()
   //----------------------------------------------------------------
   {
      super();
      this.setDescription("Mime Type detection (eu.medsea.mimeutil)");
      return;
   }


   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public void startup() throws PluginException
   //----------------------------------------------------------------
   {
      Object object = null;
      String METHOD_NAME = CLASS_NAME + ":startup(): ";
      String detectorClassname = null;
      String propName = null;
      String propValue = null;

      super.startup();

      /*
       * Get the "detector" classname ... test it
       */

      detectorClassname = this.getProperty(PROP_DETECTOR);
      if (detectorClassname == null || detectorClassname.length() < 1)
      {
         this.setState(State.DISABLED);
         this.handleError(METHOD_NAME + "Property '" + PROP_DETECTOR + "' is null");
      }

      try
      {
         object = Class.forName(detectorClassname).newInstance();
      }
      catch (ClassNotFoundException ex)
      {
         this.setState(State.DISABLED);
         this.handleError(METHOD_NAME + ex.getMessage());
      }
      catch (InstantiationException ex)
      {
         this.setState(State.DISABLED);
         this.handleError(METHOD_NAME + ex.getMessage());
      }
      catch (IllegalAccessException ex)
      {
         this.setState(State.DISABLED);
         this.handleError(METHOD_NAME + ex.getMessage());
      }

      propName = PROP_MIMETYPE;
      propValue = this.getProperty(propName);
      if (propValue == null || propValue.length() < 1)
      {
         this.setState(State.DISABLED);
         this.handleError(METHOD_NAME + "Property '" + propName + "' is null");
      }

      _util = new MimeUtil2();
      _util.registerMimeDetector(detectorClassname);

      return;
   }


   /**
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @Override
   public void shutdown() throws PluginException
   //----------------------------------------------------------------
   {
      super.shutdown();
      return;
   }


   /**
    * @param structIn
    * @return
    * @throws PluginException
    */
   //----------------------------------------------------------------
   @SuppressWarnings("rawtypes")
   @Override
   public synchronized StructureIF execute(final StructureIF structIn) throws PluginException
   //----------------------------------------------------------------
   {
      int icnt = 0;
      byte[] bytes = null;
      Object object = null;
      Object[] objArray = null;
      String METHOD_NAME = CLASS_NAME + ":execute(): ";
      StructureIF structOut = null;
      Collection collection = null;
      Iterator iterator = null;
      MimeType mimeType = null;

      if (this.getState() != State.READY)
      {
         this.handleError(METHOD_NAME + "Plugin is not READY, state='" +
            this.getStateAsString() + "'");
      }

      if (structIn == null)
      {
         this.handleError(METHOD_NAME + "Input Structure is null");
      }

      if (structIn.getValueType() != StructureType.OBJECT)
      {
         this.handleError(METHOD_NAME + "Structure value is not an OBJECT, type='" +
            structIn.getValueType().toString() + "'");
      }

      object = structIn.getValue();
      if (object == null)
      {
         this.handleError(METHOD_NAME + "Structure value is null");
      }

      if (object instanceof byte[])
      {
         bytes = (byte[]) object;
         collection = _util.getMimeTypes(bytes);
      }
      else
      {
         this.handleError(METHOD_NAME + "Structure value (Object) is not a byte[], type='" +
            object.getClass().getSimpleName() + "'");
      }

      if (collection.size() < 1)
      {
         this.handleError(METHOD_NAME + "no Mime Types were found");
      }

      iterator = collection.iterator();
      objArray = new Object[collection.size()];
      while (iterator.hasNext())
      {
         objArray[icnt++] = iterator.next();
      }

      if (objArray[0] instanceof MimeType)
      {
         mimeType = (MimeType) objArray[0];
      }
      else
      {
         this.handleError(METHOD_NAME + "Object is not a MimeType, class='" +
            objArray[0].getClass().getSimpleName() + "'");
      }

      structOut = new BasicStructure(this.getProperty(PROP_MIMETYPE), mimeType.toString());

      return structOut;
   }
}
