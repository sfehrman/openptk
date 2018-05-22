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

<form name="data" action="">
   <input type=hidden name="mode" value="create"/>
   <table class="content" cellpadding="3" cellspacing="3">
      <tr align="left">
         <td>
            <table class="section">
               <tr>
                  <td class="middle-banner">Profile:</td>
               </tr>
            </table>

            <table class="border" >
               <tr>
                  <td>
                     <table align="center">
                        <tr class="middle">
                           <td class="required">First Name:</td>
                           <td class="data">
                              <input type="text" size="18" id="fname" align="left" name="fname" value=""/>
                           </td>
                           <td class="required">Last Name:</td>
                           <td class="data">
                              <input type="text" size="18" id="lname" align="left" name="lname" value=""/>
                           </td>
                        </tr>

                        <tr class="middle">
                           <td class="name">Middle Name:</td>
                           <td class="data">
                              <input type="text" size="12" id="mname" align="left" name="mname" value=""/>
                           </td>
                           <td class="required">Last 4 SSN:</td>
                           <td class="data">
                              <input type="text" size="4" id="last4ssn" align="left" name="last4ssn" value=""/>
                           </td>
                        </tr>

                        <tr class="middle">
                           <td class="required">Date Of Birth:</td>
                           <td class="data">
                              <input type="text" size="12" id="dob" align="left" name="dob" value=""/>
                           </td>
                           <td class="required">Drivers License #:</td>
                           <td class="data">
                              <input type="text" size="18" id="dln" align="left" name="dln" value=""/>
                           </td>
                        </tr>

                        <tr class="middle">
                           <td class="required">Gender:</td>
                           <td>
                              <input type="radio" name="gender" value="male" checked>Male&nbsp;
                              <input type="radio" name="gender" value="female">Female
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

            <table class="section">
               <tr>
                  <td class="middle-banner">Address:</td>
               </tr>
            </table>

            <table class="border" >
               <tr>
                  <td>
                     <table align="center">
                        <tr valign="top" class="middle">
                           <td>
                              <table>
                                 <tr><td class="name"><b>Home:</b></td></tr>
                                 <tr>
                                    <td class="required">Address 1:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="haddr1" align="left" name="haddr1" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Address 2:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="haddr2" align="left" name="haddr2" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="required">City:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="hcity" align="left" name="hcity" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="required">State:</td>
                                    <td class="data">
                                       <select name="hstate">
                                          <option>AK</option>
                                          <option>AL</option>
                                          <option>AR</option>
                                          <option>CA</option>
                                          <option>CO</option>
                                          <option>CT</option>
                                          <option>DC</option>
                                          <option>DE</option>
                                          <option>FL</option>
                                          <option>GA</option>
                                          <option>HI</option>
                                          <option>IA</option>
                                          <option>ID</option>
                                          <option>IL</option>
                                          <option>IN</option>
                                          <option>KS</option>
                                          <option>KY</option>
                                          <option>LA</option>
                                          <option>MA</option>
                                          <option>MD</option>
                                          <option>ME</option>
                                          <option>MI</option>
                                          <option>MN</option>
                                          <option>MO</option>
                                          <option>MS</option>
                                          <option>MT</option>
                                          <option>NC</option>
                                          <option>ND</option>
                                          <option>NE</option>
                                          <option>NH</option>
                                          <option>NJ</option>
                                          <option>NM</option>
                                          <option>NY</option>
                                          <option>NV</option>
                                          <option>OH</option>
                                          <option>OK</option>
                                          <option>OR</option>
                                          <option>PA</option>
                                          <option>RI</option>
                                          <option>SC</option>
                                          <option>SD</option>
                                          <option>TN</option>
                                          <option>TX</option>
                                          <option>UT</option>
                                          <option>VA</option>
                                          <option>VT</option>
                                          <option>WA</option>
                                          <option>WI</option>
                                          <option>WV</option>
                                          <option>WY</option>
                                       </select>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="required">Zip Code:</td>
                                    <td class="data">
                                       <input type="text" size="10" id="hzip" align="left" name="hzip" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="required">County:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="hcounty" align="left" name="hcounty" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="required">Country:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="hcountry" align="left" name="hcountry" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Primary:</td>
                                    <td>
                                       <input type="checkbox" checked="true" name="hprimaryindicator" value="hprimaryindictor">
                                    </td>
                                 </tr>
                              </table>
                           </td>
                           <td>
                              <table>
                                 <tr><td class="name"><b>Work:</b></td></tr>
                                 <tr>
                                    <td class="name">Address 1:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="waddr1" align="left" name="waddr1" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Address 2:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="waddr2" align="left" name="waddr2" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">City:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="wcity" align="left" name="wcity" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">State:</td>
                                    <td class="data">
                                       <select name="hstate">
                                          <option>AK</option>
                                          <option>AL</option>
                                          <option>AR</option>
                                          <option>CA</option>
                                          <option>CO</option>
                                          <option>CT</option>
                                          <option>DC</option>
                                          <option>DE</option>
                                          <option>FL</option>
                                          <option>GA</option>
                                          <option>HI</option>
                                          <option>IA</option>
                                          <option>ID</option>
                                          <option>IL</option>
                                          <option>IN</option>
                                          <option>KS</option>
                                          <option>KY</option>
                                          <option>LA</option>
                                          <option>MA</option>
                                          <option>MD</option>
                                          <option>ME</option>
                                          <option>MI</option>
                                          <option>MN</option>
                                          <option>MO</option>
                                          <option>MS</option>
                                          <option>MT</option>
                                          <option>NC</option>
                                          <option>ND</option>
                                          <option>NE</option>
                                          <option>NH</option>
                                          <option>NJ</option>
                                          <option>NM</option>
                                          <option>NY</option>
                                          <option>NV</option>
                                          <option>OH</option>
                                          <option>OK</option>
                                          <option>OR</option>
                                          <option>PA</option>
                                          <option>RI</option>
                                          <option>SC</option>
                                          <option>SD</option>
                                          <option>TN</option>
                                          <option>TX</option>
                                          <option>UT</option>
                                          <option>VA</option>
                                          <option>VT</option>
                                          <option>WA</option>
                                          <option>WI</option>
                                          <option>WV</option>
                                          <option>WY</option>
                                       </select>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Zip Code:</td>
                                    <td class="data">
                                       <input type="text" size="10" id="wzip" align="left" name="wzip" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">County:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="wcounty" align="left" name="wcounty" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Country:</td>
                                    <td class="data">
                                       <input type="text" size="32" id="wcountry" align="left" name="wcountry" value=""/>
                                    </td>
                                 </tr>
                                 <tr>
                                    <td class="name">Primary:</td>
                                    <td>
                                       <input type="checkbox" name="wprimaryindicator" value="wprimaryindictor">
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

            <table class="section">
               <tr>
                  <td class="middle-banner">Emails:</td>
               </tr>
            </table>

            <table class="border" >
               <tr>
                  <td>
                     <table align="center">
                        <tr valign="top" class="middle">
                           <td class="required">Home Email:</td>
                           <td class="data">
                              <input type="text" size="48" id="hemail" align="left" name="hemail" value=""/>
                              <input type="checkbox" checked="true" name="hemailprimary" value="hemailprimary">
                           </td>
                           <td>Primary</td>
                        </tr>
                        <tr valign="top" class="middle">
                           <td class="name">Work Email:</td>
                           <td class="data">
                              <input type="text" size="48" id="wemail" align="left" name="wemail" value=""/>
                              <input type="checkbox" name="wemailprimary" value="wemailprimary">
                           </td>
                           <td>Primary</td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>

            <table class="section">
               <tr>
                  <td class="middle-banner">Phone Numbers:</td>
               </tr>
            </table>

            <table class="border">
               <tr>
                  <td>
                     <table align="center">
                        <tr valign="top" class="middle">
                           <td class="required">Home:</td>
                           <td class="data">
                              <input type="text" size="32" id="hphone" align="left" name="hphone" value=""/>
                           </td>
                           <td></td>
                           <td></td>
                           <td>
                              <input type="checkbox" checked="true" name="hphoneprimary" value="hponeprimary">
                           </td>
                           <td>Primary</td>
                        </tr>
                        <tr valign="top" class="middle">
                           <td class="name">Work:</td>
                           <td class="data">
                              <input type="text" size="32" id="wphone" align="left" name="wphone" value=""/>
                           </td>
                           <td class="name">Extension:</td>
                           <td class="data">
                              <input type="text" size="8" id="wexten" align="left" name="wexten" value=""/>
                           </td>
                           <td>
                              <input type="checkbox" name="hphoneprimary" value="hponeprimary">
                           </td>
                           <td>Primary</td>
                        </tr>
                        <tr valign="top" class="middle">
                           <td class="name">Other:</td>
                           <td class="data">
                              <input type="text" size="32" id="ophone" align="left" name="ophone" value=""/>
                           </td>
                           <td></td>
                           <td></td>
                           <td>
                              <input type="checkbox" name="hphoneprimary" value="hponeprimary">
                           </td>
                           <td>Primary</td>
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
                     <table align="center">
                        <tr class="middle">
                           <td class="required">Password:</td>
                           <td class="data">
                              <input type="password" size="18" id="password" align="left" name="password" value=""/>
                           </td>
                        </tr>
                        <tr class="middle">
                           <td class="required">Confirm Password:</td>
                           <td class="data">
                              <input type="password" size="18" id="confirmpwd" align="left" name="confirmpwd" value=""/>
                           </td>
                        </tr>
                        <tr>
                           <td colspan="2"><hr></td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is your favorite color?:</td>
                           <td class="data">
                              <input type="text" size="18" id="answer1" align="left" name="answer1" value=""/>
                           </td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is your mother's maiden name?:</td>
                           <td class="data">
                              <input type="text" size="18" id="answer2" align="left" name="answer2" value=""/>
                           </td>
                        </tr>
                        <tr class="middle">
                           <td class="required">What is the city of your birth?:</td>
                           <td class="data">
                              <input type="text" size="18" id="answer3" align="left" name="answer3" value=""/>
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
            <input type="submit" value="Submit" name="submit" onclick="verifyData();return false;"/>
         </td>
      </tr>
   </table>
</form>