<!-- oper_forgotaccountid2.jsp -->

<ptk:getConnection var="umlConn" properties="openptk_client" scope="session"/>

<ptk:setInput var="myinput"/>
<ptk:setQuery input="myinput" name="search" value="${param.email}" type="NOOPERATOR" var="myquery"/>
<ptk:doSearch connection="umlConn" input="myinput" output="myoutput"/>

<c:if test="${ptkError == 'false'}">

   <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />

   <c:if test="${resSize != '1'}">
      <p>
         <b>Sorry:</b>
         <br/>
         A unique Account Id was not found for email address: ${param.email}
      </p>
   </c:if>

   <c:if test="${resSize == '1'}">
      <c:forEach items="${resList}" var="resItem">

         <ptk:getAttributesList var="attrList" result="resItem"/>

         <c:if test="${ptkError == 'false'}">
            <table>
               <tr>
                  <td align="right"><i>Email Address:</i></td>
                  <td align="left"><b>${param.email}</b></td>
               </tr>
               <tr>
                  <td align="right"><i>User Id:</i></td>
                  <td align="left"><b><ptk:getUniqueId result='resItem'/></b></td>
               </tr>
            </table>
         </c:if>

      </c:forEach>
   </c:if>

</c:if>

<c:if test="${ptkError == 'true'}">
   <p>
      <b>Sorry:</b>
      <br/>
      Could not find a record matching the email address: ${param.email}
   </p>
</c:if>

<br>
&nbsp;&nbsp;<a href="../index.jsp">Return to Menu</a>
<br>