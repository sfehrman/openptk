<!-- oper_find.jsp -->

<script type="text/javascript">
   function checkSearch(){
      var searchInput = document.getElementById('findinput');
      if( searchInput.value == "" )
      {
         window.alert("Search value required.");
         return false;
      }
      else
      {
         return true;
      }
   }
</script>

<table cellpadding="2" cellspacing="2" border="0" width="100%">
   <tr>
      <td><b>Find User:</b></td>
   </tr>
   <tr>
      <td align="left">Enter search value: <i>(firstname, lastname, uniqueid)</i></td>
   </tr>
</table>

<form action="operations.jsp" name="searchForm" onsubmit="return checkSearch();">
   <input type=hidden name="mode" value="list"/>
   <center>
      <table cellpadding="1" cellspacing="1" border="0">
         <tr>
            <td>
               <table cellpadding="2" cellspacing="2" border="0">
                  <tr>
                     <td align="left">
                        <input type="text" size="18" align="left" id="findinput" name="findval"/>
                     </td>
                     <td align="left">
                        <input type="submit" value="Search"/>
                     </td>
                  </tr>
               </table>
            </td>
         </tr>
      </table>
   </center>
</form>
