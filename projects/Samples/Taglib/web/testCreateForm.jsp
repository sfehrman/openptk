<%@include file="header_logic.jsp"%>

<form action='testCreate.jsp'>	
   <table>
      <tr>
         <td>First Name:</td>
         <td><input type='text' name='firstName'/></td>
      </tr>
      <tr>
         <td>Last Name:</td>
         <td><input type='text' name='lastName'/></td>
      </tr>
      <tr>
         <td>
            Select Roles that you have worked with: 
         </td>
         <td>
            <select name='roles' size='3' 
                    multiple='true'>
               <option value='Business'>Business</option>
               <option value='Admin'>Admin</option>
               <option value='Customer'>Customer</option>
            </select>
         </td>
      </tr>
   </table>
   <p><input type='submit' value='Create User'/>
</form>    
<br><br> 

<%@include file="footer_logic.jsp"%>