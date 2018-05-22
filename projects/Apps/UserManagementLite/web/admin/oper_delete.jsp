<!-- oper_delete.jsp -->

<ptk:setInput var="myinput"/> 
<ptk:setUniqueId input="myinput" value="${param.uid}"/> 
<ptk:doDelete connection="umlConn" input="myinput" output="myoutput"/>

<c:if test="${ptkError == 'false'}">
   <table cellpadding="1" cellspacing="2" border="0" width="100%">
      <tr><td colspan="2"><b>User Successfully Deleted:</b></td></tr>
      <tr><td align=right><i>UniqueId:</i></td><td><b><c:out value="${param.uid}"/></b></td></tr>
   </table>
</c:if>

