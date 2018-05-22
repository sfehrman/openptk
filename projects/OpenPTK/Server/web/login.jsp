<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/transitional.dtd">
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="ptk" uri="http://www.openptk.org/taglib" %>
<!-- Get Connection -->

<c:set scope="session" var="connProps" value="openptk_client"/>

<!-- Close the existing OpenPTK Client Connection to force a logout -->
<ptk:closeConnection connection="umlConn" scope="session"/>

<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>OpenPTK Server</title>
   <link rel="stylesheet" type="text/css" href="styles/openptk-config.css" />
</head>

<c:if test="${param.error != null}">
   <c:if test="${param.error == 'true'}">
      <c:set scope="page" var="errorMsg" value="Admin Access Required!"/>
   </c:if>
</c:if>

<body id="openptk-server-main">

   <div id="container">

      <div id='toptoolbar'>
         <div id="userMessage">
            <c:out value="${pageScope.errorMsg}"/>
         </div>
      </div>

      <div id="header">
         <div id="banner">
            <div id="headline">
                 <span class="headline-background">OpenPTK Server v2.2.0</span>
                 <span class="headline-foreground">OpenPTK Server v2.2.0</span>
            </div>
         </div>
      </div>

      <!-- Begin Main Panel -->
      <div id="mainpanelabout">

         <div id="leftborder">

         </div>
         <center>
            <br>
            <FORM ACTION="<%=request.getContextPath() %>/login" METHOD="POST">
               <table>
                  <tr><th colspan="2">
                     <INPUT TYPE="hidden" NAME="goto" value="<%=request.getContextPath() %>/config/index.jsp">
                     <INPUT TYPE="hidden" NAME="clientid" value="openptkserver">
                  </th></tr>
                  <tr><td align="right">Username:</td><td><INPUT TYPE="text" NAME="user"></td></tr>
                  <tr><td align="right">Password:</td><td><INPUT TYPE="password" NAME="password"></td></tr>
                  <tr><td colspan="2" align="center"><INPUT TYPE="submit" VALUE="Login"></td></tr>
               </table>
            </FORM>
            <br><br>
            <a href="http://docs.openptk.org">OpenPTK Documentation Page</a>

         </center>
         <div id="rightborder">
         </div>
      </div>
      <!-- End of Main Panel -->

      <div id="footer">

      </div>
   </div>
   <br>
</body>
</html>
