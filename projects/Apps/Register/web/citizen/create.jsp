<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2011 Oracle America
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

<%@include file="../common/conn_get.jsp"%>

<form name="create" action="">
   <input type=hidden name="mode" value="welcome"/>
   <table class="content">
      <tr>
         <td>
            <p>
               Request for <b>${param.fname}&nbsp;${param.lname}</b> has been submitted:
            </p>

      <ptk:setInput var="myinput"/>
      
      <ptk:setAttribute input="myinput" key="firstname" value="${param.fname}"/>
      <ptk:setAttribute input="myinput" key="lastname" value="${param.lname}"/>
      <ptk:setAttribute input="myinput" key="middlename" value="${param.mname}"/>
      <ptk:setAttribute input="myinput" key="gender" value="${param.gender}"/>
      <ptk:setAttribute input="myinput" key="dob" value="${param.dob}"/>
      <ptk:setAttribute input="myinput" key="last4ssn" value="${param.last4ssn}"/>
      <ptk:setAttribute input="myinput" key="driverlic" value="${param.dln}"/>
      <ptk:setAttribute input="myinput" key="homephone" value="${param.hphone}"/>
      <ptk:setAttribute input="myinput" key="homeaddress1" value="${param.haddr1}"/>
      <ptk:setAttribute input="myinput" key="homecity" value="${param.hcity}"/>
      <ptk:setAttribute input="myinput" key="homecounty" value="${param.hcounty}"/>
      <ptk:setAttribute input="myinput" key="homestate" value="${param.hstate}"/>
      <ptk:setAttribute input="myinput" key="homezip" value="${param.hzip}"/>
      <ptk:setAttribute input="myinput" key="homecountry" value="${param.hcountry}"/>
      <ptk:setAttribute input="myinput" key="email" value="${param.hemail}"/>
      <ptk:setAttribute input="myinput" key="password" value="${param.password}"/>
      
      <ptk:setAttribute input="myinput" key="forgottenPasswordQuestions"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordQuestions" value="What is your favorite color?"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordQuestions" value="What is your mother's maiden name?"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordQuestions" value="What is the city of your birth?"/>

      <ptk:setAttribute input="myinput" key="forgottenPasswordAnswers"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordAnswers" value="${param.answer1}"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordAnswers" value="${param.answer2}"/>
      <ptk:addValue input="myinput" attribute="forgottenPasswordAnswers" value="${param.answer3}"/>

      <ptk:doCreate connection="registerConn" input="myinput" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">

         <c:set var="accountId"><ptk:getUniqueId output="myoutput"/></c:set>

         <table cellpadding="3" cellspacing="3" border="0">
            <tr class="middle">
               <td class="name"><i>Request Number:</i></td>
               <td class="data"><b><c:out value="${accountId}"/></b></td>
            </tr>
            <tr class="middle">
               <td class="name"><i>First Name:</i></td>
               <td class="data"><b><c:out value="${param.fname}"/></b></td>
            </tr>
            <tr class="middle">
               <td class="name"><i>Last Name:</i></td>
               <td class="data"><b><c:out value="${param.lname}"/></b></td>
            </tr>
         </table>

      </c:if>
      </td></tr>
   </table>
   <table class="buttons">
      <tr><td>
            <input type="submit" value="Finished"/>
         </td></tr>
   </table>
</form>
            
<c:if test="${ptkError == 'true'}">
   Error: <c:out value="${ptkStatus}"/>
   <c:set var="ptkError" value="false"/>
</c:if>

<%@include file="../common/conn_close.jsp"%>
