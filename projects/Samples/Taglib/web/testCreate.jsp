<%@include file="header_logic.jsp"%>

<h3>Create User</h3>

<c:if test="${ptkError == 'false'}">
      
   <c:if test="${ptkError == 'false'}">
                  
      <ptk:setInput var="myinput"/> 
      <ptk:setAttribute input="myinput" key="firstname" value="Taglib"/>
      <ptk:setAttribute input="myinput" key="lastname" value="User"/>
      <ptk:doCreate connection="myconn" input="myinput" output="myoutput"/>
      
      <c:if test="${ptkError == 'false'}"> 
         <table border="1" >
            <tr><td>Results: Create Successful</td></tr>
         </table>
      </c:if>
      
      <c:if test="${ptkError != 'false'}"> 
         <table border="1" >
            <tr><td>Results: Create Failed</td></tr>
         </table>
      </c:if>
      
   </c:if>     
</c:if>   

<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table>  


<%@include file="footer_logic.jsp"%>
