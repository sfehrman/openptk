<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ptk" uri="http://www.openptk.org/taglib" %>

<!-- Set the value of the OpenPTK Client properties file -->
<c:set scope="session" var="connProps" value="openptk_client"/>

<!-- Create the OpenPTK Client Connection -->
<ptk:getConnection var="identitycentralapi" properties="${connProps}" scope="session" forcenew="true" />

<script type="text/javascript">
var searchquantity = "<ptk:getConnectionProperty connection="identitycentralapi" name="search.results.quantity" />";
var searchminchars = "<ptk:getConnectionProperty connection="identitycentralapi" name="search.min.chars" />";
var registerContext = "<ptk:getConnectionProperty connection="identitycentralapi" name="context.registration" />";
var photoContext = "<ptk:getConnectionProperty connection="identitycentralapi" name="context.photo" />";
var mediaContext = "<ptk:getConnectionProperty connection="identitycentralapi" name="context.media" />";
var contextPath = "<ptk:getConnectionProperty connection="identitycentralapi" name="server.relative.uri" />";
var updateLocation = "<ptk:getConnectionProperty connection="identitycentralapi" name="context.subject.use.location" />";
</script>

<html>
   <head>
      <meta http-equiv="X-UA-Compatible" content="IE=8">
      <meta http-equiv="expires" content="0">
      <meta http-equiv="pragma" content="no-cache">
      <title>OpenPTK Identity Central</title>

      <link rel="stylesheet" type="text/css" href="../styles/openptk.css" />

   </head>
   <body id="openptk-server-main">

      <div id="container">

         <div id='toptoolbar'>
            <div id="userMessage">
               <c:if test="${openptksessioninfo != null}">
                  <c:out value="${openptksessioninfo}"/>
               </c:if>
               <c:if test="${param.logout != null}">
                  Logged Out
               </c:if>
               <c:if test="${param.login == 'failed'}">
                  Login Failed
               </c:if>
            </div>

                  <div id='loginpopup'>
                     <div id="myProfile">
                        <a href="javascript:void(0)" onclick = "registerUserStart();return false">Register</a>
                     </div>
                     <a href="javascript:void(0)" onclick = "loginStart();return false">Login</a>

                  </div>
                  <div id='logout'>
                     <a id="profilelink" href="javascript:void(0)" title="My Personal Information" onclick = 'getMyProfile();return false'>Profile</a>
                     <a href="<%=request.getRequestURI()%>?logout=true&goto=<%=request.getRequestURI()%>" id="logoutlink">Logout</a>
                  </div>
         </div>

         <div id="header">

            <div id="banner">
               <img src="../images/logo.png" border="0" >
               <div id="search">
                  <form id="searchForm" action="javascript:submitform()">
                     <table>
                        <tr>
                                 <td>
                                    <div id="createbutton"><a href="javascript:void(0)" title="Create User..." onclick = "createUserStart();return false"><div id="subjectcreate"></div></a></div>
                                 </td>
                           <td>
                              <input class="searchinput" type="text" id="searchVal" size="20" title="Enter Search Information"/>
                              <a class="searchbutton" href="javascript:void(0)" title="User Search" onclick = "submitform();return false"><img class="searchbutton" src="../images/dot.gif" border="0" ></a>
                           </td>
                           <td>
                              <a id="searchClear" href="javascript:void(0)" title="Clear Input" onclick = "clearAll();return false"><img src="../images/clear.png" border="0" ></a>
                           </td>
                        </tr>
                     </table>
                  </form>
               </div>
            </div>
         </div>
