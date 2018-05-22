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
package org.openptk.engine;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 */
//===================================================================
public class Stats
//===================================================================
{

   private int count = 0;
   private int size = 100;
   private int used = 0;
   private int ptr = 0;
   private long[] data;

   //----------------------------------------------------------------
   public Stats()
   //----------------------------------------------------------------
   {
      data = new long[size];
      for (int i = 0; i < size; i++)
      {
         data[i] = 0;
      }
   }


   /**
    * @param value
    */
   //----------------------------------------------------------------
   public void add(long value)
   //----------------------------------------------------------------
   {

      synchronized (this)
      {
         if (this.ptr >= 100)
         {
            this.ptr = 0;
         }
         this.data[ptr] = value;
         ptr++;

         if (used < size)
         {
            used++;
         }
         count++;
      }
      return;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public int count()
   //----------------------------------------------------------------
   {
      return this.count;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public long average()
   //----------------------------------------------------------------
   {
      long total = 0;
      long avg = 0;

      synchronized (this)
      {
         for (int i = 0; i < this.used; i++)
         {
            total += this.data[i];
         }
         if (this.used > 0)
         {
            avg = total / this.used;
         }
      }

      return avg;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public long min()
   //----------------------------------------------------------------
   {
      long min = this.data[0];

      synchronized (this)
      {
         for (int i = 0; i < this.used; i++)
         {
            if (this.data[i] < min)
            {
               min = this.data[i];
            }
         }
      }

      return min;
   }


   /**
    * @return
    */
   //----------------------------------------------------------------
   public long max()
   //----------------------------------------------------------------
   {
      long max = this.data[0];

      synchronized (this)
      {
         for (int i = 0; i < this.used; i++)
         {
            if (this.data[i] > max)
            {
               max = this.data[i];
            }
         }
      }

      return max;
   }
}
