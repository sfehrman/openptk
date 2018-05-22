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

<%@include file="../common/header.jsp"%>
<%@include file="../common/initvars.jsp" %>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td>
         <b>Password reset for: <c:out value='${uniqueid}'/></b>
      </td>
   </tr>
</table>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><font size="-1"><i>Resource:</i></font></td>
      <td><font size="-1"><i>Password:</i></font></td>
   </tr>
   
   <c:forEach items='<%=renderRequest.getPortletSession().getAttribute("pwdlistelements")%>' var="resource">
      <tr>
         <td><font size="-1"><ptk:getUniqueId result="resource"/></font></td>
         <td><font size="-1"><ptk:getValue result="resource" name="${per_attr_password}"/></font></td>
      </tr>        
   </c:forEach>
   
</table>
<form action="<portlet:actionURL />" method="post">
   <input type="hidden" name="opcode" value="start" />
   <input type="submit" value="Continue" />
</form>


<%@include file="../common/footer.jsp"%>
