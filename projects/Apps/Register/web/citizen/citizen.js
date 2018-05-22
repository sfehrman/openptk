function cancel() {
   document.data.mode.value = "welcome";
}

function verifyData() {
   
   var themessage = "Required fields: ";
   if (document.data.fname.value=="") {
      themessage = themessage + " - First Name";
   }
   if (document.data.lname.value=="") {
      themessage = themessage + " -  Last Name";
   }
   if (document.data.dob.value=="") {
      themessage = themessage + " -  DOB";
   }
   if (document.data.last4ssn.value=="") {
      themessage = themessage + " -  Last 4 SSN";
   }
   if (document.data.dln.value=="") {
      themessage = themessage + " -  Drivers Lic";
   }
   if (document.data.gender.value=="") {
      themessage = themessage + " -  Gender";
   }
   if (document.data.haddr1.value=="") {
      themessage = themessage + " -  Home Address 1";
   }
   if (document.data.hcity.value=="") {
      themessage = themessage + " -  Home City";
   }
   if (document.data.hstate.value=="") {
      themessage = themessage + " -  Home State";
   }
   if (document.data.hzip.value=="") {
      themessage = themessage + " -  Home Zip";
   }
   if (document.data.hcounty.value=="") {
      themessage = themessage + " -  Home County";
   }
   if (document.data.hcountry.value=="") {
      themessage = themessage + " -  Home Country";
   }
   if (document.data.hemail.value=="") {
      themessage = themessage + " -  Home Email";
   }
   if (document.data.hphone.value=="") {
      themessage = themessage + " -  Home Phone";
   }
   if (document.data.password.value=="") {
      themessage = themessage + " -  Password";
   }
   if (document.data.answer1.value=="") {
      themessage = themessage + " -  Answer 1";
   }
   if (document.data.answer2.value=="") {
      themessage = themessage + " -  Answer 2";
   }
   if (document.data.answer3.value=="") {
      themessage = themessage + " -  Answer 3";
   }
   //alert if fields are empty and cancel form submit
   if (themessage == "Required fields: ") {
      // does the email data match
      if (document.data.password.value == document.data.confirmpwd.value) {
         document.form.submit();
         return true;
      }
      else {
         alert("Password and Confirmation do not match.");
         document.data.mode.value = "data";
         return false;
      }
   }
   else {
      alert(themessage);
      document.data.mode.value = "data";
      return false;
   }
   return true;
}