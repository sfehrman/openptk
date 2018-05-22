function verifyData() {

   var themessage = "Required fields: ";
   if (document.data.fname.value=="") {
      themessage = themessage + " - First Name";
   }
   if (document.data.lname.value=="") {
      themessage = themessage + " -  Last Name";
   }
   if (document.data.email.value=="") {
      themessage = themessage + " -  Email";
   }
   if (document.data.confirmemail.value=="") {
      themessage = themessage + " -  Confirm Email";
   }
   //alert if fields are empty and cancel form submit
   if (themessage == "Required fields: ") {
      // does the email data match
      if (document.data.email.value == document.data.confirmemail.value) {
         document.form.submit();
      }
      else {
         alert("Email and Confirm Email do not match.");
         document.data.mode.value = "data";
      }
   }
   else {
      alert(themessage);
      document.data.mode.value = "data";
   }
   return true;
}

function verifyTerms() {
   var chk = document.terms.accept.checked;
   if ( chk == false) {
      alert("You MUST accept the Terms and Conditions.");
      document.terms.mode.value = "terms";
   }
   else
   {
      document.form.submit();
   }
   return true;
}

function prevData() {
   document.data.mode.value = "welcome";
}

function prevTerms() {
   document.terms.mode.value = "data";
}

function cancelTerms() {
   document.terms.mode.value = "welcome";
}

function prevConfirm() {
   document.confirm.mode.value = "terms";
}

function cancelConfirm() {
   document.confirm.mode.value = "welcome";
}
