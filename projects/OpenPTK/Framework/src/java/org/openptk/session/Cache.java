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
 * Portions Copyright 2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.session;

import org.openptk.api.State;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.engine.EngineIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Cache extends Component implements CacheIF
//===================================================================
{
   private Object _value = null;
   private Long _ttl = null;

   /**
    * @param session
    */
   //----------------------------------------------------------------
   public Cache(final SessionIF session)
   //----------------------------------------------------------------
   {
      super();

      this.setCategory(Category.CACHE);
      this.setDescription("Abstract Class: Cache");
      this.setTimeStamp(SessionIF.TIMESTAMP_CREATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);

      if (session != null)
      {
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL, session.getProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL));
      }

      this.init();

      return;
   }

   /**
    * @param cache
    */
   //----------------------------------------------------------------
   public Cache(final CacheIF cache)
   //----------------------------------------------------------------
   {
      super(cache);

      if (cache != null)
      {
         _value = cache.getValue();

         this.setTimeStamp(SessionIF.TIMESTAMP_CREATED, cache.getCreated());
         this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED, cache.getUpdated());
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED, cache.getAccessed());

         this.setProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL, cache.getProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL));
      }

      this.init();

      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract CacheIF copy();
   //----------------------------------------------------------------

   /**
    * @param value
    */
   //----------------------------------------------------------------
   @Override
   public final void setValue(final Object value)
   //----------------------------------------------------------------
   {
      if (value != null)
      {
         synchronized (this)
         {
            _value = value;
            this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
            this.setState(State.VALID);
         }
      }
      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isExpired()
   //----------------------------------------------------------------
   {
      boolean bool = false;
      Long current = null;

      /*
       * Return TRUE it the cache has been updated or accessed between
       * NOW and either of the two values. The value MUST also be non-null.
       * IF the access or update times are old, set the value to null.
       */

      if (this.getState() == State.VALID)
      {
         current = new Long(System.currentTimeMillis());

         if ((current - this.getUpdated()) < _ttl
            || (current - this.getAccessed()) < _ttl)
         {
            if (_value != null)
            {
               bool = true;
            }
         }
         else
         {
            _value = null;
            this.setState(State.INVALID);
         }
      }

      return bool;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Object getValue()
   //----------------------------------------------------------------
   {
      Object obj = null;

      if (this.isExpired())
      {
         obj = _value;
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
      }

      return obj;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Long getCreated()
   //----------------------------------------------------------------
   {
      return this.getTimeStamp(SessionIF.TIMESTAMP_CREATED);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Long getUpdated()
   //----------------------------------------------------------------
   {
      return this.getTimeStamp(SessionIF.TIMESTAMP_UPDATED);
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final Long getAccessed()
   //----------------------------------------------------------------
   {
      return this.getTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
   }

   /*
    * ***************
    * PRIVATE METHODS
    * ***************
    */
   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      String prop = null;

      prop = this.getProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL);

      if (prop != null && prop.length() > 0)
      {
         try
         {
            _ttl = new Long(prop);
         }
         catch (NumberFormatException ex)
         {
            _ttl = DEFAULT_TTL;
            this.setProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL, Long.toString(DEFAULT_TTL));
            this.setStatus("TTL property is not a number, using default");
         }
      }
      else
      {
         _ttl = DEFAULT_TTL;
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL, Long.toString(DEFAULT_TTL));
         this.setStatus("TTL property is null, using default");
      }

      this.setState(State.VALID);

      return;
   }
}
