<!-- debug.jsp -->

<c:if test="${error == 'true'}">
   <div class=error>
      <hr>
      <ul>
         <li>
         <c:out value="${errorMsg}"/>
         <c:if test="${showDebug == 'on'}">
            ( <idm:getMetadata var="${errorComp}" type="status"/> )
         </c:if>
         </li>
      </ul>
      <hr>
   </div>
</c:if>

