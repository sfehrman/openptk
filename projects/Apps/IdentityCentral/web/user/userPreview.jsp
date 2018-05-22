<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/transitional.dtd">
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="ptk" uri="http://www.openptk.org/taglib" %>
<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*" %>

<!-- Set the value of the OpenPTK Client properties file -->
<c:set scope="session" var="connProps" value="openptk_client"/>

<!-- Create the OpenPTK Client Connection -->
<ptk:getConnection var="identitycentralapi" properties="${connProps}" scope="session" forcenew="true" />

<ptk:getConnectionProperty connection="identitycentralapi" name="context.photo" var="photoContext"/>
<ptk:getConnectionProperty connection="identitycentralapi" name="context.media" var="mediaContext"/>
<ptk:getConnectionProperty connection="identitycentralapi" name="server.relative.uri" var="contextPath" />

<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>OpenPTK User Detail</title>
      <link rel="stylesheet" type="text/css" href="../styles/openptk.css" />
   </head>
   <body id="openptk-server-main">

      <c:set var="subjectContext" value="${photoContext}"/>

      <c:if test="${param.user != null}">
         <!-- Get Connection -->
         <c:set scope="session" var="connProps" value="openptk_client"/>
         <ptk:getConnection var="myconn" properties="${connProps}" scope="session"/>

         <c:if test="${ptkError == 'false'}">
            <ptk:setInput var="myinput"/>
            <ptk:setUniqueId input="myinput" value="${param.user}"/>
            <ptk:setContext connection="myconn" value="${subjectContext}"/>
            <ptk:doRead connection="myconn" input="myinput" output="myoutput"/>
            <c:set scope="page" var="subEmail">
               <ptk:getValue output='myoutput' name="email"/>
            </c:set>
            <c:set scope="page" var="subTelephone">
               <ptk:getValue output='myoutput' name="telephone"/>
            </c:set>
            <c:if test="${ptkError == 'false'}">
               <hr>
               <table border="0">
                  <tr>
                     <td align="center">
                        <ptk:setInput var="imageinput"/>
                        <ptk:setUniqueId input="imageinput" value="${subjectContext}-${param.user}-thumbnail"/>
                        <ptk:setContext connection="myconn" value="${mediaContext}"/>

                        <c:if test="${ptkError == 'false'}">
                           <ptk:doRead connection="myconn" input="imageinput" output="imageoutput"/>
                           <c:if test="${ptkError == 'false'}">
                              <c:set var="thumbnailuri">
                                 ${contextPath}/resources/contexts/${subjectContext}/subjects/<c:out value="${param.user}"/>/relationships/thumbnail?time=<%=new java.util.Date()%>
                              </c:set>
                              <img src="<c:out value="${thumbnailuri}"/>">
                           </c:if>
                           <c:if test="${ptkError != 'false'}">
                              <img src="../images/person.png">
                              <c:set var="ptkError" value="false"/>
                           </c:if>
                        </c:if>

                     </td>
                  </tr>
                  <tr>
                     <td align="center"><small><c:out value="${subEmail}"/></small></td>
                  </tr>
                  <tr>
                     <td align="left"><small><c:out value="${subTelephone}"/></small></td>
                  </tr>
               </table>
               <hr>
            </c:if>

         </c:if>

      </c:if>
      <c:if test="${ptkError != 'false'}">
         <table border="1" >
            <tr><td>Results: Read Failed</td></tr>
            <tr><td><c:out value="${ptkError}"/></td></tr>
         </table>
      </c:if>

   </body>
</html>

