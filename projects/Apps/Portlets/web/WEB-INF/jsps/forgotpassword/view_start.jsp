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
<%@include file="../common/initvars.jsp"%>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><b>Forgot your password</b></td>
   </tr>
</table>

<form action="<portlet:actionURL />" method="post">
   <input type="hidden" name="opcode" value="forgotpwd"/>
   <table cellpadding="2" cellspacing="2" border="0" width="100%">
      <tr>
         <td align="right"><i>Account Id:</i></td>
         <td align="left">
            <input type="text" size="12" align="left" name='<c:out value="${per_attr_uniqueid}"/>'/>
         </td>
         <td align="left">
            <input type="submit" name="forgotpwd" value="Forgot Password"/>
         </td>
      </tr>
   </table>
</form>

<%@include file="../common/footer.jsp"%>