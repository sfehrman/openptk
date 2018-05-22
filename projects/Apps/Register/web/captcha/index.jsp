<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Oracle America
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
-->

<!--
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
-->

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="ptk" uri="http://www.openptk.org/taglib" %>

<!-- Setup Defaults -->
<c:set scope="session" var="openptkClientProps" value="openptk_client"/>

<c:choose>
   <c:when test='${param.clientProps != null}'>
      <c:set scope="session" var="openptkClientProps" value="${param.clientProps}"/>
   </c:when>
   <c:when test='${openptkClientProps == null}'>
      <c:set scope="session" var="openptkClientProps" value="${openptkClientProps}"/>
   </c:when>
</c:choose>

<!-- enable debugging -->

<c:choose>
   <c:when test='${showDebug == "off" }'>
      <c:set scope="session" var="showDebug" value="off"/>
   </c:when>
   <c:when test='${param.debug == "on" }'>
      <c:set scope="session" var="showDebug" value="on"/>
   </c:when>
   <c:otherwise>
      <c:set scope="session" var="showDebug" value="off"/>
   </c:otherwise>
</c:choose>

<!-- Create the OpenPTK Client Connection -->

<ptk:getConnection var="registerConn" properties="${openptkClientProps}" scope="session"/>

<html>
   <head>
      <script language="Javascript" type="text/javascript" src="captcha.js"></script>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>Self Service</title>
   </head>
   <link rel="stylesheet" href="captcha.css">

   <body class="body">
      <table class="application" >
         <tr>
            <td>
               <table class="header">
                  <tr>
                     <td>
                        <a href="index.jsp?mode=about">Need help? Click here for instructions</a>
                     </td>
                     <td class="hdr-menu">
                        <a href="index.jsp?mode=welcome">Home</a>&nbsp;|&nbsp;<a href="index.jsp?mode=about">About</a>
                     </td>
                  </tr>
                  <tr>
                     <td colspan="2" class="hdr-banner">
                        End User Self-Service
                        <br>
                        ACCOUNT REQUEST
                     </td>
                  </tr>
               </table>

               <table class="middle">
                  <tr>
                     <td>
                        <c:choose>

                           <c:when test="${param.mode == 'about'}">
                              <%@include file="about.jsp"%>
                           </c:when>

                           <c:when test="${param.mode == 'data'}">
                              <%@include file="data.jsp"%>
                           </c:when>

                           <c:when test="${param.mode == 'create'}">
                              <%@include file="create.jsp"%>
                           </c:when>

                           <c:otherwise>
                              <%@include file="welcome.jsp"%>
                           </c:otherwise>
                        </c:choose>
                     </td>
                  </tr>
               </table>

               <table class="footer">
                  <c:if test="${ptkError == 'true'}">
                     <tr><td class="error">Error: <c:out value="${ptkStatus}"/></td></tr>
                     <c:set var="ptkError" value="false"/>
                  </c:if>
               </table>
            </td>
         </tr>
      </table>
   </body>
</html>
