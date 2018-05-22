<%@include file="header_logic.jsp"%>

<h3>Read User</h3>

<c:if test="${ptkError == 'false'}">

   <c:if test="${ptkError == 'false'}">
   
      <ptk:setInput var="myinput"/> 
      <ptk:setUniqueId input="myinput" value="tuser"/>
      <ptk:doRead connection="myconn" input="myinput" output="myoutput"/>
      
      <c:if test="${ptkError == 'false'}">
      
         <h4>test: (ptk:getValue)</h4>
         <table border="1">
            <tr>
               <td align=right>First:</td>
               <td align=left><b><ptk:getValue output='myoutput' name="firstname"/></b></td>
            </tr>
            <tr>
               <td align=right>Last:</td>
               <td align=left><b><ptk:getValue output='myoutput' name="lastname"/></b></td>
            </tr>
            <tr>
               <td align=right>Fullname:</td>
               <td align=left><b><ptk:getValue output='myoutput' name="fullname"/></b></td>
            </tr>
         </table>
         
         <h4>test: (ptk:getResultsList)</h4>
         
         <ptk:getResultsList var="resList" output="myoutput" sizevar="resSize" />
         
         <c:if test="${ptkError == 'false'}"> 
            <table border="1" >
               <tr><td>Results: Read Successful</td><td></td><td></td></tr>                       
               <tr><td>Results: <c:out value="${resSize}"/></td><td></td><td></td></tr>
               <tr><td>Name:</td><td>Value:</td><td>Type:</td></tr>
               <c:forEach items="${resList}" var="resItem">
               
                  <ptk:getAttributesList var="attrList" result="resItem"/>
                  
                  <c:if test="${ptkError == 'false'}">
                     
                     <c:forEach items="${attrList}" var="attrItem">
                        <tr>
                           <td><ptk:getName attribute="attrItem"/></td>
                           <td>
                              <ptk:getValuesList attribute="attrItem" var="valueList" sizevar="valueSize"/>   
                              <c:forEach items="${valueList}" var="value">
                              ${value} <br>
                              </c:forEach>
                           </td>
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
      <tr><td>Results: Read Failed</td></tr>
   </table>
   
</c:if>

<br><br>      
<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table> 

<%@include file="footer_logic.jsp"%>
