<!-- operations.jsp -->

<%@include file="../common/header_start.jsp"%>
<%@include file="../common/header_logic.jsp"%>
<%@include file="../common/header_end.jsp"%>
<%@include file="../common/conn_get.jsp"%>

<c:if test="${umlConn != null}">
   <!-- Close the existing OpenPTK Client Connection to force a logout -->
   <ptk:closeConnection connection="umlConn" scope="session"/>
</c:if>

<!-- Create the OpenPTK Client Connection -->
<ptk:getConnection var="umlConn" properties="${openptkClientProps}" scope="session" forcenew="true" />

<ptk:getConnectionData connection="umlConn" name="type" var="usertype" />
<ptk:getConnectionData connection="umlConn" name="principal" var="userprincipal" />


<c:redirect url="operations.jsp" />