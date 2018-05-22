<%@include file="header.jsp"%>
<!-- Begin Main Panel -->
<div id="mainpanel">


   <div id="leftborder">

   </div>

   <div id="subjectlist">
      <div id="list" class="listresults">
         <div id="results-accordion">
            <a id="results-accordion-searchtab" href="#" class="accordion-toggle active">Search Results</a>
            <div id="results-accordion-searchcontent" class="accordion-content active">
               <div id="results-panel">
                  <div id="results">
                  </div>
                  <div id="listnav"></div>
               </div>
            </div>
            <a id="results-accordion-reportstotab" href="#" class="accordion-toggle">Reports To</a>
            <div id="results-accordion-reportstocontent" class="accordion-content">
               <div id="tab-reportsTo"></div>
            </div>
            <a id="results-accordion-peerstab" href="#" class="accordion-toggle">Peers</a>
            <div id="results-accordion-peerscontent" class="accordion-content">
               <div id="tab-peers"></div>
            </div>
            <a id="results-accordion-directreportstab" href="#" class="accordion-toggle">Direct Reports</a>
            <div id="results-accordion-directreportscontent" class="accordion-content">
               <div id="tab-directReports"></div>
            </div>
         </div>
      </div>

   </div>

   <div id="subjectdetails">
      <div id="details" class="detailresults">
         <div id="result">



            <div id="subjectcontrols">
               <div id="subjectorgtree">
                  <a href = "javascript:void(0)" title="Organization Information ..." onclick = "$('orgsubjectlight').style.display='block'; $('orgsubjectfade').style.display='block';"> <div id="subjectorgtreelink">Org</div></a>
               </div>
            </div>

            <div id="subject-tabs" class="tabset">
               <ul class="tabset_tabs">
                  <li id="subjectcard" class="active"><a href="#tab-one" title="User Details ..."></a></li>
                  <li id='subjectedit'><a href="#tab-two" title="Update Record ..."></a></li>
                  <li id='subjectchangepassword'> <a href="#tab-three" title="Change Password ..."></a></li>
                  <li id='subjectresetpassword'><a href="#tab-four" title="Reset Password ..."></a></li>
                  <li id='subjectdelete'><a href="#tab-five" title="Delete User ..."></a></li>
               </ul>
               <div class="tabset_content_container">
                  <div id="tab-one" class="tabset_content">

                     <div id="subject">


                     </div>
                     <div id="subjectmore">
                     </div>



                  </div>
                  <div id="tab-two" class="tabset_content">
                     <div id="editcancellink"><a href = "javascript:void(0)" title="Cancel" onclick = 'subjecttabs.toggleTab("0")'>&times;</a></div>
                     <div id="subjectupdate">
                        <form id="updateSubjectForm" action="javascript:updateSubject()">
                           <input type="hidden" id="subjectupdateuniqueid" size="25"/>

                           <div id="subject-accordion" class="horizontal">
                              <a id="subject-accordion-detailtab" title="Profile" href="#" class="accordion-toggle active"></a>
                              <div id="subject-accordion-detailcontent" class="accordion-content active">
                                 <table>
                                    <tr>
                                       <td align="right"><i>Firstname: </i></td>
                                       <td><input type="text" id="subjectupdatefirstname" size="15" /></td>
                                       <td align="right"><i>Lastname: </i></td>
                                       <td><input type="text" id="subjectupdatelastname" size="15" /></td>
                                    </tr>
                                    <tr>
                                       <td align="right"><i>Email: </i></td>
                                       <td><input type="text" id="subjectupdateemail" size="15" /></td>
                                       <td align="right"><i>Title: </i></td>
                                       <td><input type="text" id="subjectupdatetitle" size="15" /></td>
                                    </tr>
                                    <tr>
                                       <td align="right"><i>Telephone: </i></td>
                                       <td><input type="text" id="subjectupdatetelephone" size="15" /></td>
                                       <td align="right"><i>Manager: </i></td>
                                       <td><input type="text" id="subjectupdatemanager" size="15" /></td>
                                    </tr>
                                    <tr>
                                       <td align="right"><i>Organization: </i></td>
                                       <td><input type="text" id="subjectupdateorganization" size="15"/></td>
                                       <td align="right"><i>Roles: </i></td>
                                       <td><input type="text" id="subjectupdateroles" size="15"/></td>
                                    </tr>
                                    <tr id="subject-location-fields">
                                       <td align="right"><i>Location: </i></td>
                                       <td><input type="text" id="subjectupdatelocation" size="15"/></td>
                                       <td align="right"><i>Street Address: </i></td>
                                       <td><input type="text" id="subjectupdatestreet" size="15"/></td>
                                    </tr>
                                    <tr id="subject-location-fields2">
                                       <td align="right"><i>City: </i></td>
                                       <td><input type="text" id="subjectupdatecity" size="15"/></td>
                                       <td align="right"><i>State: </i></td>
                                       <td><input type="text" id="subjectupdatestate" size="15"/></td>
                                    </tr>
                                    <tr id="subject-location-fields3">
                                       <td align="right"><i>Postal Code: </i></td>
                                       <td><input type="text" id="subjectupdatepostalCode" size="15"/></td>
                                    </tr>
                                 </table>

                              </div>
                              <a id="subject-accordion-fpwdtab" title="Forgotten Password Questions" href="#" class="accordion-toggle"></a>
                              <div id="subject-accordion-fpwdcontent" class="accordion-content">

                                 <h3>Forgotten Password Answers</h3>
                                 <input type="hidden" id="fpnumquestions"/>
                                 <div id="fpwdupdate">
                                 </div>
                              </div>
                           </div>

                           <br>
                           <br>
                           <input id="updateSubjectFormSubmit" type="submit" name="Update" value="Update">
                        </form>
                     </div>




                  </div>
                  <div id="tab-three" class="tabset_content">
                     <div id="cancellink"><a href = "javascript:void(0)" title="Cancel" onclick = 'subjecttabs.toggleTab("0")'>&times;</a></div>
                     <div id="subjectdochangepassword">
                        <form id="changepasswordSubjectForm" action="javascript:changePasswordSubject()">
                           <p>
                              <input type="hidden" id="uniqueid" size="25"/>
                              <b>New Password:</b>
                              <br><input type="password" id="subjectpassword" size="20"/>
                              <br><b>Confirm New Password:</b>
                              <br><input type="password" id="subjectconfpassword" size="20"/>
                              <br><INPUT type="submit" name="Update" value="Change Password">
                           </p>
                        </form>
                     </div>


                  </div>
                  <div id="tab-four" class="tabset_content">
                     <div id="cancellink"><a href = "javascript:void(0)" title="Cancel" onclick = 'subjecttabs.toggleTab("0")'>&times;</a></div>
                     <div id="subjectresetpasswordConfirm">
                        <form id="resetPasswordSubjectForm" action="javascript:resetPasswordSubject()">
                           <p><b>Are you sure you want to reset the password?</b>
                              <input type="hidden" id="resetuniqueid" />
                              <br><br><INPUT type="submit" name="Update" value="Confirm">
                           </p>
                        </form>
                     </div>
                     <div id="resetdetails">
                     </div>


                  </div>
                  <div id="tab-five" class="tabset_content">
                     <div id="cancellink"><a href = "javascript:void(0)" title="Cancel" onclick = 'subjecttabs.toggleTab("0")'>&times;</a></div>
                     <div id="deleteconfirm">
                     </div>
                  </div>
               </div>
            </div>

         </div>

      </div>

   </div>


   <div id="rightborder">
   </div>
</div>
<!-- End of Main Panel -->
<%@include file="footer.jsp"%>

<!-- Modal Panel Details -->
<div id="createsubject">
   <div id="createsubjectlight" class="white_create_content">
      <div id="createcancellink"><a href = "javascript:void(0)" title="Cancel" onclick = "$('createsubjectlight').style.display='none';$('createsubjectfade').style.display='none'">&times;</a></div>
      <div id="create">
         <form id="createSubjectForm" action="javascript:createSubject()">
            <center>
               <h2>Enter New User Details</h2>
               <table>
                  <tr><td>Firstname:</td><td><input type="label" id="createfirstname" size="10"/></td></tr>
                  <tr><td>Lastname:</td><td><input type="label" id="createlastname" size="10" /></td></tr>
                  <tr><td>Email:</td><td><input type="text" id="createemail" size="10" /></td></tr>
                  <tr><td>title:</td><td><input type="text" id="createtitle" size="10" /></td></tr>
                  <tr><td>Telephone:</td><td><input type="text" id="createtelephone" size="10" /></td></tr>
                  <tr><td>Manager:</td><td><input type="text" id="createmanager" size="10" /></td></tr>
                  <tr><td>Roles:</td><td><input type="text" id="createroles" size="10"/></td></tr>
                  <tr><td>Organization:</td><td><input type="text" id="createorganization" size="10"/></td></tr>
                  <tr><td>Password:  </td><td><INPUT id="createpassword" TYPE="password" NAME="password" size="10"></td></tr>
                  <tr><td>Confirm:  </td><td><INPUT id="createconfpassword" TYPE="password" NAME="confpassword" size="10"></td></tr>
               </table>
               <br><INPUT type="submit" name="Submit" value="Submit">
            </center>
         </form>
      </div>

   </div>
   <div id="createsubjectfade" class="black_create_overlay"></div>
</div>


<div id="loginpanel">
   <div id="loginpanellight" class="login_white_content">
      <div id="logincancellink"><a href = "javascript:void(0)" title="Cancel" onclick = "clearLogin();return false">&times;</a></div>
      <div id="loginpanelcontent">
         <div id="loginpanelinner">
            <FORM name="loginPanelFormName" id="loginPanelForm" ACTION="" METHOD="POST"> <b>Please Login:</b>
               <br><br>
               <INPUT TYPE="hidden" NAME="clientid" value="identitycentral">
               <INPUT TYPE="hidden" NAME="goto" value="<%=request.getRequestURI()%>">
               <table>
                  <tr><td colspan="2"><input type="hidden" name="login" value="true" /></td></tr>
                  <tr><td>Username:  </td><td><INPUT id="username" TYPE="text" NAME="user"></td></tr>
                  <tr><td>Password:  </td><td><INPUT id="userpassword"  TYPE="password" NAME="password"></td></tr>
               </table
               <BR><INPUT TYPE="submit" VALUE="Submit"></FORM>
            <BR><a href = "javascript:void(0)" onclick = "forgotPasswordStart(); return false">Forgot Password</a>
         </div>
         <div id="forgotpanelinner">
            <br>
            <form id="fpwdForm1" action="javascript:forgotPasswordSubject()">
               <fieldset>
                  <legend>Enter UserId</legend>
                  <input type="text" id="fp0uniqueid" size="10"/>
                  <br><br><INPUT type="submit" name="Update" value="Continue">
               </fieldset>
            </form>
         </div>

         <div id="subjectForgotPass">
            <form id="ForgotPassForm" action="javascript:forgotPasswordSubject2()">
               <fieldset>
                  <legend>Forgotten Password User Questions</legend>
                  <input type="hidden" id="fpuniqueid" />
                  <input type="hidden" id="fpnumquestions" />
                  <div id="questions"></div>
                  <br><INPUT type="submit" name="Update" value="Validate">
               </fieldset>
            </form>

         </div>
         <div id="fpwdchangepassword">
            <h2>Answers Verified!</h2>
            <form id="fpwdchangePasswordForm" action="javascript:fpwdChangePassword()">
               <fieldset>
                  <legend>Change Password</legend>
                  <p>
                     <input type="hidden" id="uniqueid" />
                  <table>
                     <tr><td>Password:</td><td><input type="password" id="fp2password" size="10"/></td></tr>
                     <tr><td>Confirm:</td><td><input type="password" id="fp2confpassword" size="10"/></td></tr>
                  </table>
                  <br><INPUT type="submit" name="Update" value="Change Password"/>
                  </p>
               </fieldset>
            </form>
         </div>

      </div>
   </div>
   <div id="loginpanelfade" class="black_overlay"></div>
</div>

<div id="registrationpanel">
   <div id="registrationpanellight" class="white_content">
      <div id="logincancellink"><a href = "javascript:void(0)" title="Cancel" onclick = "clearLogin();return false">&times;</a></div>
      <div id="registrationpanelcontent">
         <div id="registrationpanelinner">
            <FORM id="registration" action="javascript:registerUser()" METHOD="POST">
               <fieldset>
                  <legend>User Registration</legend>
                  <br>
                  <table>
                     <tr>
                        <td>
                           <table>
                              <tr><td align="right">Firstname: </td><td><INPUT id="regfirstname" TYPE="text" NAME="firstname" size="10"></td></tr>
                              <tr><td align="right">Lastname: </td><td><INPUT id="reglastname" TYPE="text" NAME="lastname" size="10"></td></tr>
                              <tr><td align="right">Email: </td><td><INPUT id="regemail" TYPE="text" NAME="email" size="10"></td></tr>
                              <tr><td align="right">Password: </td><td><INPUT id="regpassword" TYPE="password" NAME="password" size="10"></td></tr>
                              <tr><td align="right">Confirm: </td><td><INPUT id="regconfpassword" TYPE="password" NAME="confpassword" size="10"></td></tr>
                           </table>
                        </td>
                        <td align="right">
                           <table id="qtab1">
                              <tr><td><b>Forgotten Password Questions</b></td></tr>
                              <input type="hidden" id="fpnumquestions" value="3" />
                              <tr><td>What is your favorite color?</td></tr>
                              <input type="hidden" id="regfpwdquestion0" value="What is your favorite color?" />
                              <tr><td><INPUT id="regfpwdanswer0" TYPE="password" NAME="regfpwdanswer0" size="10"></td></tr>
                              <tr><td>What is your mother's maiden name?</td></tr>
                              <input type="hidden" id="regfpwdquestion1" value="What is your mother's maiden name?" />
                              <tr><td><INPUT id="regfpwdanswer1" TYPE="password" NAME="regfpwdanswer1" size="10"></td></tr>
                              <tr><td>What is the city of your birth?</td></tr>
                              <input type="hidden" id="regfpwdquestion2" value="What is the city of your birth?" />
                              <tr><td><INPUT id="regfpwdanswer2" TYPE="password" NAME="regfpwdanswer2" size="10"></td></tr>
                           </table>
                        </td>
                     </tr>
                  </table>
                  <br>
                  <center><a href="terms.jsp">Terms and Conditons</a><INPUT id="regacceptterms" TYPE=CHECKBOX NAME="acceptterms"></center>
                  <BR><BR><INPUT TYPE="submit" VALUE="Register">
                  <br>
               </fieldset>
            </FORM>
            <br>

         </div>
      </div>

   </div>
   <div id="registrationpanelfade" class="black_overlay"></div>
</div>


<div id="organizationsubject">
   <div id="orgsubjectlight" class="org_white_content">
      <div id="orgchartcancellink"><a href = "javascript:void(0)" title="Cancel" onclick = "$('orgsubjectlight').style.display='none';$('orgsubjectfade').style.display='none'; subjecttabs.toggleTab('0');">&times;</a></div>
      <div id="organizationsubjectDisplay">
         <h2>Organization Information</h2>

         <div id="reportsTo"></div>
         <div id="oc-subject"></div>
         <div id="directReports"></div>
         <div id="peers"></div>

      </div>

   </div>
   <div id="orgsubjectfade" class="black_overlay"></div>
</div>
