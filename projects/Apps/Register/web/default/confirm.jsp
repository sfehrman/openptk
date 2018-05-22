<!-- confirm.jsp -->

<form name="confirm" action="">
   <input type=hidden name="mode" value="create"/>
   <input type="hidden" name="fname" value="${param.fname}"/>
   <input type="hidden" name="lname" value="${param.lname}"/>
   <input type="hidden" name="email" value="${param.email}"/>
   <input type="hidden" name="confirmemail" value="${param.confirmemail}"/>
   <table class="content">
      <tr><td>
            <p>
               Please <b>CONFIRM</b> the following information:
            </p>
            <table cellpadding="3" cellspacing="3" width="100%" >
               <tr class="middle">
                  <td class="name">Full&nbsp;Name:</td>
                  <td class="data">${param.fname}&nbsp;${param.lname}</td>
               </tr>
               <tr class="middle">
                  <td class="name">Email:</td>
                  <td class="data">${param.email}</td>
               </tr>
            </table>
            <p>
               Click <b>Submit</b> to complete the process.
            </p>
         </td></tr>
   </table>
   <table class="buttons">
      <tr>
         <td>
            <input type="submit" value="Prev" name="prev" onclick="prevConfirm();"/>
         </td>
         <td>
            <input type="submit" value="Cancel" name="submit" onclick="cancelConfirm();"/>
         </td>
         <td>
            <input type="submit" value="Submit" name="submit"/>
         </td>
      </tr>
   </table>
</form>