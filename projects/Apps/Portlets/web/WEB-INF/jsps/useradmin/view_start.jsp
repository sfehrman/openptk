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

<center>
<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr align="left">
      <td><b>Welcome ...</b></td>
   </tr>
   <tr>
      <td>
         <ul>
            <li><b>Create</b>, <b>Update</b> and <b>Delete</b> users</li>
            <li>Perform <i>password management</i> tasks for users</li>
         </ul>
      </td>
   </tr>
</table>
<form action="<portlet:actionURL />" method="post">
   <input type="hidden" name="opcode" value="menu" />
   <table cellpadding="2" cellspacing="2" border="0" width="100%">
      <tr>
         <td align="right">
            <input type="submit" name="userfind" value="Find" />
         </td>
         <td align="left">
            <input type="submit" name="usercreate" value="Create" />
         </td>
      </tr>
   </table>
</form>
</center>

<%@include file="../common/footer.jsp"%>




