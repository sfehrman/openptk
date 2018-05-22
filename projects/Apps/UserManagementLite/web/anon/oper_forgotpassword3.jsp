<!-- oper_forgotpassword3.jsp -->

<ptk:getConnection var="umlConn" properties="openptk_client" scope="session"/>

<c:set var="counter" value="0"/>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="phase2"/>
   <ptk:setUniqueId input="phase2" value="${param.username}"/>
   <ptk:setProperty input="phase2" key="mode" value="answers"/>
   <ptk:setAttribute input="phase2" var="attrQ"/>
   <ptk:setAttribute input="phase2" key="forgottenPasswordAnswers"/>

   <c:forEach var="counter" begin="0" end="${param.num_answers - 1}" step="1">
      <c:set var="answerkey">answer.<c:out value="${counter}"/></c:set>

      <% pageContext.setAttribute("thisanswerval", (String) request.getParameter((String) pageContext.getAttribute("answerkey")));%>

      <ptk:addValue input="phase2" attribute="forgottenPasswordAnswers" value="${thisanswerval}"/>

   </c:forEach>

   <ptk:doPasswordForgot connection="umlConn" input="phase2" output="validation"/>

   <c:set var="fpwdState">
      <ptk:getInformation type="state" element="validation"/>
   </c:set>

   <c:choose>
      <c:when test="${fpwdState != 'SUCCESS'}">
         <h3>Authentication Question/Answers Failed!</h3>
         <center>
            <a href="../index.jsp">Return to Menu</a>
         </center>
      </c:when>
      <c:when test="${fpwdState == 'SUCCESS'}">
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

         <form method="post" action="index.jsp" name="pwdChange" onsubmit="return checkPassword();">

            <input type=hidden name="mode" value="forgotpassword4"/>
            <input type=hidden name="username" value="${param.username}"/>

            <table cellpadding="1" cellspacing="2" border="0" width="100%">
               <tr>
                  <td>Your challenge questions/answers have been validated</td>
               </tr>
            </table>
            
            <table cellpadding="1" cellspacing="2" border="0" width="100%">
               <tr><td colspan="2"><b>Change&nbsp;Password:</b></td></tr>
               <tr>
                  <td align=right>Id:</td>
                  <td align=left><b><c:out value="${param.username}"/></b></td>
               </tr>
               <tr>
                  <td align=right>Name:</td>
                  <td align=left>
                     <b><c:out value="${param.fname}"/>&nbsp;<c:out value="${param.lname}"/></b>
                  </td>
               </tr>
               <tr>
                  <td align=right>New Password:</td>
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
            <center>
               &nbsp;&nbsp;<a href="../index.jsp">Cancel</a>
            </center>
         </form>
      </c:when>
   </c:choose>
</c:if>