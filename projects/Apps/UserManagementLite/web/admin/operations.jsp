<!-- operations.jsp -->

<%@include file="../common/header_start.jsp"%>
<%@include file="../common/header_logic.jsp"%>

<%@include file="../common/conn_get.jsp"%>

<!-- Create the OpenPTK Client Connection -->

<ptk:getConnection var="umlConn" properties="${openptkClientProps}" scope="session"/>

<ptk:getConnectionData connection="umlConn" name="type" var="usertype" />
<ptk:getConnectionData connection="umlConn" name="principal" var="userprincipal" />

<%@include file="../common/header_end.jsp"%>

   <c:if test='${usertype == "SYSTEM" }'>

      <table cellpadding="2" border="0" width="400">
         <tr>
            <td>
               <table cellpadding="0" cellspacing="0" border="1" width="400">
                  <tr>
                     <td>
                        <table cellpadding="2" cellspacing="2" border="0" width="100%">
                           <tr align=left valign="top">
                              <td colspan="2">
                                 <table class="menu" width="100%">
                                    <tr align=left valign="top">
                                       <td>
                                          <a href="?" title="Home page">Home</a>
                                          &nbsp;|&nbsp;
                                          <a href="?mode=find" title="Find an existing User">Find</a>
                                          &nbsp;|&nbsp;
                                          <a href="?mode=add" title="Create a new User">Create</a>
                                       </td>
                                    </tr>
                                 </table>
                                 <hr>
                              </td>
                           </tr>
                           <tr align=left valign=top>
                              <td width="99%" align=left>
                                 <table cellpadding="1" cellspacing="1" bgcolor="white" border="0" width="100%">
                                    <c:choose>

                                       <c:when test="${param.mode == 'find'}">
                                          <tr><td><%@include file="oper_find.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'list'}">
                                          <tr><td><%@include file="oper_list.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'detail'}">
                                          <tr><td><%@include file="oper_detail.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'update'}">
                                          <tr><td><%@include file="oper_update.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'delete'}">
                                          <tr><td><%@include file="oper_delete.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'add'}">
                                          <tr><td><%@include file="oper_add.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'create'}">
                                          <tr><td><%@include file="oper_create.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'pwdchange'}">
                                          <tr><td><%@include file="oper_pwdchange.jsp"%></td></tr>
                                       </c:when>

                                       <c:when test="${param.mode == 'pwdreset'}">
                                          <tr><td><%@include file="oper_pwdreset.jsp"%></td></tr>
                                       </c:when>

                                       <c:otherwise>
                                          <tr><td><%@include file="oper_welcome.jsp"%></td></tr>
                                       </c:otherwise>
                                    </c:choose>
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
   <c:if test='${usertype != "SYSTEM" }'>

      <c:redirect url="/anon/login.jsp?int=admin&loginmessage=true" />

      You must have a SYSTEM session to access this content!

   </c:if>



<%@include file="../common/footer_logic.jsp"%>
