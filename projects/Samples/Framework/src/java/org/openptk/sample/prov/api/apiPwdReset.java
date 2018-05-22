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
package org.openptk.sample.prov.api;

import java.util.Iterator;
import java.util.List;

import org.openptk.api.AttributeIF;
import org.openptk.api.ElementIF;
import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.config.Configuration;
import org.openptk.common.Operation;
import org.openptk.definition.SubjectIF;
import org.openptk.exception.ConfigurationException;

//===================================================================
class apiPwdReset extends apiTest
//===================================================================
{

    private static final String EOL = "\n";

    //----------------------------------------------------------------
    public static void main(String[] args)
    //----------------------------------------------------------------
    {
        apiPwdReset test = new apiPwdReset();
        test.run();
        return;
    }

    //----------------------------------------------------------------
    public void run()
    //----------------------------------------------------------------
    {
        Configuration config = null;
        SubjectIF subject = null;
        Input input = null;
        Output output = null;
        ElementIF result = null;
        AttributeIF attr = null;
        List<ElementIF> list = null;
        String[] names = null;

        try
        {
            config = new Configuration(this.CONFIG, _props);
        }
        catch (ConfigurationException ex)
        {
            System.out.println("new Configuration(): " + ex.getMessage());
            return;
        }

        try
        {
            subject = config.getSubject(this.CONTEXT);
        }
        catch (ConfigurationException ex)
        {
            System.out.println("config.getSubject(): " + ex.getMessage());
            return;
        }

        input = new Input();

        input.setUniqueId("sjohnson");

        try
        {
            output = subject.execute(Operation.PWDRESET, input);
        }
        catch (Exception ex)
        {
            System.out.println("subject.doPasswordReset(): " + ex.getMessage());
            return;
        }

        if (output != null)
        {
            System.out.println("password reset output: " + output.getStatus());
            if (output.getResultsSize() > 0)
            {

                list = output.getResults();
                System.out.println("Resources:  " + list.toString());

                for (Iterator<ElementIF> iter = list.iterator(); iter.hasNext();)
                {
                    result = iter.next();

                    if (result != null)
                    {
                        System.out.print(result.getUniqueId().toString() + ": ");

                        names = result.getAttributeNames();
                        for (int i = 0; i < names.length; i++)
                        {
                            attr = result.getAttribute(names[i]);
                            if (attr != null)
                            {
                                System.out.print(attr.getName() + "=" + attr.getValue() + ", ");
                            }
                        }
                        System.out.print("\n");
                    }
                }
                System.out.print("\n");
            }
            else
            {
                System.out.println("no resources returned.");
            }
        }


        return;
    }
}
