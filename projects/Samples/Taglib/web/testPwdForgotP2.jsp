<%@include file="header_logic.jsp"%>

<h3>Forgot Password: Phase 2</h3>

<c:if test="${ptkError == 'false'}">

   <c:set var="counter" value="0"/>

   <ptk:setInput var="phase2"/>
   <ptk:setUniqueId input="phase2" value="${param.username}"/>
   <ptk:setProperty input="phase2" key="mode" value="answers"/>
   <ptk:setAttribute input="phase2" var="attrQ"/>
   <ptk:setAttribute input="phase2" key="forgottenPasswordAnswers"/>

   <c:forEach var="counter" begin="0" end="${param.num_answers - 1}" step="1">
      <c:set var="answerkey">
         answer.<c:out value="${counter}"/>
      </c:set>

      <% pageContext.setAttribute("thisanswerval", (String) request.getParameter((String) pageContext.getAttribute("answerkey"))); %>

      <!-- ptk:setProperty input="phase2" key="${counter}" value="${thisanswerval}"/ -->

      <ptk:addValue input="phase2" attribute="forgottenPasswordAnswers" value="${thisanswerval}"/>

   </c:forEach>

   <ptk:doPasswordForgot connection="myconn" input="phase2" output="validation"/>

   <c:set var="fpwdState">
      <ptk:getInformation type="state" element="validation"/>
   </c:set>

   <c:choose>
      <c:when test="${fpwdState != 'OK'}">
         <h3>Authentication Question/Answers Failed!</h3>
         <br>State:  <c:out value="${fpwdState}"/>
      </c:when>
      <c:when test="${fpwdState == 'OK'}">
         <h3>Forgotten Password Answers Validated ... <i>changing your Password</i></h3>
         <br>State:  <c:out value="${fpwdState}"/>

         <ptk:setInput var="phase3"/>
         <ptk:setUniqueId input="phase3" value="tuser"/>
         <ptk:setProperty input="phase3" key="mode" value="change"/>
         <ptk:setAttribute input="phase3" key="password" value="Passw0rd"/>
         <ptk:doPasswordForgot connection="myconn" input="phase3" output="changed"/>

      </c:when>
   </c:choose>

</c:if>

<hr>
<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>

<%@include file="footer_logic.jsp"%>
