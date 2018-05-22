<!-- footer.jsp -->

<!-- footer table for the entire app -->
<table class="footer">
   <c:if test="${ptkError == 'true'}">
      <tr><td class="error">Error: <c:out value="${ptkStatus}"/></td></tr>
      <c:set var="ptkError" value="false"/>
   </c:if>
</table>

<!-- outer application table -->
</div>
</div>
</td></tr></table>
</body>
</html>
