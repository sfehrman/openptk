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

<%@page import="net.tanesha.recaptcha.ReCaptcha" %>
<%@page import="net.tanesha.recaptcha.ReCaptchaFactory" %>

<!--
OOTB themes for recapthca ... red (default), white, blackglass, clean
-->

<script type="text/javascript">
 var RecaptchaOptions = {
    theme : 'clean'
 };
 </script>

<div class="instructions">
   Please fill in all of the <b>REQUIRED (Blue)</b> fields ... Click <b>Submit</b>
</div>

<form name="data" action="">
   <input type=hidden name="mode" value="create"/>
   <table class="content" cellpadding="3" cellspacing="3">
      <tr align="left">
         <td>

            <table class="section">
               <tr>
                  <td class="middle-banner">User Profile:</td>
               </tr>
            </table>

            <table class="border" >
               <tr>
                  <td>
                     <table class="center">
                        <tr class="middle">
                           <td class="required">First Name:</td>
                           <td class="data"><input type="text" size="18" id="fname" align="left" name="fname" value="${param.fname}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Last Name:</td>
                           <td class="data"><input type="text" size="18" id="lname" align="left" name="lname" value="${param.lname}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="name">Phone:</td>
                           <td class="data"><input type="text" size="12" id="phone" align="left" name="phone" value="${param.phone}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Email:</td>
                           <td class="data"><input type="text" size="48" id="email" align="left" name="email" value="${param.email}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Confirm&nbsp;Email:</td>
                           <td class="data"><input type="text" size="48" id="confirmemail" align="left" name="confirmemail" value="${param.confirmemail}"/></td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

            <table class="section">
               <tr>
                  <td class="middle-banner">Login Information:</td>
               </tr>
            </table>

            <table class="border">
               <tr>
                  <td>
                     <table class="center">
                        <tr class="middle">
                           <td class="required">Password:</td>
                           <td class="data"><input type="password" size="18" id="password" align="left" name="password" value="${param.password}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Confirm Password:</td>
                           <td class="data"><input type="password" size="18" id="confirmpassword" align="left" name="confirmpassword" value="${param.confirmpassword}"/></td>
                        </tr>
                        <tr>
                           <td colspan="2"><hr></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is your favorite color?:</td>
                           <td class="data"><input type="text" size="18" id="answer1" align="left" name="answer1" value="${param.answer1}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is your mother's maiden name?:</td>
                           <td class="data"><input type="text" size="18" id="answer2" align="left" name="answer2" value="${param.answer2}"/></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is the city of your birth?:</td>
                           <td class="data"><input type="text" size="18" id="answer3" align="left" name="answer3" value="${param.answer3}"/></td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

            <table class="section">
               <tr>
                  <td class="middle-banner">Verification:</td>
               </tr>
            </table>

            <table class="border" >
               <tr>
                  <td>
                     <table class="center">
                        <tr class="middle">
                           <td class="name">Terms and Conditions:</td>
                           <td>
                              <table class="border">
                                 <tr><td>
                                       <div class="scrollbox">
                                          <%@include file="terms.html"%>
                                       </div>
                                    </td></tr>
                              </table>
                           </td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Accept:</td>
                           <td class="data"><input type="checkbox" name="accept"/>I agree to the Terms and Conditions</td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Real User Test:</td>
                           <td>
                              <table class="border">
                                 <tr>
                                    <td>
                                       <%
                                             ReCaptcha c = ReCaptchaFactory.newReCaptcha("6Ld8t8sSAAAAAEAOPM4a-lvYrEEX97-6tLWX1gMN", "PRIVATE_KEY_HERE", false);
                                             out.print(c.createRecaptchaHtml(null, null));
                                       %>
                                    </td>
                                    <td>
                                       <ol>
                                          <li>Read the two words in the image</li>
                                          <li>Type those words (space separated) in the space provided</li>
                                       </ol>
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

         </td>
      </tr>
   </table>

   <table align="center">
      <tr>
         <td>
            <input type="submit" value="Cancel" name="submit" onclick="cancel();"/>
         </td>
         <td>
            <input type="submit" value="Submit" name="submit" onclick="verifyData();"/>
         </td>
      </tr>
   </table>
                                    
</form>