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
      <td><b>Your challenge questions/answers have been validated</b></td>
   </tr>
</table>

<center>
   <form action="<portlet:actionURL />" method="post">
      <input type="hidden" name="opcode" value="changepwd" />
      <input type="hidden" name='<c:out value="${per_attr_uniqueid}"/>' value='<c:out value="${uniqueid}"/>'/>
      <table cellpadding="2" cellspacing="2" border="0" width="100%">
         <tr>
            <td align="right">Account Id:&nbsp;</td>
            <td align="left">
               <c:out value="${uniqueid}"/>&nbsp;<i>(<c:out value="${firstname}"/>&nbsp;<c:out value="${lastname}"/>)</i>
            </td>
         </tr>
         <tr>
            <td align="right"><i>New Password:</i></td>
            <td align="left">
               <input type="password" name="newpwd" value="" />
            </td>
         </tr>
         <tr>
            <td align="right"><i>Confirm Password:</i></td>
            <td align="left">
               <input type="password" name="confirmpwd" value="" />
            </td>
         </tr>
      </table>
      <table cellpadding="2" cellspacing="2" border="0">
         <tr>
            <td align="center">
               <input type="submit" name="change" value="Change Password" />
            </td>
            <td align="center">
               <input type="submit" name="cancel" value="Cancel" />
            </td>
         </tr>
      </table>
   </form>
</center>

<%@include file="../common/footer.jsp"%>