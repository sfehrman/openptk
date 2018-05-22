<%@include file="header_logic.jsp"%>

<h3>Search</h3>

<c:if test="${ptkError == 'false'}">  
   
   <c:if test="${ptkError == 'false'}">
      
      <!-- query var should only be needed for compound queries -->

      <ptk:setInput var="myinput"/> 
      <ptk:setQuery input="myinput" name="search" value="john and" type="NULL" var="myquery"/>
      <ptk:doSearch connection="myconn" input="myinput" output="myoutput"/>
      
      <c:if test="${ptkError == 'false'}">
         
         <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
         
         <c:if test="${ptkError == 'false'}"> 
            <table border="1" >
               <tr><td>Results: Search Successful</td><td></td><td></td></tr>                     
               <tr><td>Results: <c:out value="${resSize}"/></td><td></td><td></td></tr>
               <tr><td>Name:</td><td>Value:</td><td>Type:</td></tr>
               <c:forEach items="${resList}" var="resItem">
                  
                  <ptk:getAttributesList var="attrList" result="resItem"/>
                  
                  <c:if test="${ptkError == 'false'}">
                     
                     <c:forEach items="${attrList}" var="attrItem">
                        <tr>
                           <td><ptk:getName attribute="attrItem"/></td>
                           <td><ptk:getValue attribute="attrItem"/></td>
                           <td><ptk:getType attribute="attrItem"/></td>
                        </tr>
                     </c:forEach>
                     <tr><td>&nbsp;</td></tr> 
                  </c:if>
                  
               </c:forEach>
               
            </table>
         </c:if>
         
      </c:if>      
      
   </c:if>     
</c:if>

<c:if test="${ptkError != 'false'}"> 
   <table border="1" >
      <tr><td>Results: Search Failed</td></tr>
   </table>
   
</c:if>     

<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>      

<br><br>

<%@include file="footer_logic.jsp"%>
