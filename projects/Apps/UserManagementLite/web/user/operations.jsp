<!-- user/index.jsp -->

<%@include file="../common/header_start.jsp"%>
<%@include file="../common/header_logic.jsp"%>

<%@include file="../common/conn_get.jsp"%>

<!-- Create the OpenPTK Client Connection -->

<ptk:getConnection var="umlConn" properties="${openptkClientProps}" scope="session" />

<ptk:getConnectionData connection="umlConn" name="type" var="usertype" />
<ptk:getConnectionData connection="umlConn" name="principal" var="userprincipal" />

<%@include file="../common/header_end.jsp"%>

<c:if test='${usertype != null }'>
   <c:if test='${usertype == "USER" }'>

      <table cellpadding="2" border="0" width="400">
         <tr>
            <td>
               <table cellpadding="0" cellspacing="0" border="1" width="400">
                  <tr>
                     <td>
                        <table cellpadding="2" cellspacing="2" border="0" width="100%">
                           <tr align=left valign=top>
                              <td width="99%" align=left>
                                 <table cellpadding="1" cellspacing="1" bgcolor="white" border="0" width="100%">
                                    <c:choose>

                                       <c:when test="${param.mode == 'detail'}">
                                          <tr><td><%@include file="oper_detail.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'update'}">
                                          <tr><td><%@include file="oper_update.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'pwdchange'}">
                                          <tr><td><%@include file="oper_pwdchange.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'pwdreset'}">
                                          <tr><td><%@include file="oper_pwdreset.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'forgotquestions1'}">
                                          <tr><td><%@include file="oper_forgotquestions1.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'forgotquestions2'}">
                                          <tr><td><%@include file="oper_forgotquestions2.jsp"%></td></tr>
                                       </c:when>

                                       <c:otherwise>
                                          <tr><td><%@include file="oper_welcome.jsp"%></td></tr>
                                       </c:otherwise>
                                    </c:choose>
                                 </table>

                                 <table cellpadding="1" cellspacing="2" border="0" width="100%">
                                    <tr><td><hr></td></tr>
                                    <tr>
                                       <td>
                                          <c:if test="${userIntType != 'password' }">
                                             &nbsp;
                                             <a href="?uid=<c:out value='${uid}'/>&mode=detail"
                                                title="My Profile"><b>Edit&nbsp;Profile</b></a>
                                             &nbsp;|
                                          </c:if>
                                          &nbsp;
                                          <a href="?mode=pwdchange&uid=<c:out value='${uid}'/>"
                                             title="Change password for this user"><b>Change&nbsp;Password</b></a>
                                          &nbsp;|
                                          &nbsp;
                                          <a href="?uid=<c:out value='${uid}'/>&mode=forgotquestions1"
                                             title="Set forgot password questions"><b>Authen&nbsp;Questions</b></a>
                                       </td>
                                    </tr>
                                 </table>
                              </td>
                           </tr>
                        </table>
                     </td>
                  </tr>
               </table>
            </td>
         </tr>
      </table>
                                             
   </c:if>
</c:if>

<c:if test='${usertype != "USER" }'>
      <c:redirect url="/anon/login.jsp?int=user&loginmessage=true" />

      You must have a USER session to access this content!
      
</c:if>


<%@include file="../common/footer_logic.jsp"%>