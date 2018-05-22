<!-- data.jsp -->

<form name="data" action="">
   <input type=hidden name="mode" value="terms"/>
   <table class="content">
      <tr><td>
            <p>
               Please provide the following <b>REQUIRED</b> information:
            </p>
            <table cellpadding="3" cellspacing="3" width="100%" >
               <tr class="middle">
                  <td class="name">First Name:</td>
                  <td class="data">
                     <input type="text" size="12" id="fname" align="left" name="fname" value="${param.fname}"/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Last Name:</td>
                  <td class="data">
                     <input type="text" size="18" id="lname" align="left" name="lname" value="${param.lname}"/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Email:</td>
                  <td class="data">
                     <input type="text" size="24" id="email" align="left" name="email" value="${param.email}"/>
                  </td>
               </tr>
               <tr class="middle">
                  <td class="name">Confirm&nbsp;Email:</td>
                  <td class="data">
                     <input type="text" size="24" id="confirmemail" align="left" name="confirmemail" value="${param.confirmemail}"/>
                  </td>
               </tr>
            </table>
         </td></tr>
   </table>
   <table class="buttons">
      <tr>
         <td>
            <input type="submit" value="Prev" name="prev" onclick="prevData();"/>
         </td>
         <td>
            <input type="reset" value="Reset" name="reset"/>
         </td>
         <td>
            <input type="submit" value="Next" name="submit" onclick="verifyData();"/>
         </td>
      </tr>
   </table>
</form>