<!-- oper_pwdreset.jsp -->

<c:if test="${ptkError == 'false'}">

   <ptk:setInput var="readInput"/> 
   <ptk:setUniqueId input="readInput" value="${param.uid}"/>      
   <ptk:setAttribute input="readInput" key="firstname"/>    
   <ptk:setAttribute input="readInput" key="lastname"/>  
   <ptk:doRead connection="umlConn" input="readInput" output="readOutput"/>
   
   <c:if test="${ptkError == 'false'}">
      
      <ptk:setInput var="resetInput"/> 
      <ptk:setUniqueId input="resetInput" value="${param.uid}"/>         
      
      <ptk:doPasswordReset connection="umlConn" input="resetInput" output="resetOutput"/>
      
      <c:if test="${ptkError == 'false'}">
         <table cellpadding="1" cellspacing="2" border="0" width="100%">
            <tr><td colspan="2"><b>Password has been reset for:</b></td></tr>
            <tr>
               <td align=right>Id:</td>
               <td align=left><b>${param.uid}</b></td>
            </tr>
            <tr>
               <td align=right>Name:</td>
               <td align=left>
                  <b><ptk:getValue output='readOutput' name='firstname'/>&nbsp;
                  <ptk:getValue output='readOutput' name='lastname'/></b>
               </td>
            </tr>
         </table>
         
         <ptk:getResultsList var="resList" output="resetOutput" sizevar="resSize" />
         
         <c:if test="${ptkError == 'false'}">
            
            Resources Reset: ${resSize} <br>
            
            <table cellpadding="2" cellspacing="2" border="0" width="100%">  
               
               <tr class="list-user-header"> 
                  <td><i>Resource:</i></td> 
                  <td><i>Password:</i></td> 
               </tr> 
               
               <c:forEach items="${resList}" var="resource"> 
                  <ptk:getUniqueId var="resourceName" result="resource" /> 
                  
                  <tr class="list-user-detail"> 
                     <td> 
                        ${resourceName}
                     </td>
                     <td>
                        <ptk:getValue result="resource" name="password"/> 
                     </td> 
                  </tr>         
               </c:forEach> 
               
            </table>  
         </c:if>
      </c:if>
   </c:if>
</c:if>