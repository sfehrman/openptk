<form method="get"  action="index.jsp">

   <table cellpadding="1" cellspacing="2" border="0" width="100%">
      <tr valign="top"><td colspan="2"><a href="../index.jsp">Main Page</a></td></tr>
      <tr><td colspan="2"><b>Configuration:</b></td></tr>
      <tr><td colspan="2"><hr></td></tr>

      <tr>
         <td align=right>Properties File:</td>
         <td>
            <input type="text" size="60" name="configXML" value="${openptkClientProps}"><br>
            <i>default: ${openptkClientProps}</i>
         </td>
      </tr>

      <tr><td colspan="2"><hr></td></tr>

      <tr>
         <td></td>
         <td align=left>
            <input type="submit" value="Set"/>
         </td>
      </tr>

      <tr><td colspan="2"><hr></td></tr>

      <tr>
         <td align=right>Registration Validation:</td>
         <td>
             (register2 = no validation, register = validation)
            <br>
            Current mode:  <b><c:out value="${registermode}"/></b>
      <br>
      <a href="index.jsp?regval=on">Turn Registration Validation On</a>
      <br>
      <a href="index.jsp?regval=off">Turn Registration Validation Off</a>
      </td>
      </tr>

   </table>
</form>
