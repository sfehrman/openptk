<!-- anon/login.jsp -->

<%@include file="../common/header_start.jsp"%>
<%@include file="../common/header_end.jsp"%>
  
<c:if test="${param.loginmessage == 'true'}">
   <table>
      <tr>
         <td>
            <font color=red>
               Please enter a valid username and password
            </font>
         </td>
      </tr>
   </table>
</c:if> 

<table cellpadding="2" border="0" width="400">
   <tr>
      <td>

         <form method="POST" action="../<c:out value='${param.int}'/>/index.jsp">
            <c:if test="${param.inttype == 'password'}">
               <input type=hidden name="inttype" value="password"/>
            </c:if>
            <c:if test="${param.inttype == 'enduser'}">
               <input type=hidden name="inttype" value="enduser"/>
            </c:if>
            <input type=hidden name="login" value="true"/>
            <input type=hidden name="int" value="<c:out value='${param.int}'/>"/>
            <table cellpadding="1" cellspacing="2" border="0" width="100%">
               <tr><td colspan="3"><b>Login</b></td></tr>
               <tr>
                  <td align="right">User Id:</td>
                  <td align="left">
                     <input type="text" size="18" align="left" name="loginid"/>
                  </td>
                  <td align=left>
                  </td>
               </tr>
               <tr>
                  <td align="right">Password:</td>
                  <td align="left">
                     <input type="password" size="18" align="left" name="password"/>
                  </td>
                  <td align=left>
                     <input type="submit" value="Login"/>
                  </td>
               </tr>
            </table>
         </form>


      </td>
   </tr>
</table>

<br>

<table>
   <tr>
      <td>
         &nbsp;[&nbsp;
         <font color=red><a href="index.jsp?mode=<c:out value="${registermode}"/>">New Account</a></font>
         &nbsp;|&nbsp;
         <font color=red><a href="index.jsp?mode=forgotpassword">Forgot Password</a></font>
         &nbsp;|&nbsp;
         <font color=red><a href="index.jsp?mode=forgotaccountid">Forgot User Id</a></font>
         &nbsp;]&nbsp;
      </td>
   </tr>
</table>
<br>
<br>

<%@include file="../common/footer_logic.jsp"%>
