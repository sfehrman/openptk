<%@include file="header_logic.jsp"%>

<h3>Reset Password</h3>

<c:if test="${ptkError == 'false'}">
   
   <c:if test="${ptkError == 'false'}">
      
      <ptk:setInput var="myinput"/>
      <ptk:setUniqueId input="myinput" value="tuser"/>
      <ptk:doPasswordReset connection="myconn" input="myinput" output="myoutput"/>
      
      <c:if test="${ptkError == 'false'}">
         
         <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
         
         <c:if test="${ptkError == 'false'}"> 
            <table border="1" >
               <tr><td>Results: <c:out value="${resSize}"/></td><td></td><td></td><td></td></tr>
               <tr><td>Resource:</td><td>Name:</td><td>Value:</td><td>Type:</td></tr>
               <c:forEach items="${resList}" var="resItem">
                  <ptk:getUniqueId var="myuid" result="resItem"/>
                  <ptk:getAttributesList var="attrList" result="resItem"/>
                  
                  <c:if test="${ptkError == 'false'}">
                     
                     <c:forEach items="${attrList}" var="attrItem">
                        <tr>
                           <td><c:out value="${myuid}"/></td>
                           <td><ptk:getName attribute="attrItem"/></td>
                           <td><ptk:getValue attribute="attrItem"/></td>
                           <td><ptk:getType attribute="attrItem"/></td>
                        </tr>
                     </c:forEach>
                     
                  </c:if>
                  
               </c:forEach>
               
            </table>
         </c:if>
         
      </c:if>
      
   </c:if>
   
</c:if>

<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>

<%@include file="footer_logic.jsp"%>