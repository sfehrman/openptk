<%@include file="header_logic.jsp"%>


<ptk:setContext connection="myconn" value="Media-Embed-JDBC"/>

<h4>Current Context: <ptk:getContext connection="myconn"/></h4>

<h3>Connection Data</h3>

<!-- valid data names: ID, TYPE, PRINCIPAL, AUTHEN (not case sensitive) -->

<h4>test: (ptk:getConnectionData)</h4>

<table border="1" cellpadding="1" cellspacing="1">

<ptk:getConnectionData connection="myconn" name="ID" var="data" />

   <tr>
      <td align="right"><tt>ID</tt> </td>
      <td align="left"><b><c:out value="${data}"/></b></td>
   </tr>

<ptk:getConnectionData connection="myconn" name="type" var="data" />

   <tr>
      <td align="right"><tt>TYPE</tt> </td>
      <td align="left"><b><c:out value="${data}"/></b></td>
   </tr>

   <tr>
      <td align="right"><tt>PRINCIPAL</tt> </td>
      <td align="left"><b><ptk:getConnectionData connection="myconn" name="PRINCIPAL" /></b></td>
   </tr>

   <tr>
      <td align="right"><tt>AUTHEN</tt> </td>
      <td align="left"><b><ptk:getConnectionData connection="myconn" name="authen" /></b></td>
   </tr>

</table>

<h3>Connection Properties</h3>

<h4>test: (ptk:getConnectionPropertyNames)</h4>

<ptk:getConnectionPropertyNames connection="myconn" var="propList" sizevar="propSize" />

<h4>test: (ptk:getConnectionProperty)</h4>

Quantity: <c:out value="${propSize}"/>

<table border="1" cellpadding="1" cellspacing="1">
   <c:forEach items="${propList}" var="propItem">
      <tr>
         <td align="left">
            <tt><c:out value="${propItem}" /></tt>
         </td>
         <td align="left">
            <b><ptk:getConnectionProperty connection="myconn" name="${propItem}" /></b>
         </td>
      </tr>
   </c:forEach>
</table>

<h3>Context Test</h3>

<c:if test="${ptkError == 'false'}">

   <c:set scope="session" var="contextId">
      <ptk:getContext connection="myconn" />
   </c:set>

   <c:if test="${ptkError == 'false'}">

      <h4>test: (ptk:getContext)</h4>
      <table border="1">
         <tr>
            <td align=right>contextId:</td>
            <td align=left><b><c:out value="${contextId}"/></b></td>
         </tr>
      </table>

      <h4>test: (ptk:getContextsList)</h4>

      <ptk:getContextsList connection="myconn" var="ctxList" sizevar="resSize" />

      <c:if test="${ptkError == 'false'}">
         Results: getContextsList Successful, size = <c:out value="${resSize}"/>
         <ul>
            <c:forEach items="${ctxList}" var="ctxItem">
               <li><c:out value="${ctxItem}"/></li>
            </c:forEach>
         </ul>

         <ptk:setContext connection="myconn" value="Media-Embed-JDBC"/>

         <c:if test="${ptkError == 'false'}">
            <h4>test: (ptk:setContext)</h4>
            <c:set scope="session" var="contextId">
               <ptk:getContext connection="myconn" />
            </c:set>
            Current Context: <b><c:out value="${contextId}"/></b>
         </c:if>
      </c:if>
   </c:if>
</c:if>

<c:if test="${ptkError != 'false'}"> 
   <table border="1" >
      <tr><td>Results: getContext Failed</td></tr>
   </table>
</c:if>

<br><br>      
<table border="1">
   <tr><td>Error:</td><td><c:out value="${ptkError}"/></td></tr>
   <tr><td>Status:</td><td><c:out value="${ptkStatus}"/></td></tr>
</table> 

<%@include file="footer_logic.jsp"%>
