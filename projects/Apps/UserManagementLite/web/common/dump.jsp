<!-- dump.jsp -->

<table class=debug width="100%">
<tr><td>    
<pre>
   <idm:debug var="${dump}"/>
</pre>
<table>
<tr><td>Session Type:</td><td><ptk:getConnectionData connection="umlConn" name="type" var="usertype" /></td></tr>
<tr><td>UserID:</td><td><ptk:getConnectionData connection="umlConn" name="principal" var="userprincipal" /></td></tr>
</table>
</td></tr>
</table>