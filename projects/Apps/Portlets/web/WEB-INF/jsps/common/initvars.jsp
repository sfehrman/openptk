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

<c:set var="uniqueid">
   <%=renderRequest.getPortletSession().getAttribute("uniqueid")%>
</c:set>
<c:set var="lastname">
   <%=renderRequest.getPortletSession().getAttribute("lastname")%>
</c:set>
<c:set var="firstname">
   <%=renderRequest.getPortletSession().getAttribute("firstname")%>
</c:set>
<c:set var="title">
   <%=renderRequest.getPortletSession().getAttribute("title")%>
</c:set>
<c:set var="email">
   <%=renderRequest.getPortletSession().getAttribute("email")%>
</c:set>
<c:set var="telephone">
   <%=renderRequest.getPortletSession().getAttribute("telephone")%>
</c:set>

<c:set var="per_attr_uniqueid"      value="uniqueid"/>
<c:set var="per_attr_firstname"     value="firstname"/>
<c:set var="per_attr_lastname"      value="lastname"/>
<c:set var="per_attr_title"         value="title"/>
<c:set var="per_attr_fullname"      value="fullname"/>
<c:set var="per_attr_email"         value="email"/>
<c:set var="per_attr_password"      value="password"/>
<c:set var="per_attr_epassword"     value="epassword"/>
<c:set var="per_attr_telephone"     value="telephone"/>
<c:set var="per_attr_organization"  value="organization"/>
<c:set var="per_attr_manager"       value="manager"/>
<c:set var="per_attr_forgotpwd"     value="forgotpwd"/>