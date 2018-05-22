// The contextPath variable is set on the server in a script in header.jsp
var baseuri = contextPath+"/resources/";
var subject = "";
var currentsubjectid = "";
var currentsubject = null;
var resaccordion = null;
var serverContext = null;
var openptksessionid = null;
var openptkuser = null;
var openptkusercontext = null;
var openptksessiontype = null;

function getCurrentContext() {

   getResource('defaultcontext', 'clients/identitycentral');

   return;
}

function setCurrentContext(context) {

   serverContext = context;
   return;
}

function getCurrentSessionInfo() {

   getResource('sessioninfo', 'sessioninfo');

   return;
}

function setCurrentSessionInfo(sessionid, sessiontype, principalid, usercontext) {
   openptksessionid = sessionid;
   openptksessiontype = sessiontype;
   openptkuser = principalid;
   openptkusercontext = usercontext;
   
   if ((openptksessiontype == 'USER')||(openptksessiontype == 'SYSTEM'))
   {
      $('loginpopup').style.display='none';
      $('profilelink').style.display='block';
      $('logout').style.display='block';
      if (openptksessiontype == 'SYSTEM')
      {
         $('profilelink').style.display='none';
         $('createbutton').style.display='block';
      }
   }
   else if ((openptksessiontype == 'ANON')||(openptksessiontype == null)||(openptksessiontype == ''))
   {
      $('loginpopup').style.display='block';
      $('logout').style.display='none';
      $('createbutton').style.display='none';
   }
   return;
}

function checkCurrentSessionInfo() {
   getCurrentSessionInfo();
}

function loginStart() {
   $('loginpanellight').style.display='block';
   $('loginpanelfade').style.display='block';
   $('loginpanelinner').style.display='block';
   $('username').focus();
   var userloginfield = new LiveValidation( 'username', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   userloginfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var userpasswordfield = new LiveValidation( 'userpassword', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   userpasswordfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );

}

function registerUserStart() {
   $('registrationpanellight').style.display='block';
   $('registrationpanelfade').style.display='block';

   //clear the form
   Form.Element.setValue($("registration")['regfirstname'], '');
   Form.Element.setValue($("registration")['reglastname'], '');
   Form.Element.setValue($("registration")['regemail'], '');
   Form.Element.setValue($("registration")['regpassword'], '');
   Form.Element.setValue($("registration")['regconfpassword'], '');
   Form.Element.setValue($("registration")['regfpwdanswer0'], '');
   Form.Element.setValue($("registration")['regfpwdanswer1'], '');  
   Form.Element.setValue($("registration")['regfpwdanswer2'], '');
   Form.Element.setValue($("registration")['regacceptterms'], '');

   var regfirstnamefield = new LiveValidation( 'regfirstname', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   regfirstnamefield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var reglastnamefield = new LiveValidation( 'reglastname', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   reglastnamefield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var regemailfield = new LiveValidation( 'regemail', {
      validMessage: "",
      onlyOnBlur: true
   });
   regemailfield.add( Validate.Email );
   regemailfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var regpasswordfield = new LiveValidation( 'regpassword', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   regpasswordfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var regconfpasswordfield = new LiveValidation('regconfpassword', {
      validMessage: ""
   });
   regconfpasswordfield.add( Validate.Confirmation, {
      failureMessage: "Passwords must match!",
      match: 'regpassword'
   } );
   regconfpasswordfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var regaccepttermsfield = new LiveValidation( 'regacceptterms', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   regaccepttermsfield.add( Validate.Acceptance );

}

function registerUser() {
   var forgottenPasswordAnswers = new Array();
   var forgottenPasswordQuestions = new Array();
   var cform = $('registration');
   var firstname = cform['regfirstname'];
   var lastname = cform['reglastname'];
   var email = cform['regemail'];
   var password = cform['regpassword'];
   var confpassword = cform['regconfpassword'];

   var fnameval = $F(firstname);
   var lnameval = $F(lastname);
   var emailval = $F(email);
   var passwordval = $F(password);

   var numquestions =  cform['fpnumquestions'];
   var numquestionsval = $F(numquestions);
   var thisquestion = '';
   var thisanswer = '';

   for (var i=0; i < numquestionsval; i++)
   {
      thisquestion = cform['regfpwdquestion'+i];
      thisanswer = cform['regfpwdanswer'+i];
      forgottenPasswordQuestions[i]=$F(thisquestion);
      forgottenPasswordAnswers[i]=$F(thisanswer);
   }

   subject = {
      "subject" : {
         "attributes" :

         {
            "lastname" : lnameval,
            "firstname" : fnameval,
            "email" : emailval,
            "password": passwordval,
            "forgottenPasswordQuestions": forgottenPasswordQuestions,
            "forgottenPasswordAnswers": forgottenPasswordAnswers
         }
      }
   };

   var subjectstring = Object.toJSON(subject);

   new Ajax.Request(baseuri+'contexts/'+registerContext+'/subjects', {
      method:'post',
      postBody:  Object.toJSON(subject),
      requestHeaders:{
         'Accept':'application/json'
      },
      contentType: 'application/json',
      onSuccess: function (transport) {
         $('registrationpanellight').style.display='none';
         $('registrationpanelfade').style.display='none';
         $('userMessage').update('Registration Success!');
         Form.reset('registration');

         // Log user in, this assumes that the userid id firstinitial + lastname, not always true.
         Form.Element.setValue($("loginPanelForm")['user'], fnameval.substring(0,1) + lnameval);
         Form.Element.setValue($("loginPanelForm")['password'], passwordval);
         window.document.loginPanelFormName.submit();
      },
      onFailure:function(){
         $('registrationpanellight').style.display='none';
         $('registrationpanelfade').style.display='none';
         $('userMessage').update('Registration Failed...');
      }
   });
}

function createUserStart() {

   clearAll();
   $('createsubjectlight').style.display='block';
   $('createsubjectfade').style.display='block';

   Form.Element.setValue($("createSubjectForm")['createfirstname'], '');
   Form.Element.setValue($("createSubjectForm")['createlastname'], '');
   Form.Element.setValue($("createSubjectForm")['createtitle'], '');
   Form.Element.setValue($("createSubjectForm")['createtelephone'], '');
   Form.Element.setValue($("createSubjectForm")['createemail'], '');
   Form.Element.setValue($("createSubjectForm")['createmanager'], '');
   Form.Element.setValue($("createSubjectForm")['createroles'], '');
   Form.Element.setValue($("createSubjectForm")['createorganization'], '');
   Form.Element.setValue($("createSubjectForm")['createpassword'], '');
   Form.Element.setValue($("createSubjectForm")['confpassword'], '');

   var createfirstnamefield = new LiveValidation( 'createfirstname', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   createfirstnamefield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var createlastnamefield = new LiveValidation( 'createlastname', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   createlastnamefield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   //   var createemailfield = new LiveValidation( 'createemail', {
   //      validMessage: "",
   //      onlyOnBlur: true
   //   });
   //   createemailfield.add( Validate.Email );
   //   createemailfield.add( Validate.Presence, {
   //      failureMessage: "Required!"
   //   } );
   var createpasswordfield = new LiveValidation( 'createpassword', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   createpasswordfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
   var createconfpasswordfield = new LiveValidation('createconfpassword', {
      validMessage: ""
   });
   createconfpasswordfield.add( Validate.Confirmation, {
      failureMessage: "Passwords must match!",
      match: 'createpassword'
   } );
   createconfpasswordfield.add( Validate.Presence, {
      failureMessage: "Required!"
   } );
}
function createSubject() {

   var cform = $('createSubjectForm');
   var firstname = cform['createfirstname'];
   var lastname = cform['createlastname'];
   //var fullname = firstname + ' ' + lastname;
   var title = cform['createtitle'];
   var telephone = cform['createtelephone'];
   var email = cform['createemail'];
   var manager = cform['createmanager'];
   var roles = cform['createroles'];
   var organization = cform['createorganization'];
   var password = cform['createpassword'];

   var fnameval = $F(firstname);
   var titleval = $F(title);
   var lnameval = $F(lastname);
   var telephoneval = $F(telephone);
   var emailval = $F(email);
   var managerval = $F(manager);
   var rolesval = $F(roles);
   var organizationval = $F(organization);
   var passwordval = $F(password);

   var fullnameval = fnameval + ' ' + lnameval;

   subject = {
      "subject" : {
         "attributes" :
         {
            "lastname" : lnameval,
            "title" : titleval,
            "firstname" : fnameval,
            "telephone" : telephoneval,
            "email" : emailval,
            "manager" : managerval,
            "roles" : rolesval,
            "fullname" : fullnameval,
            "organization" : organizationval,
            "password" : passwordval
         }
      }
   };

   var subjectstring = Object.toJSON(subject);

   new Ajax.Request(baseuri + 'contexts/' + serverContext +'/subjects', {
      method:'post',
      postBody:  Object.toJSON(subject),
      requestHeaders:{
         'Accept':'application/json'
      },
      contentType: 'application/json',
      onSuccess: function (transport) {
         $('userMessage').update("User Created. " );
         $('createsubjectlight').style.display='none';
         $('createsubjectfade').style.display='none';

      },
      onFailure:function(){
         $('userMessage').update('Something went wrong...'+ subject);
         $('createsubjectlight').style.display='none';
         $('createsubjectfade').style.display='none';
      }
   });
}

function updateSubject() {

   var upform = $('updateSubjectForm');
   var uniqueid = upform['subjectupdateuniqueid'];
   var firstname = upform['subjectupdatefirstname'];
   var lastname = upform['subjectupdatelastname'];
   var title = upform['subjectupdatetitle'];
   var telephone = upform['subjectupdatetelephone'];
   var email = upform['subjectupdateemail'];
   var manager = upform['subjectupdatemanager'];
   var roles = upform['subjectupdateroles'];
   var organization = upform['subjectupdateorganization'];
   var forgottenPasswordAnswers = new Array();
   var forgottenPasswordQuestions = new Array();

   for (var i=0; i < Form.Element.getValue($("updateSubjectForm")['fpnumquestions']); i++)
   {

      forgottenPasswordQuestions[i]=Form.Element.getValue($("updateSubjectForm")['q'+i]);
      forgottenPasswordAnswers[i]=Form.Element.getValue($("updateSubjectForm")['a'+i]);
   }

   var subjectupdates = {
      "subject" : {

   }
   };

   var subattributes = new Object;

   if ( currentsubject.subject.attributes.firstname != $F(firstname) ){
      subattributes.firstname = $F(firstname);
   }
   if ( currentsubject.subject.attributes.lastname != $F(lastname) ){
      subattributes.lastname = $F(lastname);
   }
   if ( currentsubject.subject.attributes.title != $F(title) ){
      subattributes.title = $F(title);
   }
   if ( currentsubject.subject.attributes.telephone != $F(telephone) ){
      subattributes.telephone = $F(telephone);
   }
   if ( currentsubject.subject.attributes.email != $F(email) ){
      subattributes.email = $F(email);
   }
   if ( currentsubject.subject.attributes.manager != $F(manager) ){
      subattributes.manager = $F(manager);
   }
   if ( currentsubject.subject.attributes.roles != $F(roles) ){
      subattributes.roles = $F(roles);
   }
   if ( currentsubject.subject.attributes.organization != $F(organization) ){
      subattributes.organization = $F(organization);
   }
   for (var i=0; i < Form.Element.getValue($("updateSubjectForm")['fpnumquestions']); i++)
   {
      if ( currentsubject.subject.attributes.forgottenPasswordAnswers[i] != forgottenPasswordAnswers[i] ){
         subattributes.forgottenPasswordQuestions = forgottenPasswordQuestions;
         subattributes.forgottenPasswordAnswers = forgottenPasswordAnswers;
      }
   }

   subjectupdates.subject.attributes = subattributes;

   // prototype does not respect HTTP PUT and DELETE, they are simulated over POST so XHR is used directly

   var xmlhttp = null;
   if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
      //make sure that Browser supports overrideMimeType
      if ( typeof xmlhttp.overrideContentType != 'undefined') {
         xmlhttp.overrideMimeType('text/xml');
      }
   } else if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   } else {
      $('userMessage').update('ERROR: xmlhttprequest failed');
   }
   xmlhttp.open("PUT", baseuri + 'contexts/' + serverContext +'/subjects/'+$F(uniqueid), true);

   xmlhttp.setRequestHeader("Content-Type","application/json");
   xmlhttp.onreadystatechange = function(){

      if ( xmlhttp.readyState == 4 ) {
         if ( (xmlhttp.status == 200) || (xmlhttp.status == 204) || (xmlhttp.status == 1223) ) {
            // Output the results
            getResource('subject', 'contexts/'+serverContext+'/subjects/'+$F(uniqueid)+'/views/extended/');

            $('userMessage').update('Subject Updated!');
            subjecttabs.toggleTab("0");
         }
         else {

            $('userMessage').update('Updated Failed!');

         }
         clearSubjectDisplay();
      } else {
   // waiting for the call to complete
   }
   };
   xmlhttp.send(Object.toJSON(subjectupdates));

}

function changePasswordSubject() {

   var upform = $('changepasswordSubjectForm');
   var uniqueid = upform['uniqueid'];
   var password = upform['subjectpassword'];
   ;

   subject = {
      "subject" : {
         "attributes" :

         {
            "password" : $F(password)
         }
      }
   };
   // prototype does not respect HTTP PUT and DELETE, they are simulated over POST so XHR is used directly

   var xmlhttp = null;
   if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
      //make sure that Browser supports overrideMimeType
      if ( typeof xmlhttp.overrideContentType != 'undefined') {
         xmlhttp.overrideMimeType('text/xml');
      }
   } else if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   } else {
      $('userMessage').update('ERROR: xmlhttprequest failed');
   }
   xmlhttp.open("PUT", baseuri + 'contexts/' + serverContext +'/subjects/'+$F(uniqueid)+ '/password/change', true);

   xmlhttp.setRequestHeader("Content-Type","application/json");
   xmlhttp.onreadystatechange = function(){
      if ( xmlhttp.readyState == 4 ) {
         if ( (xmlhttp.status == 200) || (xmlhttp.status == 204) || (xmlhttp.status == 1223) ) {
            // Output the results
            $('userMessage').update('Password Changed!');
            subjecttabs.toggleTab("0");
            Form.Element.setValue($("changepasswordSubjectForm")['subjectpassword'], null);
            Form.Element.setValue($("changepasswordSubjectForm")['subjectconfpassword'], null);
         }
         else {

            $('userMessage').update('Change Password failed!  ');
         }
      } else {
   // waiting for the call to complete
   }
   };
   xmlhttp.send(Object.toJSON(subject));

}

function fpwdChangePassword() {

   var upform = $('fpwdchangePasswordForm');
   var uniqueid = upform['uniqueid'];
   var password = upform['fp2password'];
   var confpassword = upform['fp2password'];

   subject = {
      "subject" : {
         "attributes" :

         {
            "password" : $F(password)
         }
      }
   };
   // prototype does not respect HTTP PUT and DELETE, they are simulated over POST so XHR is used directly
   var xmlhttp = null;
   if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
      //make sure that Browser supports overrideMimeType
      if ( typeof xmlhttp.overrideContentType != 'undefined') {
         xmlhttp.overrideMimeType('text/xml');
      }
   } else if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   } else {
      $('userMessage').update('ERROR: xmlhttprequest failed');
   }
   xmlhttp.open("PUT", baseuri + 'contexts/' + serverContext +'/subjects/'+$F(uniqueid)+ '/password/forgot/change', true);

   xmlhttp.setRequestHeader("Accept","application/json");
   xmlhttp.setRequestHeader("Content-Type","application/json");
   xmlhttp.onreadystatechange = function(){
      if ( xmlhttp.readyState == 4 ) {

         if ( (xmlhttp.status == 200) || (xmlhttp.status == 204) || (xmlhttp.status == 1223) ) {
            // Output the results
            Form.reset('fpwdchangePasswordForm');
            $('userMessage').update('Password Changed!');
            $('fpwdchangepassword').style.display='none';
            $('loginpanellight').style.display='none';
            $('loginpanelfade').style.display='none';

            // Log user in, this assumes that the userid id firstinitial + lastname, not always true.
            Form.Element.setValue($("loginPanelForm")['user'], fnameval.substring(0,1) + lnameval);
            Form.Element.setValue($("loginPanelForm")['password'], passwordval);
            window.document.loginPanelFormName.submit();
            $('userMessage').update('Welcome: '+fnameval.substring(0,1) + lnameval);

         }
         else {
            $('loginpanellight').style.display='none';
            $('loginpanelfade').style.display='none';
            $('userMessage').update('Change Password failed!  ');
         }
      } else {
   // waiting for the call to complete
   }
   };
   xmlhttp.send(Object.toJSON(subject));

}

function resetPasswordSubject() {

   $('subjectresetpasswordConfirm').style.display='block';
   $('resetdetails').style.display='block';

   url = baseuri + 'contexts/' + serverContext +'/subjects/'+ currentsubjectid + '/password/reset';

   new Ajax.Request(url, {
      method:'get',
      requestHeaders: {
         Accept: 'application/json'
      },
      onSuccess: function(transport){
         var resourceresponse = transport.responseJSON;

         var resethtml = '<br>New password value:  <b>'+resourceresponse.subject.attributes.password+'</b><br><br><a href = "javascript:void(0)" title="Cancel" onclick = \'resetPasswordClear()\'>Return to User Details</a>';
         $('resetdetails').update(resethtml);
         $('userMessage').update('Password Reset Success.');
         $('subjectresetpasswordConfirm').style.display='none';

      },
      onFailure:function(){
         $('userMessage').update('Something went wrong...')

      }
   });

}

function resetPasswordClear() {
   var resethtml = '';
   $('resetdetails').update(resethtml);
   $('userMessage').update('');
   $('subjectresetpasswordConfirm').style.display='block';
   subjecttabs.toggleTab("0");
}

function forgotPasswordStart() {

   $('loginpanelinner').style.display='none';
   $('forgotpanelinner').style.display='block';
   var uniqueidField = new LiveValidation( 'fp0uniqueid', {
      validMessage: "",
      onlyOnSubmit: true
   } );
   uniqueidField.add( Validate.Presence, {
      failureMessage: "Required!"
   } );

}

function forgotPasswordSubject() {

   var html = "";
   var upform = $('fpwdForm1');
   var uniqueid = upform['fp0uniqueid'];
   var thisquestion=null;
   var fpquestlist=[];

   url = baseuri + 'contexts/' + serverContext +'/subjects/'+$F('fp0uniqueid') + '/password/forgot/questions';

   new Ajax.Request(url, {
      method:'get',
      requestHeaders: {
         Accept: 'application/json'
      },
      onSuccess: function(transport){
         Form.reset('fpwdForm1');
         var resourceresponse = transport.responseJSON;
         $('forgotpanelinner').style.display='none';
         $('subjectForgotPass').style.display='block';
         Form.Element.setValue($("ForgotPassForm")['fpuniqueid'], resourceresponse.subject.uniqueid);

         Form.Element.setValue($("ForgotPassForm")['fpnumquestions'], resourceresponse.subject.attributes.forgottenPasswordQuestions.length);

         for (var i=0; i < resourceresponse.subject.attributes.forgottenPasswordQuestions.length; i++)
         {
            html = html +resourceresponse.subject.attributes.forgottenPasswordQuestions[i]+'<br><input type="hidden" id="fpwdquestion'+i+'" value="'+resourceresponse.subject.attributes.forgottenPasswordQuestions[i]+'"/><input TYPE="password" id="fpwdanswer'+i+'" size="10"/><br>';
         }

         $('questions').update(html);

         for (var i=0; i < resourceresponse.subject.attributes.forgottenPasswordQuestions.length; i++)
         {
            fpquestlist[i] = new LiveValidation( 'fpwdanswer'+i, {
               validMessage: "",
               onlyOnSubmit: true
            } );
            fpquestlist[i].add( Validate.Presence, {
               failureMessage: "Required!"
            } );
         }

         $('userMessage').update('Forgot Password Step 1');
      },
      onFailure:function(){
         Form.reset('fpwdForm1');
         $('userMessage').update('Invalid Input');
         clearLogin();
      }
   });

}

function forgotPasswordSubject2() {

   var fpform = $('ForgotPassForm');

   var uniqueid = fpform['fpuniqueid'];
   var uniqueidval = $F(uniqueid);
   var forgottenPasswordAnswers = new Array();
   var forgottenPasswordQuestions = new Array();

   $('subjectForgotPass').style.display='none';

   for (var i=0; i < Form.Element.getValue($("ForgotPassForm")['fpnumquestions']); i++)
   {

      forgottenPasswordQuestions[i]=Form.Element.getValue($("ForgotPassForm")['fpwdquestion'+i]);
      forgottenPasswordAnswers[i]=Form.Element.getValue($("ForgotPassForm")['fpwdanswer'+i]);

   }

   url = baseuri + 'contexts/' + serverContext +'/subjects/'+uniqueidval + '/password/forgot/answers';

   subject = {
      "subject" : {
         "attributes" :
         {
            "forgottenPasswordQuestions": forgottenPasswordQuestions,
            "forgottenPasswordAnswers": forgottenPasswordAnswers
         }
      }
   };

   // prototype does not respect HTTP PUT and DELETE, they are simulated over POST so XHR is used directly
   var xmlhttp = null;
   if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
      //make sure that Browser supports overrideMimeType
      if ( typeof xmlhttp.overrideContentType != 'undefined') {
         xmlhttp.overrideMimeType('text/xml');
      }
   } else if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   } else {
      $('userMessage').update('ERROR: xmlhttprequest failed');
   }
   xmlhttp.open("PUT", url, true);

   xmlhttp.setRequestHeader("Content-Type","application/json");
   xmlhttp.setRequestHeader("Accept","application/json");

   xmlhttp.onreadystatechange = function(){
      if ( xmlhttp.readyState == 4 ) {
         if ( (xmlhttp.status == 200) || (xmlhttp.status == 204) || (xmlhttp.status == 1223) ) {
            // Output the results
            Form.reset('ForgotPassForm');
            Form.Element.setValue($("fpwdchangePasswordForm")['uniqueid'], uniqueidval);
            $('fpwdchangepassword').style.display='block';
            $('userMessage').update('Forgot Password Step2.');

            var fp2passwordfield = new LiveValidation( 'fp2password', {
               validMessage: "",
               onlyOnSubmit: true
            } );
            fp2passwordfield.add( Validate.Presence );
            var fp2confpasswordfield = new LiveValidation('fp2confpassword', {
               validMessage: ""
            });
            fp2confpasswordfield.add( Validate.Confirmation, {
               failureMessage: "Must match!",
               match: 'fp2password'
            } );
            fp2confpasswordfield.add( Validate.Presence );

         }
         else {
            $('loginpanellight').style.display='none';
            $('loginpanelfade').style.display='none';
            $('userMessage').update('Password Answers validation failed!  ');
            Form.reset('ForgotPassForm');            
         }
      } else {
   // waiting for the call to complete
   }
   };
   xmlhttp.send(Object.toJSON(subject));

}

function deleteSubject(subjectId) {
   // prototype does not respect HTTP PUT and DELETE, they are simulated over POST so XHR is used directly
   var xmlhttp = null;
   if (window.XMLHttpRequest) {
      xmlhttp = new XMLHttpRequest();
      //make sure that Browser supports overrideMimeType
      if ( typeof xmlhttp.overrideContentType != 'undefined') {
         xmlhttp.overrideMimeType('text/xml');
      }
   } else if (window.ActiveXObject) {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   } else {
      $('userMessage').update('ERROR: xmlhttprequest failed');
   }

   xmlhttp.open("DELETE", baseuri + 'contexts/' + serverContext + '/subjects/'+subjectId, true);
   xmlhttp.onreadystatechange = function(){
      if ( xmlhttp.readyState == 4 ) {
         if ( (xmlhttp.status == 200) || (xmlhttp.status == 204) || (xmlhttp.status == 1223) ) {
            // Output the results

            clearAll();
            $('userMessage').update('User Deleted!');
            //clear the subject from the display

         }
         else {

            $('userMessage').update('Delete User failed!  ');
         }

      } else {
   // waiting for the call to complete
   }
   };
   xmlhttp.send(null);

}

function getResource(resourcetype, resource) {
   var url = baseuri + resource;
   
   new Ajax.Request(url, {
      method:'get',
      requestHeaders: {
         Accept: 'application/json'
      },
      onSuccess: function(transport){
         var resourceresponse = transport.responseJSON;

         switch(resourcetype)
         {
            case 'defaultcontext':
               setCurrentContext(resourceresponse.defaultContext);
               checkCurrentSessionInfo();
               break;
            case 'sessioninfo':
               setCurrentSessionInfo(resourceresponse.uniqueid, resourceresponse.type, resourceresponse.principal.uniqueid, resourceresponse.principal.contextid);
               break;
            case 'subjects':
               displaySubjects(resourceresponse);
               checkCurrentSessionInfo();
               break;
            case 'subject':
               displaySubject(resourceresponse);
               checkCurrentSessionInfo();
               break;
            default:
               $('userMessage').update("Invalid Resource Type : "+resourcetype);
         }

      },
      onFailure:function(){
         $('userMessage').update('ERROR: xmlhttprequest failed');
      }
   });

}
