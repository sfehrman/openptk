/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2012-2013 Project OpenPTK
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
package org.openptk.common;

/**
 *
 * @author Scott Fehrman
 * @since 2.2.0
 */
//===================================================================
public abstract class Assignment extends Component implements AssignmentIF
//===================================================================
{

   private ComponentIF _source = null;
   private ComponentIF _destination = null;

   //----------------------------------------------------------------
   public Assignment(ComponentIF source, ComponentIF destination)
   //----------------------------------------------------------------
   {
      super();
      
      this.setCategory(Category.ASSIGNMENT);
      
      if (source != null)
      {
         _source = source.copy();
      }

      if (destination != null)
      {
         _destination = destination.copy();
      }

      return;
   }

   //----------------------------------------------------------------
   public Assignment(AssignmentIF assignment)
   //----------------------------------------------------------------
   {
      super(assignment);
      
      this.setCategory(Category.ASSIGNMENT);

      if (assignment != null)
      {
         _source = assignment.getSource();
         _destination = assignment.getDestination();
      }

      return;
   }

   //----------------------------------------------------------------
   @Override
   public abstract AssignmentIF copy();
   //----------------------------------------------------------------

   //----------------------------------------------------------------
   @Override
   public final synchronized ComponentIF getSource()
   //----------------------------------------------------------------
   {
      return (_source != null ? _source.copy() : null);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized ComponentIF getDestination()
   //----------------------------------------------------------------
   {
      return (_destination != null ? _destination.copy() : null);
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setSource(ComponentIF component)
   //----------------------------------------------------------------
   {
      if (component != null)
      {
         _source = component.copy();
      }
      return;
   }

   //----------------------------------------------------------------
   @Override
   public final synchronized void setDestination(ComponentIF component)
   //----------------------------------------------------------------
   {
      if (component != null)
      {
         _destination = component.copy();
      }
      return;
   }
}
