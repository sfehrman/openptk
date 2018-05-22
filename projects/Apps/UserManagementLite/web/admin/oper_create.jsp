<!-- oper_create.jsp -->

<ptk:setInput var="myinput"/> 
<ptk:setAttribute input="myinput" key="firstname" value="${param.fname}"/>    
<ptk:setAttribute input="myinput" key="lastname" value="${param.lname}"/>           
<ptk:setAttribute input="myinput" key="email" value="${param.email}"/> 
<ptk:doCreate connection="umlConn" input="myinput" output="myoutput"/>

<c:if test="${ptkError == 'false'}">
   <table>
      <tr><td class="result">Create Successful!</td></tr>
   </table>
   
   <hr>
   
   <table cellpadding="3" cellspacing="3" border="0">
      <tr valign="top"><td><b>User Created:</b></td></tr>
      <tr valign="top">
         <td align="right"><i>Account Id:</i></td>
         <td align="left"><b><ptk:getUniqueId output="myoutput"/></b></td>
      </tr>
      <tr valign="top">
         <td align="right"><i>First:</i></td>
         <td align="left"><b><c:out value="${param.fname}"/></b></td>
      </tr>
      <tr valign="top">
         <td align="right"><i>Last:</i></td>
         <td align="left"><b><c:out value="${param.lname}"/></b></td>
      </tr>
   </table>
   
</c:if>


