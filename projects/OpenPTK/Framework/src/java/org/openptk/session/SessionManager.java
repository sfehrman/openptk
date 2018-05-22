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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public abstract class SessionManager implements SessionManagerIF
//===================================================================
{
   protected final ConcurrentMap<String, SessionIF> _sessions = new ConcurrentHashMap<String, SessionIF>();

   //----------------------------------------------------------------
   public SessionManager()
   //----------------------------------------------------------------
   {
      return;
   }

   //----------------------------------------------------------------
   @Override
   public abstract void startup();
   //----------------------------------------------------------------


   //----------------------------------------------------------------
   @Override
   public abstract void shutdown();
   //----------------------------------------------------------------



   /**
    * @param id
    * @param session
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void set(String id, SessionIF session)
   //----------------------------------------------------------------
   {
      if (id != null && id.length() > 0 && session != null)
      {
         session.setTimeStamp(SessionIF.TIMESTAMP_UPDATED);
         _sessions.put(id, session);
      }

      return;
   }


   /**
    * Returns the Session related to the id.
    * Note: This is a copy of the stored Session.  If changes are made, the
    * updated Session will need to be put back with set()
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public SessionIF get(String id)
   //----------------------------------------------------------------
   {
      SessionIF session = null;

      if (id != null && id.length() > 0 && _sessions.containsKey(id))
      {
         session = _sessions.get(id);
         if (session != null)
         {
            session.setTimeStamp(SessionIF.TIMESTAMP_ACCESSED);
            /*
             * Make and Return a copy
             */
            session = session.copy();
         }
      }

      return session;
   }


   /**
    * @param id
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public boolean contains(String id)
   //----------------------------------------------------------------
   {
      boolean found = false;

      if (id != null && id.length() > 0)
      {
         found = _sessions.containsKey(id);
      }

      return found;
   }


   /**
    * @param id
    */
   //----------------------------------------------------------------
   @Override
   public synchronized void remove(String id)
   //----------------------------------------------------------------
   {
      SessionIF session = null;

      if (id != null && id.length() > 0)
      {
         session = _sessions.get(id);
         if (session != null)
         {
            session.destroy();
            session = null;
            _sessions.remove(id);
         }
      }

      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   @Override
   public String[] getIds()
   //----------------------------------------------------------------
   {
      String[] ids = null;

      ids = _sessions.keySet().toArray(new String[_sessions.size()]);

      return ids;
   }
}
