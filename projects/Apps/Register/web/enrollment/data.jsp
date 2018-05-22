<form name="data" action="">
   <input type=hidden name="mode" value="create"/>
   <table class="content">
      <tr align="left"><td>
            <table cellpadding="3" cellspacing="3" width="100%" >
               <tr class="middle">
                  <td colspan="2">
                     <b>Contact information:</b>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">First Name:</td>
                  <td class="data">
                     <input type="text" size="12" id="fname" align="left" name="fname" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Last Name:</td>
                  <td class="data">
                     <input type="text" size="18" id="lname" align="left" name="lname" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Email:</td>
                  <td class="data">
                     <input type="text" size="24" id="email" align="left" name="email" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Confirm&nbsp;Email:</td>
                  <td class="data">
                     <input type="text" size="24" id="confirmemail" align="left" name="confirmemail" value=""/>
                  </td>
               </tr>
               <tr class="middle">
                  <td colspan="2">
                     <hr>
                  </td>
               </tr>
               <tr class="middle">
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
               </tr>
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
                                 <%@include file="terms.html"%>
                              </div>
                           </td></tr>
                     </table>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Accept:</td>
                  <td class="data">
                     <input type="checkbox" name="accept"/>I agree to the Terms and Conditions
                  </td>
               </tr>
            </table>
         </td></tr>
   </table>
   <table class="buttons">
      <tr>
         <td>
            <input type="submit" value="Cancel" name="submit" onclick="cancel();"/>
         </td>
         <td>
            <input type="submit" value="Submit" name="submit"/>
         </td>
      </tr>
   </table>
</form>