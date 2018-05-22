<!-- oper_add.jsp -->

<form action="operations.jsp">
   <input type=hidden name="mode" value="create"/>
   <table cellpadding="2" cellspacing="2" border="0" width="100%">
      <tr><td colspan="2"><b>Create&nbsp;User:</b></td></tr>
      <tr>
         <td align="right">First:</td>
         <td align="left">
            <input type="text" size="12" align="left" name="fname"/>
         </td>
      </tr>
      <tr>
         <td align="right">Last:</td>
         <td align="left">
            <input type="text" size="18" align="left" name="lname"/>
         </td>
      </tr>
      <tr>
         <td align="right">Email:</td>
         <td align="left">
            <input type="text" size="24" align="left" name="email"/>
         </td>
      </tr>
   </table>
   <center>
      <table cellpadding="2" cellspacing="2" border="0">
         <tr>
            <td align="center">
               <input type="submit" value="Submit"/>
            </td>
         </tr>
      </table>
   </center>
</form>
