/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2008-2010 Sun Microsystems, Inc.
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
 * Portions Copyright 2011-2012 Project OpenPTK
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
package org.openptk.spi.operations;

import org.openptk.common.ComponentIF;
import org.openptk.common.Operation;
import org.openptk.common.RequestIF;
import org.openptk.common.ResponseIF;
import org.openptk.exception.OperationException;
import org.openptk.spi.ServiceIF;

/**
 *
 * @author Scott Fehrman, Sun Microsystems Inc,
 */
//===================================================================
public interface OperationsIF extends ComponentIF
//===================================================================
{
   public static final String PROP_PROXY_HOST = "proxy.host";
   public static final String PROP_PROXY_PORT = "proxy.port";
   public static final String PROP_USER_NAME = "user.name";
   public static final String PROP_USER_NAME_ENCRYPTED = "user.name.encrypted";
   public static final String PROP_USER_PASSWORD = "user.password";
   public static final String PROP_USER_PASSWORD_ENCRYPTED = "user.password.encrypted";
   public static final String PROP_FILENAME = "filename";
   public static final String PROP_PROTOCOL = "protocol";
   public static final String PROP_DRIVER = "driver";
   public static final String PROP_URL = "url";
   public static final String PROP_RESET_PASSWORD_LENGTH = "reset.password.length";
   public static final String PROP_ATTRIBUTE_EMPTY_REMOVE = "attribute.empty.remove";

   public void shutdown();

   public void startup();

   /**
    * @param req
    * @param res
    * @throws OperationException
    */
   public void execute(RequestIF req, ResponseIF res) throws OperationException;
   
   public void preExecute(RequestIF req, ResponseIF res) throws OperationException;

   public void postExecute(RequestIF req, ResponseIF res) throws OperationException;

   /**
    * @param operation
    * @return boolean
    */
   public boolean isImplemented(Operation operation);

   /**
    * @param operation
    * @param enabled
    */
   public void setEnabled(Operation operation, boolean enabled);

   /**
    * @param operation
    * @return boolean
    */
   public boolean isEnabled(Operation operation);

   /**
    * @param type
    */
   public void setType(OperationsType type);

   /**
    * @return OperationsType
    */
   public OperationsType getType();

   /**
    * @return String
    */
   public String getTypeAsString();

   /**
    * @param service
    */
   public void setService(ServiceIF service);

   /**
    * @return ServiceIF
    */
   public ServiceIF getService();
}
