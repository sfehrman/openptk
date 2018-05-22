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
<%@include file="../common/initvars.jsp" %>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><b>User Detail:</b></td>
   </tr>
</table>

<form action="<portlet:actionURL />" method="post">
   <input type="hidden" name="opcode" value="userupdate" />
   <input type="hidden" name="<c:out value='${per_attr_uniqueid}'/>" value="<c:out value='${uniqueid}'/>" />
   <center>
      <table cellpadding="2" cellspacing="2" border="0" width="100%">
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>Account Id:</i></font></td>
            <td align="left"><c:out value='${uniqueid}'/></td>
         </tr>
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>First:</i></font></td>
            <td align="left"><c:out value='${firstname}'/></td>
         </tr>
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>Last:</i></font></td>
            <td align="left"><c:out value='${lastname}'/></td>
         </tr>
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>Title:</i></font></td>
            <td align="left">
               <input type="text" size="24" align="left"
                      name="<c:out value='${per_attr_title}'/>"
                      value="<c:out value='${title}'/>"/>
            </td>
         </tr>
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>Email:</i></font></td>
            <td align="left">
               <input type="text" size="24" align="left"
                      name="<c:out value='${per_attr_email}'/>"
                      value="<c:out value='${email}'/>"/>
            </td>
         </tr>
         <tr valign="bottom">
            <td align="right"><font size="-1"><i>Telephone:</i></font></td>
            <td align="left">
               <input type="text" size="24" align="left"
                      name="<c:out value='${per_attr_telephone}'/>"
                      value="<c:out value='${telephone}'/>"/>
            </td>
         </tr>
      </table>
      <table cellpadding="2" cellspacing="2" border="0">
         <tr>
            <td align="center">
               <input type="submit" name="changeinfo" value="Update" onclick="return(window.confirm('Are you sure, UPDATE this user record?'));"/>
            </td>
            <td align="center">
               <input type="submit" name="delete" value="Delete" onclick="return(window.confirm('Are you sure, DELETE this user record?'));"/>
            </td>
            <td align="center">
               <input type="submit" name="resetpwd" value="Reset Password" onclick="return(window.confirm('Are you sure, RESET password for this user?'));"/>
            </td>
            <td align="center">
               &nbsp;&nbsp;&nbsp;&nbsp;
            </td>
            <td align="center">
               <input type="submit" name="cancel" value="Cancel" />
            </td>
         </tr>
      </table>
   </center>
</form>

<%@include file="../common/footer.jsp"%>