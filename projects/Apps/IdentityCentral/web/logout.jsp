<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN" "http://www.w3.org/TR/REC-html40/transitional.dtd">

<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title>OpenPTK User Login</title>
      <link rel="stylesheet" type="text/css" href="styles/openptk.css" />
      <script type="text/javascript" src="include/prototype.js"></script>
      <script type="text/javascript" src="include/scriptaculous.js?load=effects,controls"></script>
      <script type="text/javascript" src="include/openptk-user-objects.js" ></script>
      <script type="text/javascript" src="include/openptk-user-ui.js" ></script>

   </head>
   <body>
      <h1>Logout</h1>

      <br><br>
      <a href="index.jsp">Home</a>
      <br>
      <FORM ACTION="index.jsp" METHOD="POST">
         <INPUT TYPE="hidden" NAME="logout" value="true">
         <INPUT TYPE="submit" VALUE="Logout">
      </FORM>
      <br>
      <br><br>
      OpenPTK Session Type:  <%= request.getSession().getAttribute("openptksessiontype")%>
      <br><br>
      OpenPTK Login Info:  <%= request.getAttribute("openptksessioninfo")%>
      <br><br>


   </body>
</html>
