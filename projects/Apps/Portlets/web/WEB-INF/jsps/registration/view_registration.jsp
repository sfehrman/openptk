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

<c:set var="resubmit">
   <%=renderRequest.getPortletSession().getAttribute("resubmit")%>
</c:set>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><b>User registration</b></td>
   </tr>
</table>

<c:set var="msg"><%=renderRequest.getAttribute("message")%></c:set>
<c:set var="err"><%=renderRequest.getAttribute("status")%></c:set>

   <c:if  test='${not empty err}'>
      <hr>
      <font color="red"><c:out value="${msg}"/></font>
      <hr>
   </c:if>

<center>
   <form action="<portlet:actionURL />" method="post">
      <input type="hidden" name="opcode" value="registrationresults"/>
      <table cellpadding="2" cellspacing="2" border="0">
           <tr valign="bottom">
               <td align="right">
                  Email Address:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="text" name='email' size="24" align="left" value="<c:out value='${email}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="text" name='email' size="24" align="left"/>
                  </c:if>
               </td>
            </tr>
           <!--tr valign="bottom">
               <td align="right">
                  Login ID:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="text" name='username' size="24" align="left" value="<c:out value='${uniqueid}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="text" name='username' size="24" align="left"/>
                  </c:if>
               </td>
            </tr-->
           <tr valign="bottom">
               <td align="right" colspan="2">
                  <i>your email address will be used as your login id.</i>
            </tr>
         <tr valign="bottom">
               <td align="right">
                  First Name:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="text" name='firstname' size="24" align="left" value="<c:out value='${firstname}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="text" name='firstname' size="24" align="left"/>
                  </c:if>
               </td>
            </tr>
         <!--tr valign="bottom">
               <td align="right">
                  Middle Initial:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="text" name='middlename' size="24" align="left" value="<c:out value='${middlename}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="text" name='middlename' size="24" align="left"/>
                  </c:if>
               </td>
            </tr-->
           <tr valign="bottom">
               <td align="right">
                  Last Name:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="text" name='lastname' size="24" align="left" value="<c:out value='${lastname}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="text" name='lastname' size="24" align="left"/>
                  </c:if>
               </td>
            </tr>
           <tr valign="bottom">
               <td align="right">
                  Password:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="password" name='password' size="24" align="left" value="<c:out value='${password}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="password" name='password' size="24" align="left"/>
                  </c:if>
               </td>
            </tr>
           <tr valign="bottom">
               <td align="right">
                  Confirm Password:&nbsp;
               </td>
               <td align="left">
                  <c:if  test='${resubmit == "true"}'>
                     <input type="password" name='passwordconf' size="24" align="left" value="<c:out value='${password}'/>"/>
                  </c:if>
                  <c:if  test='${resubmit != "true"}'>
                     <input type="password" name='passwordconf' size="24" align="left"/>
                  </c:if>
               </td>
            </tr>            
            <!--tr class="middle">
                  <td colspan="2">
                     <b>Forgotten Password challenge questions:</b>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">What is your favorite color?:</td>
                  <td class="data">
                     <input type="text" size="18" id="answer1" align="left" name="answer1" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">What is your mother's maiden name?:</td>
                  <td class="data">
                     <input type="text" size="18" id="answer2" align="left" name="answer2" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">What is the city of your birth?:</td>
                  <td class="data">
                     <input type="text" size="18" id="answer3" align="left" name="answer3" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td colspan="2">
                     <hr>
                  </td>
               </tr-->
               <tr class="middle">
                  <td colspan="2">
                     <b>Terms and Conditions:</b>
                  </td>
               </tr>
               <tr align="center" class="middle">
                  <td colspan="2">
                     <table class="border">
                        <tr><td>
                              <div class="scrollbox">
                                 <%@include file="../common/terms.html"%>
                              </div>
                           </td></tr>
                     </table>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="data" colspan="2">
                     <input type="checkbox" name="acceptterms" />I agree to these Terms and Conditions
                  </td>
               </tr>
            </table>

      <table cellpadding="2" cellspacing="2" border="0">
         <tr>
            <td align="center">
               <input type="submit" name="register2" value="Register"/>
            </td>
            <td align="center">
               <input type="submit" name="cancel" value="Cancel"/>
            </td>
         </tr>
      </table>

   </form>
</center>

<%@include file="../common/footer.jsp"%>