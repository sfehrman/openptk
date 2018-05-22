<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- oper_forgotquestions1.jsp -->

<c:set var="qCnt" value="0"/>
<c:set var="aCnt" value="0"/>

<c:if test="${ptkError == 'false'}">

    <ptk:setInput var="myinput"/>
    <ptk:setUniqueId input="myinput" value="${param.uid}"/>
    <ptk:doRead connection="umlConn" input="myinput" output="myoutput"/>

    <c:if test="${ptkError == 'false'}">

        <ptk:getAttribute var="attrquestions" output="myoutput" name="forgottenPasswordQuestions"/>
        <ptk:getAttribute var="attranswers" output="myoutput" name="forgottenPasswordAnswers"/>

        <c:if test="${ptkError == 'false'}">

            <ptk:getValuesList attribute="attrquestions" var="qList" sizevar="qSize"/>
            <ptk:getValuesList attribute="attranswers" var="aList" sizevar="aSize"/>

            <c:if test="${ptkError == 'false'}">

                <form method="post" action="operations.jsp" name="forgotQuestions">
                    <input type=hidden name="mode" value="forgotquestions2"/>
                    <input type=hidden name="uid" value="<c:out value='${param.uid}'/>"/>
                    <input type=hidden name="fname" value="<ptk:getValue output='myoutput' name='firstname'/>">
                    <input type=hidden name="lname" value="<ptk:getValue output='myoutput' name='lastname'/>"/>
                    <input type=hidden name="num_answers" value="${qSize}"/>

                    <table cellpadding="1" cellspacing="2" border="0" width="100%">
                        <tr><td colspan="2"><b>Set&nbsp;Forgotten&nbsp;Password&nbsp;Questions:</b></td></tr>
                        <tr>
                            <td align=right width="50%">Id:</td>
                            <td align=left>
                                <b><c:out value="${param.uid}"/></b>
                            </td>
                        </tr>
                        <tr>
                            <td align=right>Name:</td>
                            <td align=left>
                                <b><ptk:getValue output='myoutput' name='firstname'/>&nbsp;
                                <ptk:getValue output='myoutput' name='lastname'/></b>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <table border="0" width="100%">
                                    <c:forEach items="${qList}" var="qItem">
                                        <c:set var="question">
                                            <c:out value="question."/><c:out value="${qCnt}"/>
                                        </c:set>
                                        <input type="hidden" name="${question}" value="${qItem}"/>
                                        <tr valign="bottom">
                                            <td align="right"><c:out value="${qItem}"/>:</td>
                                        </tr>
                                        <c:set var="qCnt" value="${qCnt+1}"/>

                                    </c:forEach>
                                </table>
                            </td>
                            <td>
                        <c:choose>
                           <c:when test="${aSize == '0'}">
                                <table border="0" width="100%">
                                    <c:forEach items="${qList}" var="qItem">
                                        <c:set var="answer">
                                            <c:out value="answer."/><c:out value="${aCnt}"/>
                                        </c:set>
                                        <tr>
                                            <td align="left">
                                                <input TYPE="password" name='<c:out value="${answer}"/>' size="24" align="left"/>
                                            </td>
                                        </tr>
                                        <c:set var="aCnt" value="${aCnt+1}"/>
                                    </c:forEach>
                                </table>
                           </c:when>
                           <c:otherwise>
                                <table border="0" width="100%">
                                    <c:forEach items="${aList}" var="aItem">
                                        <c:set var="answer">
                                            <c:out value="answer."/><c:out value="${aCnt}"/>
                                        </c:set>
                                        <tr>
                                            <td align="left">
                                                <input TYPE="password" name='<c:out value="${answer}"/>' value="${aItem}" size="24" align="left"/>
                                            </td>
                                        </tr>
                                        <c:set var="aCnt" value="${aCnt+1}"/>
                                    </c:forEach>
                                </table>
                           </c:otherwise>
                        </c:choose>
                            </td>
                        </tr>

                        <tr>
                            <td></td>
                            <td align=left><input type="submit" value="Update"/></td>
                        </tr>

                    </table>
                </form>
            </c:if>
        </c:if>
    </c:if>
</c:if>
