<%@include file="header_logic.jsp"%>

<h3>Change Password: eapi</h3>

<c:if test="${ptkError == 'false'}">
   
   <c:if test="${ptkError == 'false'}">
      
      <ptk:setInput var="myinput"/> 
      <ptk:setUniqueId input="myinput" value="tuser"/>
      <ptk:setAttribute input="myinput" key="password" value="Sun123abc"/>
      <ptk:doPasswordChange connection="myconn" input="myinput" output="myoutput"/>
      
   </c:if>
   
</c:if>

<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>

<%@include file="footer_logic.jsp"%>