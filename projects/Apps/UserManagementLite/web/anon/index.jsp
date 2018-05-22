<!-- anon/index.jsp --> 

<%@include file="../common/header_start.jsp"%>
<%@include file="../common/header_logic.jsp"%>
<%@include file="../common/header_end.jsp"%>

<table cellpadding="2" border="0" width="500">
   <tr>
      <td>
         <c:choose>
            <c:when test="${param.mode == 'menu'}">
               <%@include file="oper_menu.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'config'}">
               <%@include file="oper_config.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'register'}">
               <%@include file="oper_register.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'register2'}">
               <%@include file="oper_register2.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'create'}">
               <%@include file="oper_create.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotpassword'}">
               <%@include file="oper_forgotpassword.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotpassword2'}">
               <%@include file="oper_forgotpassword2.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotpassword3'}">
               <%@include file="oper_forgotpassword3.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotpassword4'}">
               <%@include file="oper_forgotpassword4.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotaccountid'}">
               <%@include file="oper_forgotaccountid.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'forgotaccountid2'}">
               <%@include file="oper_forgotaccountid2.jsp"%>
            </c:when>

            <c:otherwise>
               <%@include file="oper_menu.jsp"%>
            </c:otherwise>
         </c:choose>
      </td>
   </tr>
</table>

<%@include file="../common/footer_logic.jsp"%>
