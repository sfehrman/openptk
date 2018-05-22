<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
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

<!-- Setup the ContextName -->

<c:choose>
   <c:when test='${param.contextName != null}'>
      <c:set scope="session" var="contextName" value="${param.contextName}"/>
   </c:when>
   <c:when test='${contextName == null}'>
      <c:set scope="session" var="contextName" value="${defaultContextName}"/>
   </c:when>
</c:choose>

<!-- enable debugging -->

<c:choose>
   <c:when test='${param.debug == "on" }'>
      <c:set scope="session" var="showDebug" value="on"/>
   </c:when> 
   <c:when test='${param.debug == "off" }'>
      <c:set scope="session" var="showDebug" value="off"/>
   </c:when>
   <c:when test='${showDebug == "on" }'>
      <c:set scope="session" var="showDebug" value="on"/>
   </c:when>   
   <c:otherwise>  
      <c:set scope="session" var="showDebug" value="off"/>
   </c:otherwise>
</c:choose>

<!-- enable registration validation -->
   <c:if test='${param.regval == "on" }'>
      <c:set scope="session" var="registermode" value="register"/>
   </c:if>
   <c:if test='${param.regval == "off" }'>
      <c:set scope="session" var="registermode" value="register2"/>
   </c:if>
   <c:if test='${registermode == null }'>
      <c:set scope="session" var="registermode" value="register2"/>
   </c:if>

<!--if loginid passed accept as valid uid -->

<c:if test='${param.loginid != null }'>  
   <c:set scope="session" var="uid" value="${param.loginid}"/>
</c:if>     

<c:if test="${param.int != null}">
   <c:if test="${param.int == 'admin'}"> 
      <c:set scope="session" var="bannertext" value="User Management Lite"/>
   </c:if> 
   <c:if test="${param.int == 'user'}">  
      <c:set scope="session" var="bannertext" value="Provisioning Self Service"/>
      <c:if test="${param.inttype == 'enduser'}">  
         <c:set scope="session" var="userIntType" value="enduser"/>
      </c:if>  
   </c:if>  
   <c:if test="${param.inttype == 'password'}">  
      <c:set scope="session" var="bannertext" value="Password Change"/>
      <c:set scope="session" var="userIntType" value="password"/>
   </c:if>
</c:if>             
<c:if test="${param.int == null}">                
   <c:set scope="session" var="bannertext" value="User Management Lite"/>
</c:if>      
