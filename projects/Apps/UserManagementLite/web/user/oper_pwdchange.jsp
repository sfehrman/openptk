<!-- oper_pwdchange.jsp -->

<script type="text/javascript">
   function checkPassword()
   {
      if ( document.pwdChange.password.value != document.pwdChange.confirm.value )
      {
         window.alert("Passwords do not match!");
         return false;
      }
      else
      {
         return true;
      }
   }
</script>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="myinput"/>
   <ptk:setUniqueId input="myinput" value="${param.uid}"/>
   <ptk:doRead connection="umlConn" input="myinput" output="myoutput"/>

   <c:if test="${ptkError == 'false'}">
      <c:choose>
         <c:when test="${not empty param.password}">

            <ptk:setInput var="passwordInput"/>
            <ptk:setUniqueId input="passwordInput" value="${param.uid}"/>
            <ptk:setAttribute input="passwordInput" key="password" value="${param.password}"/>
            <ptk:doPasswordChange connection="umlConn" input="passwordInput" output="passwordOutput"/>

            <c:if test="${ptkError == 'false'}">
               <table cellpadding="1" cellspacing="2" border="0" width="100%">
                  <tr><td colspan="2"><b>Password has been changed for:</b></td></tr>
                  <tr>
                     <td align=right width="50%">Id:</td>
                     <td align=left><b><c:out value="${param.uid}"/></b></td>
                  </tr>
                  <tr>
                     <td align=right>Name:</td>
                     <td align=left>
                        <b><ptk:getValue output='myoutput' name='firstname'/>&nbsp;
                           <ptk:getValue output='myoutput' name='lastname'/></b>
                     </td>
                  </tr>
               </table>
            </c:if>
         </c:when>
         <c:otherwise>
            <form method="post" action="operations.jsp" name="pwdChange" onsubmit="return checkPassword();">
               <input type=hidden name="mode" value="pwdchange"/>
               <input type=hidden name="uid" value="<c:out value='${param.uid}'/>"/>
               <table cellpadding="1" cellspacing="2" border="0" width="100%">
                  <tr><td colspan="2"><b>Change&nbsp;Password:</b></td></tr>
                  <tr>
                     <td align=right width="50%">Id:</td>
                     <td align=left><b><c:out value="${param.uid}"/></b></td>
                  </tr>
                  <tr>
                     <td align=right>Name:</td>
                     <td align=left>
                        <b><ptk:getValue output='myoutput' name='firstname'/>&nbsp;
                           <ptk:getValue output='myoutput' name='lastname'/></b>
                     </td>
                  </tr>
                  <tr><td align=right>New Password:</td>
                     <td align=left>
                        <input type="password" size="18" align="left" name="password"/>
                     </td>
                  </tr>
                  <tr>
                     <td align=right>Confirm Password:</td>
                     <td align=left>
                        <input type="password" size="18" align="left" name="confirm"/>
                     </td>
                  </tr>
                  <tr>
                     <td></td>
                     <td align=left><input type="submit" value="Change It"/></td>
                  </tr>
               </table>
            </form>
         </c:otherwise>
      </c:choose>
   </c:if>
</c:if>