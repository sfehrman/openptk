/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2009 Sun Microsystems, Inc.
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
 * Portions Copyright 2011 Project OpenPTK, Inc.
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */

package org.openptk.jaxrs;

/**
 *
 * @author Scott Fehrman, Sun Microsystems, Inc.
 * contributor Derrick Harcey, Sun Microsystems, Inc.
 */

//===================================================================
public interface ResourceIF
//===================================================================
{

   public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
   public static final String URI_SPACE = "+";
   public static final String HTTP_HEADER_ACCEPT = "Accept";
   public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
   public static final String HTML_HEAD = "<head>\n" +
      "<title>Project OpenPTK, RESTful Web Service Interface</title>\n" +
      "<meta name=\"author\" content=\"Scott Fehrman, Derrick Harcey, Terry Sigle\" />\n" +
      "<meta name=\"description\" content=\"Project OpenPTK, RESTful Web Service Interface\" />\n" +
      "<meta name=\"keywords\" content=\"openptk,restful,provisioning,html,css\" />\n" +
      "<meta http-equiv=\"Content-Type\" content=\"text/html;charset=ISO-8859-1\" />\n" +
      "<style type=\"text/css\">\n" +
      "table.openptk {\n" +
      "   border-width: 1px 1px 1px 1px;\n" +
      "   border-spacing: 4px;\n" +
      "   border-style: groove groove groove groove;\n" +
      "   border-color: gray gray gray gray;\n" +
      "   border-collapse: separate;\n" +
      "   background-color: white;\n" +
      "}\n" +
      "table.openptk td {\n" +
      "   border-width: 1px 1px 1px 1px;\n" +
      "   padding: 2px 2px 2px 2px;\n" +
      "   border-style: none none none none;\n" +
      "   border-color: gray gray gray gray;\n" +
      "   background-color: white;\n" +
      "}\n" +
      "tr {\n" +
      "   vertical-align: top;\n" +
      "}\n" +
      "td.name {\n" +
      "   color: grey;\n" +
      "   text-align: right;\n" +
      "   font-family: ariel;\n" +
      "   font-style: italic;\n" +
      "}\n" +
      "td.value {\n" +
      "   text-align: left;\n" +
      "}\n" +
      "td.data {\n" +
      "   text-align: left;\n" +
      "   font-family: ariel;\n" +
      "   font-style: normal;\n" +
      "   font-weight: bold;\n" +
      "}\n" +
      "</style>\n" +
      "</head>\n";
}
