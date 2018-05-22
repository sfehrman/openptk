<%
/*
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
 */

/*
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 */
%>

<%@include file="../common/header.jsp" %>

<c:set var="uniqueid">
   <%=renderRequest.getPortletSession().getAttribute("username")%>
</c:set>
<c:set var="firstname">
   <%=renderRequest.getPortletSession().getAttribute("firstname")%>
</c:set>
<c:set var="lastname">
   <%=renderRequest.getPortletSession().getAttribute("lastname")%>
</c:set>
<!--c:set var="middlename"-->
   <!--%=renderRequest.getPortletSession().getAttribute("middlename")%-->
<!--/c:set-->
<c:set var="email">
   <%=renderRequest.getPortletSession().getAttribute("email")%>
</c:set>
<c:set var="password">
   <%=renderRequest.getPortletSession().getAttribute("password")%>
</c:set>

<c:set var="acceptterms">
   <%=renderRequest.getPortletSession().getAttribute("acceptterms")%>
</c:set>


<c:set scope="session" var="openptkClientProps" value="openptk_client_register"/>

<!-- Create the OpenPTK Client Connection -->

<ptk:getConnection var="registerConn" properties="${openptkClientProps}" scope="session"/>

      <ptk:setInput var="myinput"/>
      <!--ptk:setUniqueId input="myinput" value="${uniqueid}"/-->
      
      <ptk:setAttribute input="myinput" key="firstname" value="${firstname}"/>
      <!--ptk:setAttribute input="myinput" key="middlename" value="${middlename}"/-->
      <ptk:setAttribute input="myinput" key="lastname" value="${lastname}"/>
      <ptk:setAttribute input="myinput" key="email" value="${email}"/>
      <ptk:setAttribute input="myinput" key="password" value="${password}"/>



      <ptk:doCreate connection="registerConn" input="myinput" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">

         <table cellpadding="2" cellspacing="2" border="0" width="100%">
            <tr>
               <td><b>Your request has been processed</b></td>
            </tr>
         </table>
         <center>
            <br>
            <br>Thank you for registering, <c:out value="${firstname}"/> <c:out value="${lastname}"/>,
            <br>your login will be: <c:out value="${email}"/>
            <br>
            <br>please check your email for additional access instructions.
            <br>
         </center>
      </c:if>

<c:if test="${ptkError == 'true'}">


   <b>Your request could not be processed</b>
   <br><br>
   Error: <c:out value="${ptkStatus}"/>
   <c:set var="ptkError" value="false"/>
</c:if>

<br>

<center>
   <form action="<portlet:actionURL />"  method="post">
      <input type="hidden" name="opcode" value="start" />
      <table cellpadding="2" cellspacing="2" border="0" width="100%">
         <tr>
            <td align="center">
               <input type="submit" value="Continue" />
            </td>
         </tr>
      </table>
   </form>
</center>

<ptk:closeConnection connection="registerConn"/>

<%@include file="../common/footer.jsp"%>
