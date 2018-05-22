<!-- Setup Defaults -->

<c:set scope="session" var="openptkClientProps" value="openptk_client"/>

<c:choose>
   <c:when test='${param.clientProps != null}'>
      <c:set scope="session" var="openptkClientProps" value="${param.clientProps}"/>
   </c:when>
   <c:when test='${openptkClientProps == null}'>
      <c:set scope="session" var="openptkClientProps" value="${openptkClientProps}"/>
   </c:when>
</c:choose>

<!-- Create the OpenPTK Client Connection -->

<ptk:getConnection var="registerConn" properties="${openptkClientProps}" scope="session"/>
