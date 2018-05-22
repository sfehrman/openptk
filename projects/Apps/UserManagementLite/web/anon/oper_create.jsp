<!-- oper_create.jsp -->

<ptk:getConnection var="umlConn" properties="openptk_client" scope="session"/>
<ptk:setInput var="myinput"/> 
<ptk:setAttribute input="myinput" key="firstname" value="${param.fname}"/>    
<ptk:setAttribute input="myinput" key="lastname" value="${param.lname}"/>           
<ptk:setAttribute input="myinput" key="email" value="${param.email}"/> 
<ptk:doCreate connection="umlConn" input="myinput" output="myoutput"/>

<c:if test="${ptkError == 'false'}">

   <c:set var="accountId"><ptk:getUniqueId output="myoutput"/></c:set>

   <table cellpadding="3" cellspacing="3" border="0">
      <tr valign="top"><td><b>Account Created:</b></td></tr>
      <tr>
         <td align="right"><i>Account Id:</i></td>
         <td align="left"><b><c:out value="${accountId}"/></b></td>
      </tr>
      <tr valign="top">
         <td align="right"><i>First Name:</i></td>
         <td align="left"><b><c:out value="${param.fname}"/></b></td>
      </tr>
      <tr valign="top">
         <td align="right"><i>Last Name:</i></td>
         <td align="left"><b><c:out value="${param.lname}"/></b></td>
      </tr>
   </table>
   
   <br> 
   <br>
   <hr>
   <br>

   <a href="../anon/index.jsp?mode=menu&logout=true">Continue</a>

   <br>
   
   
</c:if>
