<!-- oper_list.jsp -->

<c:set var="listval" value="${param.findval}"/>

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="myinput"/>

   <c:if test="${not empty listval}">
      <ptk:setQuery input="myinput" name="search" value="${listval}" type="NOOPERATOR" var="myquery"/>
   </c:if>

   <ptk:doSearch connection="umlConn" input="myinput" output="myoutput"/>

   <c:set var="dump" value="search"/>

   <c:if test="${ptkError == 'false'}">

      <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />

      <c:if test="${ptkError == 'false'}"> 

         <table cellpadding="2" cellspacing="2" border="0" width="100%">  
            <tr>
               <td colspan="2"><b>Found: <c:out value="${resSize}"/></b></td>
            </tr>
            <tr class="list-user-header">
               <td><i>Name: (Last,&nbsp;First)</i></td>
               <td><i>Email:</i></td>
            </tr>

            <c:forEach items="${resList}" var="resItem">

               <ptk:getAttributesList var="attrList" result="resItem"/>

               <c:if test="${ptkError == 'false'}">

                  <tr class="list-user-detail">
                     <td>
                        <a href="?mode=detail&uid=<ptk:getUniqueId result='resItem'/>">
                           <ptk:getValue result="resItem" name="lastcommafirst"/>
                        </a>
                     </td>
                     <td>
                  <ptk:getValue result="resItem" name="email"/>
                  </td>
                  </tr>

               </c:if>
            </c:forEach>
         </table>
      </c:if>
   </c:if>

   <c:if test="${ptkError == 'true'}">
      <p>
         <b>Error:</b><br/>
         Please ensure that you supply a search value.
         Some Services prevent searches without a query.
      </p>
   </c:if>

</c:if>     