<!-- terms.jsp -->

<form name="terms" action="">
   <input type=hidden name="mode" value="confirm"/>
   <input type="hidden" name="fname" value="${param.fname}"/>
   <input type="hidden" name="lname" value="${param.lname}"/>
   <input type="hidden" name="email" value="${param.email}"/>
   <input type="hidden" name="confirmemail" value="${param.confirmemail}"/>
   <table class="content">
      <tr><td>
            <p>
               Please review and <b>ACCEPT</b> these Terms and Conditions:
            </p>
            <table class="border">
               <tr><td>
                     <div class="scrollbox">
                        <%@include file="terms.html"%>
                     </div>
                  </td></tr>
            </table>
            <table cellpadding="3" cellspacing="3" width="100%" >
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
            <input type="submit" value="Prev" name="prev" onclick="prevTerms();"/>
         </td>
         <td>
            <input type="submit" value="Cancel" name="submit" onclick="cancelTerms();"/>
         </td>
         <td>
            <input type="submit" value="Next" name="next" onclick="verifyTerms();"/>
         </td>
      </tr>
   </table>
</form>