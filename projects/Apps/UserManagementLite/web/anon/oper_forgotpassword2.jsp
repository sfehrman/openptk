<!-- oper_forgotpassword2.jsp -->

<ptk:getConnection var="umlConn" properties="openptk_client" scope="session"/>

<c:set var="counter" value="0"/>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="readinput"/>
   <ptk:setUniqueId input="readinput" value="${param.username}"/>
   <ptk:doRead connection="umlConn" input="readinput" output="readoutput"/>

   <c:if test="${ptkError == 'false'}">

      <ptk:setInput var="phase1"/>
      <ptk:setUniqueId input="phase1" value="${param.username}"/>
      <ptk:setProperty input="phase1" key="mode" value="questions"/>
      <ptk:doPasswordForgot connection="umlConn" input="phase1" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">

         <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
         <ptk:getAttribute var="attrQ" output="myoutput" name="forgottenPasswordQuestions"/>

         <c:if test="${ptkError == 'false'}">

            <ptk:getValuesList attribute="attrQ" var="qList" sizevar="qSize"/>

            <form action="index.jsp" method="post">
               <input type=hidden name="mode" value="forgotpassword3"/>
               <input type=hidden name="username" value="${param.username}"/>
               <input type=hidden name="fname" value="<ptk:getValue output='readoutput' name='firstname'/>">
               <input type=hidden name="lname" value="<ptk:getValue output='readoutput' name='lastname'/>"/>
               <input type=hidden name="num_answers" value="${qSize}"/>

               <table cellpadding="1" cellspacing="2" border="0" width="100%">
                  <tr><td colspan="2"><b><i>Please answer the following ${qSize} questions:</i></b></td></tr>

                  <tr>
                     <td align="right">Account Id:&nbsp;</td>
                     <td align="left">
                  <c:out value="${param.username}"/>&nbsp;<i>(<ptk:getValue output='readoutput' name='firstname'/>&nbsp;<ptk:getValue output='readoutput' name='lastname'/>)</i>
                  </td>
                  </tr>

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

                  <tr>
                     <td></td>
                     <td align=left><input type="submit" name="validate" value="Validate"/></td>
                  </tr>
               </table>
            </form>

         </c:if>
      </c:if>
   </c:if>
</c:if>
<center>
   &nbsp;&nbsp;<a href="../anon/index.jsp?mode=menu&logout=true">Return to Menu</a>
</center>