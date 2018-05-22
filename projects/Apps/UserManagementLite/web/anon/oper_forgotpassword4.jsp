<!-- oper_forgotpassword4.jsp -->

<ptk:getConnection var="umlConn" properties="openptk_client" scope="session"/>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="phase3"/>
   <ptk:setUniqueId input="phase3" value="${param.username}"/>
   <ptk:setProperty input="phase3" key="mode" value="change"/>
   <ptk:setAttribute input="phase3" key="password" value="${param.password}"/>
   <ptk:doPasswordForgot connection="umlConn" input="phase3" output="changed"/>

   <c:if test="${ptkError == 'false'}">

      <ptk:setInput var="myinput"/>
      <ptk:setUniqueId input="myinput" value="${param.username}"/>
      <ptk:doRead connection="umlConn" input="myinput" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">
         <table cellpadding="1" cellspacing="2" border="0" width="100%">
            <tr><td colspan="2"><b>Password has been changed for:</b></td></tr>
            <tr>
               <td align=right>Id:</td>
               <td align=left><b><c:out value="${param.username}"/></b></td>
            </tr>
            <tr>
               <td align=right>Name:</td>
               <td align=left>
                  <b><ptk:getValue output='myoutput' name='firstname'/>&nbsp;
                     <ptk:getValue output='myoutput' name='lastname'/></b>
               </td>
            </tr>
         </table>

         <br>
         &nbsp;&nbsp;<a href="../anon/index.jsp?mode=menu&logout=true">Return to Menu</a>
         <br>
      </c:if>
   </c:if>
</c:if>