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

<c:set var="userlist">
   <%=renderRequest.getPortletSession().getAttribute("userlistelements")%>
</c:set>

<c:set var="usersize">
   <%=renderRequest.getPortletSession().getAttribute("userlistsize")%>
</c:set>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><b>Found:&nbsp;<c:out value="${usersize}"/></b></td>
   </tr>
</table>

<center>
   <table cellpadding="2" cellspacing="2" border="0" width="100%">
      <tr>
         <td></td>
         <td><i>Last,&nbsp;First:</i></td>
         <td><i>Email:</i></td>
      </tr>
      <tr><td colspan="3"><hr></td></tr>

      <c:forEach items='<%=renderRequest.getPortletSession().getAttribute("userlistelements")%>' var="person">
         <c:set var="detailname"><ptk:getValue name="${per_attr_lastname}" result="person"/>, <ptk:getValue name="${per_attr_firstname}" result="person"/></c:set>
         <tr>
            <td>
               <form action="<portlet:actionURL />" method="post">
                  <input type="hidden" name="opcode" value="userdetail"/>
                  <input type="hidden" name="uniqueid" value="<ptk:getUniqueId result='person'/>"/>
                  <input type="submit" name="useredit" value="select"/>
               </form>
            </td>
            <td>
               <font size="-1"><c:out value='${detailname}'/></font>
            </td>
            <td>
               <font size="-1"><ptk:getValue name="${per_attr_email}" result="person"/></font>
            </td>
         </tr>
      </c:forEach>

   </table>

   <form action="<portlet:actionURL />" method="post">
      <input type="hidden" name="opcode" value="listusers" />
      <table cellpadding="2" cellspacing="2" border="0">
         <tr>
            <td align="center"><input type="submit" value="Cancel" /></td>
            <td align="center"><input type="submit" name="usercreate" value="Create" /></td>
         </tr>
      </table>
   </form>

</center>

<%@include file="../common/footer.jsp"%>
