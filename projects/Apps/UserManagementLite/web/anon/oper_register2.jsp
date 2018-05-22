<!-- oper_register2.jsp -->

<form action="index.jsp">
   <input type=hidden name="mode" value="create"/>
   <table cellpadding="1" cellspacing="2" border="0" width="100%">

      <c:if test='${registermode == "register" }'>
         <tr><td colspan="2"><b><i>Account&nbsp;Validated</i></b></td></tr>
      </c:if>

      <tr><td colspan="2"><b>New&nbsp;User Information:</b></td></tr>
      <tr>
         <td align=right>First:</td>
         <td align=left>
            <input type="text" size="12" align="left" name="fname"/>
         </td>
      </tr>
      <tr>
         <td align=right>Last:</td>
         <td align=left>
            <input type="text" size="18" align="left" name="lname"/>
         </td>
      </tr>
      <tr>
         <td align=right>Email:</td>
         <td align=left>
            <input type="text" size="24" align="left" name="email"/>
         </td>
      </tr>
      <tr>
         <td></td>
         <td align=left>
            <input type="submit" value="Submit"/>
         </td>
      </tr>
   </table>
</form>
<br>
&nbsp;&nbsp;<a href="../index.jsp">Return to Menu</a>
