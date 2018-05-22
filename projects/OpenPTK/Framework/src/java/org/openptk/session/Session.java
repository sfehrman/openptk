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

import java.util.HashMap;
import java.util.Map;

import org.openptk.api.State;
import org.openptk.authenticate.PrincipalIF;
import org.openptk.common.Category;
import org.openptk.common.Component;
import org.openptk.engine.EngineIF;

/**
 *  @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class Session extends Component implements SessionIF
//===================================================================
{
   private final Object _lockCaches = new Object();
   private String _clientId = null;
   private Long _ttl = null;
   private Long _kill = null;
   private Map<String, CacheIF> _caches = new HashMap<String, CacheIF>();
   private SessionType _type = SessionType.INTERNAL;
   private PrincipalIF _principal = null;

   /**
    * @param engine
    * @param type
    * @param id
    * @param principal
    */
   //----------------------------------------------------------------
   public Session(final EngineIF engine, final SessionType type, final String id, final PrincipalIF principal)
   //----------------------------------------------------------------
   {
      super();

      if (type != null)
      {
         _type = type;
      }

      if (id != null && id.length() > 0)
      {
         this.setUniqueId(id);
      }

      if (principal != null)
      {
         _principal = principal;
      }

      this.setCategory(Category.SESSION);
      this.setDescription("Abstract Class: Session");
      this.setTimeStamp(SessionIF.TIMESTAMP_CREATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);

      /*
       * initialize values, using Engine properties
       */

      this.init(engine);

      return;
   }

   /**
    * @param engine
    * @param type
    * @param id
    */
   //----------------------------------------------------------------
   public Session(final EngineIF engine, final SessionType type, final String id)
   //----------------------------------------------------------------
   {
      super();

      if (type != null)
      {
         _type = type;
      }

      if (id != null && id.length() > 0)
      {
         this.setUniqueId(id);
      }

      this.setCategory(Category.SESSION);
      this.setDescription("Abstract Class: Session");
      this.setTimeStamp(SessionIF.TIMESTAMP_CREATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
      this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);

      /*
       * initialize values, using Engine properties
       */

      this.init(engine);

      return;
   }

   /**
    * @param session
    */
   //----------------------------------------------------------------
   public Session(final SessionIF session)
   //----------------------------------------------------------------
   {
      super(session);

      String[] cacheIds = null;
      CacheIF cache = null;

      if (session != null)
      {
         _type = session.getType();
         _principal = session.getPrincipal();
         _clientId = session.getClientId();

         this.setTimeStamp(SessionIF.TIMESTAMP_CREATED, session.getCreated());
         this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED, session.getUpdated());
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED, session.getAccessed());

         /*
          * Copy the cache(s)
          */

         cacheIds = session.getCacheIds();

         if (cacheIds != null && cacheIds.length > 0)
         {
            for (String cacheId : cacheIds)
            {
               if (cacheId != null && cacheId.length() > 0)
               {
                  cache = session.getCache(cacheId);
                  if (cache != null)
                  {
                     _caches.put(cacheId, cache);
                  }
               }
            }
         }
      }

      this.init();

      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public abstract SessionIF copy();
   //----------------------------------------------------------------

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final boolean isExpired()
   //----------------------------------------------------------------
   {
      boolean isExpired = true;
      Long current = null;

      if (this.getState() == State.VALID)
      {
         current = new Long(System.currentTimeMillis());

         /*
          * If the session has not been updated or accessed within it's
          * configured "Time To Live" value ... mark it as INVALID
          */

         if ((current - this.getUpdated()) < _ttl || (current - this.getAccessed()) < _ttl)
         {
            /*
             * Any session can not live longer (regardless of activity) then the
             * configured "Kill" value ... mark it as INVALID
             * This is "disabled" if the "kill" value is equal to "zero" (0)
             */
            if (_kill == 0 || (current - this.getCreated()) < _kill)
            {
               isExpired = false;
            }
            else
            {
               this.setState(State.INVALID);
            }
         }
         else
         {
            this.setState(State.INVALID);
         }
      }

      return isExpired;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final SessionType getType()
   //----------------------------------------------------------------
   {
      this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
      return _type;
   }

   /**
    * @param cacheId
    * @param cache
    */
   //----------------------------------------------------------------
   @Override
   public final void setCache(final String cacheId, final CacheIF cache)
   //----------------------------------------------------------------
   {
      if (cacheId != null && cacheId.length() > 0 && cache != null)
      {
         if (this.getState() == State.VALID)
         {
            synchronized (_lockCaches)
            {
               _caches.put(cacheId, cache);
               this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
            }
         }
      }

      return;
   }

   /**
    * @param cacheId
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final CacheIF getCache(final String cacheId)
   //----------------------------------------------------------------
   {
      CacheIF cache = null;

      if (cacheId != null && cacheId.length() > 0)
      {
         if (this.getState() == State.VALID)
         {
            if (_caches.containsKey(cacheId))
            {
               cache = _caches.get(cacheId);
               /*
                * Return a copy
                */
               if (cache != null)
               {
                  cache = cache.copy();
               }
               this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
            }
         }
      }

      return cache;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String[] getCacheIds()
   //----------------------------------------------------------------
   {
      String[] ids = null;

      if (this.getState() == State.VALID && !_caches.isEmpty())
      {
         ids = _caches.keySet().toArray(new String[_caches.size()]);
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
      }

      return ids;
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

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final PrincipalIF getPrincipal()
   //----------------------------------------------------------------
   {
      PrincipalIF princ = null;

      if (_principal != null)
      {
         princ = _principal.copy();
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
      }

      return princ;
   }

   /**
    * @param clientId
    */
   //----------------------------------------------------------------
   @Override
   public final void setClientId(final String clientId)
   //----------------------------------------------------------------
   {
      synchronized (_lockCaches)
      {
         _clientId = clientId;
         this.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
      }

      return;
   }

   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public final String getClientId()
   //----------------------------------------------------------------
   {
      String str = null;

      if (_clientId != null && _clientId.length() > 0)
      {
         str = new String(_clientId); // always return a copy
         this.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
      }

      return str;
   }

   //----------------------------------------------------------------
   @Override
   public final void destroy()
   //----------------------------------------------------------------
   {
      String[] ids = null;

      ids = this.getCacheIds();

      if (ids != null && ids.length > 0)
      {
         synchronized (_lockCaches)
         {
            for (String id : ids)
            {
               _caches.remove(id);
            }
            _caches = null;
         }
      }

      this.setState(State.DESTROYED);

      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized String toString()
   //----------------------------------------------------------------
   {
      StringBuilder buf = new StringBuilder();

      buf.append(super.toString());
      buf.append(", clientId='").append((_clientId != null) ? _clientId : "(null)").append("'");

      return buf.toString();
   }



   /*
    * ***************
    * PRIVATE METHODS
    * ***************
    */
   //----------------------------------------------------------------
   private void init(final EngineIF engine)
   //----------------------------------------------------------------
   {
      if (engine != null)
      {
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_TTL, engine.getProperty(EngineIF.PROP_ENGINE_SESSION_TTL));
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_KILL, engine.getProperty(EngineIF.PROP_ENGINE_SESSION_KILL));
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL, engine.getProperty(EngineIF.PROP_ENGINE_SESSION_CACHE_TTL));
      }

      this.init();

      return;
   }

   //----------------------------------------------------------------
   private void init()
   //----------------------------------------------------------------
   {
      String prop = null;

      /*
       * Property: Time To Live: Sessions not used longer then this value are removed
       */

      prop = null;
      prop = this.getProperty(EngineIF.PROP_ENGINE_SESSION_TTL);
      if (prop != null && prop.length() > 0)
      {
         try
         {
            _ttl = new Long(prop);
         }
         catch (NumberFormatException ex)
         {
            _ttl = DEFAULT_TTL;
            this.setProperty(EngineIF.PROP_ENGINE_SESSION_TTL, Long.toString(DEFAULT_TTL));
            this.setStatus("TTL property is not a number, using default");
         }
      }
      else
      {
         _ttl = DEFAULT_TTL;
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_TTL, Long.toString(DEFAULT_TTL));
         this.setStatus("TTL property is null, using default");
      }

      /*
       * Property: KILL: Sessions "older" than this value are removed, regardless of activity
       */

      prop = null;
      prop = this.getProperty(EngineIF.PROP_ENGINE_SESSION_KILL);
      if (prop != null && prop.length() > 0)
      {
         try
         {
            _kill = new Long(prop);
         }
         catch (NumberFormatException ex)
         {
            _kill = DEFAULT_KILL;
            this.setProperty(EngineIF.PROP_ENGINE_SESSION_KILL, Long.toString(DEFAULT_KILL));
            this.setStatus("KILL property is not a number, using default");
         }
      }
      else
      {
         _kill = DEFAULT_KILL;
         this.setProperty(EngineIF.PROP_ENGINE_SESSION_KILL, Long.toString(DEFAULT_KILL));
         this.setStatus("KILL property is null, using default");
      }

      this.setState(State.VALID);

      return;
   }
}
