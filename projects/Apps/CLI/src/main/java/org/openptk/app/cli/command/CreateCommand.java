/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2008 Sun Microsystems, Inc.
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

package org.openptk.app.cli.command;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.api.Opcode;
import org.openptk.exception.AuthenticationException;
import org.openptk.exception.ConnectionException;

/**
 *
 * @author Tery Sigle
 */
public class CreateCommand extends Command implements CommandIF
{

   private String _uniqueId = null;                    // -u    Unique ID
   private Map<String, String> _setAttributes = null;  // -a    lastname=smith,firstname=john

   /**
    * @throws CommandException
    */
   public void execute() throws CommandException
   {
      Input input = null;
      Output output = null;

      input = new Input();

      input.setDescription("ptkcli create"); // optional

      if (_uniqueId != null && _uniqueId.length() > 0)
      {
         input.setUniqueId(_uniqueId);
      }

      // Add the setAttributes from the -a key1=val1,key2=val2...
      this.setAttributesOnInput(input, _setAttributes);

      try
      {
         output = this.execute(Opcode.CREATE, input);
      }
      catch (AuthenticationException ex)
      {
         handleError(ex.getMessage());
      }
      catch (ConnectionException ex)
      {
         handleError("Create Problem : " + ex.getMessage());
      }

      if (output != null)
      {
         switch (output.getState()) {
            case SUCCESS:
               _screen.println(quote(output.getUniqueId().toString()) + " successfully created.");
               break;
            case FAILED:
               if (output.getUniqueId() != null) {
                  _screen.println("Failed: " + quote(output.getUniqueId().toString()) + " already exists.");
               }
               else {
                  _screen.println("Failed: Record already exists.");
               }
               break;
            case DENIED:
               _screen.println("Failed: CREATE not allowed.");
               break;
            case ERROR:
               _screen.println("Failed: CONTEXT '" + this.getEnv().getContextId() + "' not available.");
               break;
            default:
               handleError("Create Problem" + " (" + output.getState() + ")");
         }
      }

      showTiming();

      return;
   }

   /**
    * @param args
    * @throws CommandException
    * @throws NoSuchElementException
    */
   @Override
   public void parseArgs(List<String> args) throws CommandException, NoSuchElementException
   {
      Iterator <String>iter = null;
      String arg = null;

      iter = args.iterator();

      iter.next(); // Pop off the command

      // Iterate through each of the command arguments
      while (iter.hasNext())
      {
         arg = iter.next();

         if ("-a".equals(arg))
         {
            _setAttributes = this.parseSetAttributes(iter.next());
         } 
         else if ("-u".equals(arg))
         {
            _uniqueId = this.parseId(iter.next());
         }
         else
         {
            handleError("Unknown CREATE argument '" + arg + "'");
         }

      }

      if (_setAttributes == null)
      {
         handleError("No Attributes are being set on Create.  Please use -a option to set attributes.");
      }

      return;
   }
}
