<%@include file="header_logic.jsp"%>

<h3>Forgot Password: Phase 1</h3>
<p>
   <b>Note:</b>  This is a dynamic test of the Forgotten Password validation.
   This test allows the IDM Account Policy to supply less than all of the
   questions defined in the policy.  The questions returned by the policy
   must be answered interactively to test this feature.
</p>

<c:set var="counter" value="0"/>
<c:set var="username" value="sfehrman"/>

<c:if test="${ptkError == 'false'}">

   <h4>Phase1: BEGIN</h4>

   <ptk:setInput var="phase1"/>
   <ptk:setUniqueId input="phase1" value="${username}"/>
   <ptk:setProperty input="phase1" key="mode" value="questions"/>
   <ptk:doPasswordForgot connection="myconn" input="phase1" output="myoutput"/>

   <h4>Phase1: END</h4>

   <c:if test="${ptkError == 'false'}">
      <p>
         The users forgotten password questions:&nbsp;
         <ptk:getValue name="forgottenPasswordQuestions" output="myoutput"/>
      </p>

      <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
      <ptk:getAttribute var="attrQ" output="myoutput" name="forgottenPasswordQuestions"/>

      <c:if test="${ptkError == 'false'}">

         <ptk:getValuesList attribute="attrQ" var="qList" sizevar="qSize"/>

         <form action='testPwdForgotP2.jsp' method="post">
            <input type=hidden name="num_answers" value="${qSize}"/>
            <input type=hidden name="username" value="${username}"/>
            <table>
               <c:forEach items="${qList}" var="qItem">
                  <c:set var="answer">
                     <c:out value="answer."/><c:out value="${counter}"/>
                  </c:set>
                  <tr>
                     <td><c:out value="${qItem}"/></td>
                     <td><input type="password" name='<c:out value="${answer}"/>' size="24" align="left"/></td>
                  </tr>
                  <c:set var="counter" value="${counter+1}"/>
               </c:forEach>
            </table>
            <p><input type='submit' value='Validate Answers'/>
         </form>

      </c:if>
   </c:if>
</c:if>

<hr>
<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>

<%@include file="footer_logic.jsp"%>