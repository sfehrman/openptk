<%@include file="common/conn_close.jsp"%>

<h3>Registration Interfaces:</h3>

<table border="0" cellpadding="5" cellspacing="5" width="100%">
   <tr valign="top">
      <td>
         <a href="default/index.jsp?mode=welcome">Wizard&nbsp;Layout</a>
      </td>
      <td>
         A multi-page form with input validation
      </td>
   </tr>
   <tr valign="top">
      <td>
         <a href="enrollment/index.jsp?mode=welcome">Simple&nbsp;Enrollment</a>
      </td>
      <td>
         A basic registration form for testing the JSP TAG Library.
      </td>
   </tr>
   <tr valign="top">
      <td>
         <a href="citizen/index.jsp?mode=welcome">Citizen&nbsp;Form</a>
      </td>
      <td>
         Detailed registration form that is intended to simulate a paper process.
      </td>
   </tr>
   <tr valign="top">
      <td>
         <a href="captcha/index.jsp?mode=welcome">CAPTCHA&nbsp;verification</a>
      </td>
      <td>
         Integrates the <a href="http://www.google.com/recaptcha">reCAPTCHA</a> Service
         to reduce SPAM from automated attacks.
         <br>
         <b>WARNING</b>: You must update the <tt>create.jsp</tt> and <tt>data.jsp</tt>
         to include a valid reCAPTCHA PRIVATE and PUBLIC key.
      </td>
   </tr>
</table>