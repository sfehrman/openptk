<!-- footer_logic.jsp -->

<div class="header_bottom">
   <table cellspacing=0 cellpadding=0 width=100% height=38  background="../images/header_gradient_bottom.gif">
      <tr>
         <td valign=middle align="left"> </td>
         <td nowrap="nowrap" align="right" valign="middle"> </td>
         <td valign=middle align="right" width=78>  </td>
         <td valign=middle align="right" width=78>
            <a href="http://www.openptk.org/">
               <img height=38 src="../images/OpenPTK.png" alt="OpenPTK">
            </a>
         </td>
      </tr>
   </table>
</div>

<c:if test="${ptkError == 'true'}">
      <table>
         <tr><td class="error">Error: <c:out value="${ptkStatus}"/></td></tr>
      </table>
      <c:set var="ptkError" value="false"/>
</c:if>

<c:if test="${showDebug == 'on'}">
   <%@include file="dump.jsp"%>  
</c:if>

</body>
</html>
