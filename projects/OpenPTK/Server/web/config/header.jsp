<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/transitional.dtd">
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="ptk" uri="http://www.openptk.org/taglib" %>
<!-- Get Connection -->

<c:set scope="session" var="connProps" value="openptk_client"/>

<c:if test="${umlConn != null}">
   <!-- Close the existing OpenPTK Client Connection to force a logout -->
   <ptk:closeConnection connection="umlConn" scope="session"/>
</c:if>

<ptk:getConnection var="myconn" properties="${connProps}" scope="session" forcenew="true" />

<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>OpenPTK Server</title>

      <script type="text/javascript">
          var contextPath = "<%=request.getContextPath()%>";
          var currenturi = null;
          var previousuri = null;        
      </script>

      <link rel="stylesheet" type="text/css" href="../styles/openptk-config.css" media="all"/>
   </head>
   <body id="openptk-server-main">
      <div id="container">
         <div id='toptoolbar'>
            <a href="<%=request.getContextPath()%>/logout?goto=<%=request.getContextPath()%>">Logout</a>
         </div>

         <ptk:getConnectionData connection="myconn" name="type" var="data" />

         <c:if test="${data!='SYSTEM'}">

            <script type="text/javascript">
   window.location = "<%=request.getContextPath()%>/logout?goto=<%=request.getContextPath()%>"
            </script>

         </c:if>

         <div id="header">
            <div id="banner">
               <div id="headline">
                  <span class="headline-background">OpenPTK Server v2.2.0</span>
                  <span class="headline-foreground">OpenPTK Server v2.2.0</span>
               </div>
            </div>
         </div>
