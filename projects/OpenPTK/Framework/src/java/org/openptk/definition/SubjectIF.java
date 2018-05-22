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
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.definition;

import java.util.List;

import org.openptk.api.Input;
import org.openptk.api.Output;
import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.config.Configuration;
import org.openptk.exception.ConfigurationException;
import org.openptk.exception.ProvisionException;

/**
 * The SubjectIF interface defines the provisioning operation execute.
 * Instances of this Interface are not directly instanciated.
 * Instances are created through a Configuration object via the getSubject() method.
 *
 * <pre>
 * Configuration config = new Configuration("openptk.xml");
 * SubjectIF subject = config.getSubject();
 * </pre>
 * The <tt>Configuration.getSubject()</tt> method creates a
 * <b>Subject</b> which implements the <b>execute</b> method.
 */
//
//===================================================================
public interface SubjectIF extends ComponentIF
//===================================================================
{

    public static final String PROP_UPDATE_ATTR_REMOVE = "update.attr.remove";

    /**
     * Performs one-time initialization.
     *
     * <ul>
     * <li>
     * <b>WARNING:</b> This method should <u>NOT</u> be directly invoked.
     * It is automatically invoked by the Configuration classes
     * getSubject() method.
     * </li>
     * </ul>
     *
     * @param config A Configuration object
     * @param contextName The name of a context
     * @throws ConfigurationException
     */
    public void initialize(Configuration config, String contextName) throws ConfigurationException;

    /**
    *
    * @param oper A Operation enumeration
    * @param input A Input object containing data needed to perform the operation
    * @return output A Output object with the results of the operation
    * @throws ProvisionException
    * @throws ConfigurationException
    */
    public Output execute(Operation oper, Input input) throws ProvisionException, ConfigurationException;

    /**
     * Gets the Configuration instance that created the Subject.
     * @return Configuration
     */
    public Configuration getConfiguration();

    /**
     * Gets a List which contains the available Attribute names for the Operation.
     * @param oper A Operation enumeration
     * @return list A List of Strings which is the attribute names
     */
    public List<String> getAvailableAttributes(Operation oper);

    /**
     * Gets a List which contains the required Attribute names for the Operation.
     * @param oper A Operation enumeration
     * @return list A List of Strings which is the attribute names
     */
    public List<String> getRequiredAttributes(Operation oper);
}
