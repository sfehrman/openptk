<!-- oper_forgotquestions2.jsp -->

<c:set var="counter" value="0"/>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="myinput"/>
   <ptk:setUniqueId input="myinput" value="${param.uid}"/>
   <ptk:setAttribute input="myinput" key="forgottenPasswordAnswers"/>
   <ptk:setAttribute input="myinput" key="forgottenPasswordQuestions"/>

   <!-- Set the values -->
   <c:forEach var="counter" begin="0" end="${param.num_answers - 1}" step="1">
      <c:set var="answerkey">answer.<c:out value="${counter}"/></c:set>
      <c:set var="questionkey">question.<c:out value="${counter}"/></c:set>

      <% pageContext.setAttribute("thisquestionval", (String) request.getParameter((String) pageContext.getAttribute("questionkey")));%>
      <ptk:addValue input="myinput" attribute="forgottenPasswordQuestions" value="${thisquestionval}"/>

      <% pageContext.setAttribute("thisanswerval", (String) request.getParameter((String) pageContext.getAttribute("answerkey")));%>
      <ptk:addValue input="myinput" attribute="forgottenPasswordAnswers" value="${thisanswerval}"/>
   </c:forEach>

   <c:if test="${ptkError == 'false'}">

      <ptk:doUpdate connection="umlConn" input="myinput" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">

         <table cellpadding="1" cellspacing="2" border="0" width="100%">
            <tr>
               <td colspan="2"><b>Set&nbsp;Forgotten&nbsp;Password&nbsp;Questions:</b></td>
            </tr>
            <tr>
               <td align=right width="50%">Id:</td>
               <td align=left>
                  <b><c:out value="${param.uid}"/></b>
               </td>
            </tr>
            <tr>
               <td align=right>Name:</td>
               <td align=left>
                  <b><c:out value="${param.fname}"/>&nbsp;<c:out value="${param.lname}"/></b>
               </td>
            </tr>
            <tr>
               <td colspan="2"><i>Your answers have been updated</i></td>
            </tr>
         </table>

      </c:if>

   </c:if>
</c:if>