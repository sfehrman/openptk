<!-- oper_detail.jsp -->

<c:if test="${ptkError == 'false'}">

    <ptk:setInput var="myinput"/>
    <ptk:setUniqueId  input="myinput" value="${uid}"/>
    <ptk:doRead connection="umlConn" input="myinput" output="myoutput"/>

    <c:if test="${ptkError == 'false'}">
        <table cellpadding="1" cellspacing="2" border="0" width="100%">
            <tr>
                <td align=left>Welcome <b><ptk:getValue output='myoutput' name="fullname"/></b></td>
            </tr>
        </table>
    </c:if>
</c:if>