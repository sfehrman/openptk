<!-- create.jsp -->

<!-- Create the OpenPTK Client Connection -->

<%@include file="../common/conn_get.jsp"%>

<form name="create" action="">
   <input type=hidden name="mode" value="welcome"/>
   <table class="content">
      <tr>
         <td>
            <p>
               Request for <b>${param.fname}&nbsp;${param.lname}</b> has been submitted:
            </p>

      <ptk:setInput var="myinput"/>
      <ptk:setAttribute input="myinput" key="firstname" value="${param.fname}"/>
      <ptk:setAttribute input="myinput" key="lastname" value="${param.lname}"/>
      <ptk:setAttribute input="myinput" key="email" value="${param.email}"/>
      <ptk:doCreate connection="registerConn" input="myinput" output="myoutput"/>

      <c:if test="${ptkError == 'false'}">

         <c:set var="accountId"><ptk:getUniqueId output="myoutput"/></c:set>

         <table cellpadding="3" cellspacing="3" border="0">
            <tr class="middle">
               <td class="name"><i>Reference:</i></td>
               <td class="data"><b><c:out value="${accountId}"/></b></td>
            </tr>
            <tr class="middle">
               <td class="name"><i>First Name:</i></td>
               <td class="data"><b><c:out value="${param.fname}"/></b></td>
            </tr>
            <tr class="middle">
               <td class="name"><i>Last Name:</i></td>
               <td class="data"><b><c:out value="${param.lname}"/></b></td>
            </tr>
         </table>

      </c:if>
      </td>
      </tr>
   </table>
   <table class="buttons">
      <tr><td>
            <input type="submit" value="Finished"/>
         </td></tr>
   </table>
</form>

<c:if test="${ptkError == 'true'}">
   Error: <c:out value="${ptkStatus}"/>
   <c:set var="ptkError" value="false"/>
</c:if>

<%@include file="../common/conn_close.jsp"%>

