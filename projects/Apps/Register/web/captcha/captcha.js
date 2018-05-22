function cancel() {
   document.data.mode.value = "welcome";
}

function verifyData() {
   
   var themessage = "Required fields: ";

   if (document.data.fname.value=="") {
      themessage = themessage + " - First Name";
   }
   if (document.data.lname.value=="") {
      themessage = themessage + " - Last Name";
   }
   if (document.data.email.value=="") {
      themessage = themessage + " - Email";
   }
   if (document.data.confirmemail.value=="") {
      themessage = themessage + " - Confirm Email";
   }
   if (document.data.password.value=="") {
      themessage = themessage + " - Password";
   }
   if (document.data.confirmpassword.value=="") {
      themessage = themessage + " - Confirm Password";
   }
   if (document.data.answer1.value=="") {
      themessage = themessage + " - Answer 1";
   }
   if (document.data.answer2.value=="") {
      themessage = themessage + " - Answer 2";
   }
   if (document.data.answer3.value=="") {
      themessage = themessage + " - Answer 3";
   }

   //alert if fields are empty and cancel form submit

   document.data.mode.value = "data";

   if (themessage == "Required fields: ") {

      // does the email data match

      if (document.data.email.value == document.data.confirmemail.value) {

         // does the password data match
         
         if (document.data.password.value == document.data.confirmpassword.value) {

            if ( document.data.accept.checked == true) {
               document.data.mode.value = "create";
               document.form.submit();
            } else {
               alert("You MUST accept the Terms and Conditions.");
            }
         } else {
            alert("Password and Confirmation do not match.");
         }
      } else {
         alert("Email and Confirmation do not match.");
      }
   }
   else {
      alert(themessage);
   }

   return true;
}