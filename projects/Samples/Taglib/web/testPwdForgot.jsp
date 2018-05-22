<%@include file="header_logic.jsp"%>

<h3>Forgot Password</h3>
<p>
   Note:  This is a static test of the Forgotten Password validation.
   This test supplies a static list of questions and answers based on the IDM Account Policy.
   This test assumes all of the questions defined in the policy will be answered.
</p>

<c:if test="${ptkError == 'false'}"> 

   <h4>Phase 1: BEGIN</h4>

   <ptk:setInput var="phase1"/>
   <ptk:setUniqueId input="phase1" value="sfehrman"/>
   <ptk:setProperty input="phase1" key="mode" value="questions"/>
   <ptk:doPasswordForgot connection="myconn" input="phase1" output="myoutput"/>

   <h4>Phase 1: END</h4>

   <c:if test="${ptkError == 'false'}">
      <p>
         The users forgotten password questions:&nbsp;
         <ptk:getValue name="forgottenPasswordQuestions" output="myoutput"/>
      </p>

      <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
      <ptk:getAttribute var="attrQ" output="myoutput" name="forgottenPasswordQuestions"/>

      <c:if test="${ptkError == 'false'}">

         <ptk:getValuesList attribute="attrQ" var="qList" sizevar="qSize"/>

         <p>The forgotten questions:</p>
         <table border=1>
            <c:forEach items="${qList}" var="qItem">
               <tr>
                  <td><c:out value="${qItem}"/></td>
               </tr>
            </c:forEach>
         </table>

         <h4>Phase 2: BEGIN</h4>

         <ptk:setInput var="phase2"/>
         <ptk:setUniqueId input="phase2" value="sfehrman"/>
         <ptk:setProperty input="phase2" key="mode" value="answers"/>
         <ptk:setAttribute input="phase2" var="attrQ"/>
         <ptk:setAttribute input="phase2" key="forgottenPasswordAnswers"/>
         <ptk:addValue input="phase2" attribute="forgottenPasswordAnswers" value="Smith"/>
         <ptk:addValue input="phase2" attribute="forgottenPasswordAnswers" value="Chicago"/>
         <ptk:addValue input="phase2" attribute="forgottenPasswordAnswers" value="1234"/>
         <ptk:doPasswordForgot connection="myconn" input="phase2" output="validation"/>

         <h4>Phase 2: END</h4>

         <c:if test="${ptkError == 'false'}">

            <p>Validation Information:</p>
            <table border=1>
               <tr>
                  <td align=right>Description:</td>
                  <td><ptk:getInformation type="description" element="validation"/></td>
               </tr>
               <tr>
                  <td align=right>State:</td>
                  <td><b><ptk:getInformation type="state" element="validation"/></b></td>
               </tr>
               <tr>
                  <td align=right>StateString:</td>
                  <td><b><ptk:getInformation type="statestring" element="validation"/></b></td>
               </tr>
               <tr>
                  <td align=right>Status:</td>
                  <td><ptk:getInformation type="status" element="validation"/></td>
               </tr>
               <tr>
                  <td align=right>Error:</td>
                  <td><ptk:getInformation type="error" element="validation"/></td>
               </tr>
            </table>

            <h4>Phase 3: BEGIN</h4>

            <ptk:setInput var="phase3"/>
            <ptk:setUniqueId input="phase3" value="sfehrman"/>
            <ptk:setProperty input="phase3" key="mode" value="change"/>
            <ptk:setAttribute input="phase3" key="password" value="Passw0rd"/>
            <ptk:doPasswordForgot connection="myconn" input="phase3" output="changed"/>
            
            <h4>Phase 3: END</h4>

         </c:if>
      </c:if>
   </c:if>
</c:if>

<hr>
<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>

<%@include file="footer_logic.jsp"%>
