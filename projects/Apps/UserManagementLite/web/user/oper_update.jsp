<!-- oper_update.jsp -->

<c:if test="${ptkError == 'false'}">

    <ptk:setInput var="myinput"/>
    <ptk:setUniqueId  input="myinput"                  value="${param.uid}"/>
    <ptk:setAttribute input="myinput" key="title"      value="${param.title}"/>
    <ptk:setAttribute input="myinput" key="email"      value="${param.email}"/>
    <ptk:setAttribute input="myinput" key="telephone"  value="${param.telephone}"/>
    <ptk:setAttribute input="myinput" key="manager"    value="${param.manager}"/>
    <ptk:setAttribute input="myinput" key="roles"/>

    <c:forEach var='inroles' items='${paramValues.roles}'>
        <ptk:addValue input="myinput" attribute="roles" value="${inroles}"/>
    </c:forEach>

    <ptk:doUpdate connection="umlConn" input="myinput" output="myoutput"/>

    <c:if test="${ptkError == 'false'}">
        <br>Update Successfull!<br>
        <%@include file="oper_detail.jsp"%>
    </c:if>
</c:if>