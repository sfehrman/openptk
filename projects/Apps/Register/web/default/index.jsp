<!-- index.jsp --> 

<%@include file="../common/header.jsp"%>

<table class="middle">
   <tr>
      <td>
         <c:choose>
            <c:when test="${param.mode == 'welcome'}">
               <%@include file="welcome.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'data'}">
               <%@include file="data.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'terms'}">
               <%@include file="terms.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'confirm'}">
               <%@include file="confirm.jsp"%>
            </c:when>

            <c:when test="${param.mode == 'create'}">
               <%@include file="create.jsp"%>
            </c:when>

            <c:otherwise>
               <%@include file="about.jsp"%>
            </c:otherwise>
         </c:choose>
      </td>
   </tr>
</table>

<%@include file="../common/footer.jsp"%>
