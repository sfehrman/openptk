<!-- oper_menu.jsp -->


<c:if test="${param.logout == 'true'}">
    <%@include file="../common/conn_close.jsp"%>
   <c:set scope="session" var="ptkError" value=""/>
   <h4>You have been logged out</h4>
</c:if>

<center>
   <table cellspacing="10" width="80%">
      <tr>
         <td>
            <table border="1" width="100%">
               <tr>
                  <td width="100">
                     <a href="../admin">
                        <center><img src="../images/thumbUML.jpg" alt="UML" border=0></center>
                     </a>
                  </td>
                  <td>
                     &nbsp;&nbsp;<a href="../admin">User Administration</a>
                     <ul>
                        <li>Admin Interface</li>
                        <li>Create, Edit, Delete</li>
                        <li>Change, Reset Password</li>
                     </ul>
                  </td>
               </tr>
            </table>
         </td>
      </tr>
      <tr>
         <td>
            <table border="1" width="100%">
               <tr>
                  <td width="100">
                     <a href="../user">
                        <center><img src="../images/thumbUML.jpg" alt="UML" border=0></center>
                     </a>
                  </td>
                  <td>
                     &nbsp;&nbsp;<a href="../user">Self Service</a>
                     <ul>
                        <li>End User Interface</li>
                        <li>Edit Profile</li>
                        <li>Change password</li>
                     </ul>
                  </td>
               </tr>
            </table>
         </td>
      </tr>
   </table>
</center>   
