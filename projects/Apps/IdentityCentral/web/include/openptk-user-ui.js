
function displaySubjects(subjects)
{

   var previoussubs;
   var nextsubs;
   var html = '';
   var htmlbegin = '<div id="firstsubjectdisabled"></div>';
   var htmlprev = '<div id="previoussubjectdisabled"></div>';
   var htmlnext = '<div id="nextsubjectdisabled"></div>';
   var htmlend = '<div id="lastsubjectdisabled"></div>';

   subjecttabs.toggleTab('0');
   $('listnav').style.display='none';

   if (subjects)
   {
      if (subjects.quantity>0)
      {
         htmltop = '<b>Found:  '+subjects.length+'</b><br>';
         for (var i=0; i < subjects.quantity; i++)
         {
            html = html + "<div id='subject"+i+"'><img src=\"../images/id.png\" id='subjectpreview"+i+"' border=\"0\" />&nbsp;<a href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subjects.results[i].uniqueid+"/views/extended/');return false;\">" +
            subjects.results[i].attributes.lastcommafirst +  ' ' +  '</a> </div>';
         }
         html = htmltop + html ;

         if (subjects.quantity != subjects.length){
            html = html + '<h3>'+ (subjects.offset+1) + ' - '+ (subjects.offset+subjects.quantity)+' of  '+subjects.length+'</h3>'

            if (subjects.offset-searchquantity-1 >= 0){
               htmlbegin = '<a href="#" onClick=\"paginateResults(0);return false;\"><div title="Begining" id="firstsubject"></div></a>   ';
            }
            if (subjects.offset-searchquantity >= 0){
               previoussubs = subjects.offset-searchquantity;
               htmlprev = '<a href="#" onClick=\"paginateResults('+previoussubs + ');return false;\"><div title="Previous" id="previoussubject"></div></a>';
            }
            if (subjects.offset+subjects.quantity <= subjects.length){
               nextsubs = subjects.offset+subjects.quantity;
               if (nextsubs < subjects.length){
                  htmlnext = '   <a href="#" onClick=\"paginateResults('+ nextsubs + ');return false;\"><div title="Next" id="nextsubject"></div></a>';

                  if (subjects.length - nextsubs > searchquantity){
                     nextsubs = subjects.length - searchquantity;
                     htmlend = '   <a href="#" onClick=\"paginateResults('+ nextsubs + ');return false;\"><div title="End" id="lastsubject"></div></a>';
                  }
               }
            }

            $('listnav').update(htmlbegin + htmlprev + htmlnext + htmlend);
            $('listnav').style.display='block';

         }

         if (subjects.length==1) {
            //set the current subject
            currentsubjectid = subjects.results[0].uniqueid;
            currentsubject = subjects.results[0];
            //Only 1 record found, display it
            getResource('subject', 'contexts/'+serverContext+'/subjects/'+subjects.results[0].uniqueid+'/views/extended/');
         }

      }
   }
   else{
      html = "No results Found";
   }

   $('results').update(html);

   //passing mediacontext could be done
   var hb0 = new HelpBalloon({
      title: subjects.results[0].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[0].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview0'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb1 = new HelpBalloon({
      title: subjects.results[1].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[1].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview1'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb2 = new HelpBalloon({
      title: subjects.results[2].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[2].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview2'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb3 = new HelpBalloon({
      title: subjects.results[3].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[3].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview3'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb4 = new HelpBalloon({
      title: subjects.results[4].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[4].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview4'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb5 = new HelpBalloon({
      title: subjects.results[5].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[5].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview5'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb6 = new HelpBalloon({
      title: subjects.results[6].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[6].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview6'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb7 = new HelpBalloon({
      title: subjects.results[7].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[7].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview7'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb8 = new HelpBalloon({
      title: subjects.results[8].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[8].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview8'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });
   var hb9 = new HelpBalloon({
      title: subjects.results[9].attributes.lastcommafirst ,
      dataURL: 'userPreview.jsp?user='+subjects.results[9].uniqueid+'&context='+serverContext+'&mediacontext='+mediaContext,
      icon: $('subjectpreview9'),
      autoHideTimeout: 2000,
      useEvent: ['mouseover'],
      cacheRemoteContent: false
   });

}

function displaySubject(subject)
{
   var html="";
   var fpwdhtml= "";
   if ($('upphotodiv')!=null) {
      $('upphotodiv').remove();
   }
   $('subjectmore').hide();

   subjecttabs.toggleTab('0');

   $('subject').style.display='block';
   $('details').style.display='block';

   //initially hide the links which require authorization
   $('subjectedit').style.display='none';
   $('subjectdelete').style.display='none';
   $('subjectchangepassword').style.display='none';
   $('subjectresetpassword').style.display='none';
   $('subjectcontrols').style.display='none';
   //Hide the org tree viewer
   $('orgsubjectlight').style.display='none';
   $('orgsubjectfade').style.display='none';

   //set the current subject
   currentsubjectid = subject.subject.uniqueid;
   currentsubject = subject;

   //Subject
   html= '<table><tr><td>';
   html = html + '<div id="userPhotoArea"></div>';
   html = html + '</td><td>';

   html = html + '<b>'+ subject.subject.attributes.firstname + ' '+ subject.subject.attributes.lastname + '</b>';
   if (subject.subject.attributes.title) {
      html = html +  '<br>' + subject.subject.attributes.title ;
   }

   //This is for location information in the extended view fron another referenced object
   if (subject.relationships!=null) {
      if (subject.relationships.location) {

         for (var i=0; i < subject.relationships.location.results.length; i++)
         {

            if (subject.relationships.location.results[i].attributes.street) {
               html = html + '<br>' +  subject.relationships.location.results[i].attributes.street;
            }
            if (subject.relationships.location.results[i].attributes.city) {
               html = html + '<br>' +  subject.relationships.location.results[i].attributes.city;
            }
            if (subject.relationships.location.results[i].attributes.state) {
               html = html + ', ' +  subject.relationships.location.results[i].attributes.state;
            }
            if (subject.relationships.location.results[i].attributes.postalCode) {
               html = html + '  ' +  subject.relationships.location.results[i].attributes.postalCode;
            }

         }

      }
   }

   //This is for location information on the subject object
   if (subject.subject.attributes.street) {
      html = html + '<br>' +  subject.subject.attributes.street;
   }
   if (subject.subject.attributes.city) {
      html = html + '<br>' +  subject.subject.attributes.city;
   }
   if (subject.subject.attributes.state) {
      html = html + ', ' +  subject.subject.attributes.state;
   }
   if (subject.subject.attributes.postalCode) {
      html = html + '  ' +  subject.subject.attributes.postalCode;
   }
   if (subject.subject.attributes.telephone) {
      html = html + '<br><br>' +  subject.subject.attributes.telephone;
   }
   if (subject.subject.attributes.email) {
      html = html + '<br><a href="mailto:'+ subject.subject.attributes.email+'">'+subject.subject.attributes.email+'</a>' ;
   }

   html= html+ '</tr></table>';

   html= html+ '<a id=\'showsubjectmore\' title="Show User Details" href="#" onclick="$(\'hidesubjectmore\').show(); $(\'showsubjectmore\').hide(); Effect.BlindDown(\'subjectmore\'); return false;"><img src="../images/less2.png" border="0"></a><a id=\'hidesubjectmore\' title="Hide User Details" href="#" onclick="$(\'hidesubjectmore\').hide(); $(\'showsubjectmore\').show(); Effect.BlindUp(\'subjectmore\'); return false;"><img src="../images/more2.png" border="0"></a>';


   $('subject').update(html);
   $('hidesubjectmore').hide();
   html='<table>';

   if (subject.subject.uniqueid) {
      html = html + '<tr><td class=\"dimlabel\" align=\"right\"><i>Uniqueid: </i></td><td>' +  subject.subject.uniqueid +'</td></tr>';
   }
   if (subject.subject.attributes.roles) {
      html = html + '<tr><td class=\"dimlabel\" align=\"right\"><i>Roles: </i></td><td>' +  subject.subject.attributes.roles +'</td></tr>';
   }
   if (subject.subject.attributes.organization) {
      html = html + '<tr><td class=\"dimlabel\" align=\"right\"><i>Organization: </i></td><td>' +  subject.subject.attributes.organization+'</td></tr>';
   }
   if (subject.subject.attributes.manager) {
      html = html + '<tr><td class=\"dimlabel\" align=\"right\"><i>Manager: </i></td><td>' +  subject.subject.attributes.manager+'</td></tr>';
   }
   if (subject.subject.attributes.location) {
      html = html + '<tr><td class=\"dimlabel\" align=\"right\"><i>location: </i></td><td>' +  subject.subject.attributes.location+'</td></tr>';
   }
   html= html + '<table>';
   $('subjectmore').update(html);

   Form.Element.setValue($($("updateSubjectForm")['subjectupdateuniqueid']), subject.subject.uniqueid);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatefirstname']), subject.subject.attributes.firstname);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatelastname']), subject.subject.attributes.lastname);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatetitle']), subject.subject.attributes.title);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatetelephone']), subject.subject.attributes.telephone);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateemail']), subject.subject.attributes.email);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatemanager']), subject.subject.attributes.manager);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateroles']), subject.subject.attributes.roles);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateorganization']), subject.subject.attributes.organization);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatestreet']), subject.subject.attributes.street);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatecity']), subject.subject.attributes.city);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatestate']), subject.subject.attributes.state);
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatepostalCode']), subject.subject.attributes.postalCode);

   Form.Element.setValue($($("updateSubjectForm")['fpnumquestions']), subject.subject.attributes.forgottenPasswordQuestions.length);
   fpwdhtml='';

   for (var i=0; i < subject.subject.attributes.forgottenPasswordQuestions.length; i++)
   {
      fpwdhtml = fpwdhtml + "<tr><td align=\"right\"><i>"+subject.subject.attributes.forgottenPasswordQuestions[i]+'<input type="hidden" id="q'+i+'"  value="'+subject.subject.attributes.forgottenPasswordQuestions[i]+'"/>';
      fpwdhtml = fpwdhtml + '</i></td><td>';
      if (subject.subject.attributes.forgottenPasswordAnswers)
      {
         fpwdhtml = fpwdhtml + '<input TYPE="password" id="a'+i+'" size="20" value="'+subject.subject.attributes.forgottenPasswordAnswers[i]+'"/>';
      }
      else {
         fpwdhtml = fpwdhtml + '<input TYPE="password" id="a'+i+'" size="20" />';
      }
      fpwdhtml = fpwdhtml + '</td></tr>';

   }
   $('fpwdupdate').update('<table>'+fpwdhtml+'</table>');


   $('subjectcontrols').style.display='block';
   $('details').style.display='block';

   displayOrgInfo();

   $('resetdetails').style.display='none';
   $('userPhotoArea').style.display='none';

   var currDate = new Date();
   var cardPhotoUrl = contextPath+'/resources/contexts/'+photoContext+'/subjects/'+currentsubjectid+'/relationships/cardphoto?time='+currDate.getTime();

   $('userPhotoArea').style.display='block';
   var updatePhotoFields = '<div class="photoCard"><div class="photoCard-content"><img id="userPhotoImg" src="'+cardPhotoUrl+'" alt="no image" width="100" height="120" border="0"/></div></div><div id="photostatus"></div>';
   $('userPhotoArea').update(updatePhotoFields+'<br>');
   getPhoto(currentsubjectid);

   //Let the user edit their record
   if(openptkuser == subject.subject.uniqueid) {

      $('subjectedit').style.display='block';
      $('subjectchangepassword').style.display='block';
      Form.Element.setValue($("changepasswordSubjectForm")['uniqueid'], subject.subject.uniqueid);
      Form.Element.setValue($("resetPasswordSubjectForm")['resetuniqueid'], subject.subject.uniqueid);

      var subjectupdatefirstnamefield = new LiveValidation( 'subjectupdatefirstname', {
         validMessage: ""
      } );
      subjectupdatefirstnamefield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectupdatelastnamefield = new LiveValidation( 'subjectupdatelastname', {
         validMessage: ""
      } );
      subjectupdatelastnamefield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectupdateemailfield = new LiveValidation( 'subjectupdateemail', {
         validMessage: ""
      });
      subjectupdateemailfield.add( Validate.Email );
      //subjectupdateemailfield.add( Validate.Presence );

      var subjectpasswordfield = new LiveValidation( 'subjectpassword', {
         validMessage: "",
         onlyOnSubmit: true
      } );
      subjectpasswordfield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectconfpasswordfield = new LiveValidation('subjectconfpassword', {
         validMessage: ""
      });
      subjectconfpasswordfield.add( Validate.Confirmation, {
         failureMessage: "Passwords must match!",
         match: 'subjectpassword'
      } );
      subjectconfpasswordfield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );

      $('subjectcontrols').style.display='block';

      $('userPhotoArea').style.display='block';

      var updatePhotoFields = '<div class="photoCard"><a href="#" id="photobutton" class="photoCard-content-link"><img id="userPhotoImg" src="'+cardPhotoUrl+'" alt="no image" width="100" height="120" border="0"/></a></div></div><div id="photostatus">';

      $('userPhotoArea').update(updatePhotoFields);
      getPhoto(currentsubjectid);
      uploadPhotoEvent(currentsubjectid);
   }

   //Let the configuration superuser create edit delete anyone
   if(openptksessiontype.toLowerCase() == 'system') {

      $('subjectcreate').style.display='block';
      $('subjectedit').style.display='block';
      $('subjectdelete').style.display='block';
      $('subjectchangepassword').style.display='block';
      $('subjectresetpassword').style.display='block';
      Form.Element.setValue($("changepasswordSubjectForm")['uniqueid'], subject.subject.uniqueid);
      Form.Element.setValue($("resetPasswordSubjectForm")['resetuniqueid'], subject.subject.uniqueid);

      var subjectupdatefirstnamefield = new LiveValidation( 'subjectupdatefirstname', {
         validMessage: ""
      } );
      subjectupdatefirstnamefield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectupdatelastnamefield = new LiveValidation( 'subjectupdatelastname', {
         validMessage: ""
      } );
      subjectupdatelastnamefield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectupdateemailfield = new LiveValidation( 'subjectupdateemail', {
         validMessage: ""
      });
      subjectupdateemailfield.add( Validate.Email );
      //subjectupdateemailfield.add( Validate.Presence );

      var subjectpasswordfield = new LiveValidation( 'subjectpassword', {
         validMessage: "",
         onlyOnSubmit: true
      } );
      subjectpasswordfield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );
      var subjectconfpasswordfield = new LiveValidation('subjectconfpassword', {
         validMessage: ""
      });
      subjectconfpasswordfield.add( Validate.Confirmation, {
         failureMessage: "Passwords must match!",
         match: 'subjectpassword'
      } );
      subjectconfpasswordfield.add( Validate.Presence, {
         failureMessage: "Required!"
      } );

      var delhtml = '<br><b>Are you sure you want to Delete user<br>  UserId: '+subject.subject.uniqueid+'?<br></b>' + "<br><a href='#' onClick=\"deleteSubject('"+ subject.subject.uniqueid+"');return false;\">Yes</a><br><br>";
      $('deleteconfirm').update(delhtml);
      $('subjectcontrols').style.display='block';

      $('userPhotoArea').style.display='block';

      var updatePhotoFields = '<div class="photoCard"><a href="#" id="photobutton" class="photoCard-content-link"><img id="userPhotoImg" src="'+cardPhotoUrl+'" alt="no image" width="100" height="120" border="0"/></a></div><div id="photostatus"></div>';

      $('userPhotoArea').update(updatePhotoFields);
      getPhoto(currentsubjectid);
      uploadPhotoEvent(currentsubjectid);
   }

}

function uploadPhotoEvent(subjectid) {
   var currDate = new Date();
   var photoUrl = contextPath+'/resources/contexts/'+photoContext+'/subjects/'+subjectid+'/relationships/photo';
   var cardPhotoUrl = contextPath+'/resources/contexts/'+photoContext+'/subjects/'+subjectid+'/relationships/cardphoto?time='+currDate.getTime();

   new AjaxUpload('photobutton', {
      action: photoUrl,
      name: 'media',
      onSubmit : function(file, ext){
         // Allow only images. You should add security check on the server-side.
         if (ext.toLowerCase() && /^(jpg|png|jpeg|gif)$/.test(ext.toLowerCase())){

            // change button text, when user selects file
            $('photostatus').update('');

            // If you want to allow uploading only 1 file at time,
            // you can disable upload button
            this.disable();

            // Uploding -> Uploading. -> Uploading...
            interval = window.setInterval(function(){
               var text = 'Uploading';//button.$('status');
               if (text.length < 13){
                  $('photostatus').update(text + '.....');
               } else {
                  $('photostatus').update('Uploading');
               }
            }, 200);

         } else {
            // extension is not allowed
            $('photostatus').update('Error: only images are allowed');
            return false;
         }

      },
      onComplete : function(file){
         $('photostatus').update('Upload Complete!');

         window.clearInterval(interval);

         $('photostatus').update("Success! ");

         // enable upload button
         this.enable();
         getResource('subject', 'contexts/'+serverContext+'/subjects/'+subjectid+'/views/extended/');

      }
   });

}

function getPhoto(subjectid) {
   var currDate = new Date();
   var cardPhotoUrl = contextPath+'/resources/contexts/'+photoContext+'/subjects/'+subjectid+'/relationships/cardphoto?time='+currDate.getTime();

   $('userPhotoImg').src=cardPhotoUrl;

   new Ajax.Request(cardPhotoUrl, {
      method:'get',
      requestHeaders: {
         Accept: 'application/json'
      },
      onSuccess: function(){
      //due to IE Issue, image displayed above, overridden if not found, below
      //$('userPhotoImg').src=cardPhotoUrl;
      },
      onFailure:function(){
         $('userPhotoImg').src="../images/person.png";
      }
   });

}


function getMyProfile() {
   getResource("subject", "contexts/"+serverContext+"/subjects/"+openptkuser+'/views/extended/');
}


function displayOrgInfo() {
   var html='';
   var htmltop = '';

   var subject=currentsubject;

   //Subject
   html= '<table><tr><td><img src="../images/person.png" border="0"></td><td>';
   html = html + '<b>'+ subject.subject.attributes.firstname + ' '+ subject.subject.attributes.lastname + '</b>';
   if (subject.subject.attributes.title) {
      html = html +  '<br>' + subject.subject.attributes.title ;
   }

   //This is for location information in the extended view fron another referenced object
   if (subject.relationships!=null) {
      if (subject.relationships.location) {

         for (var i=0; i < subject.relationships.location.results.length; i++)
         {

            if (subject.relationships.location.results[i].attributes.street) {
               html = html + '<br>' +  subject.relationships.location.results[i].attributes.street;
            }
            if (subject.relationships.location.results[i].attributes.city) {
               html = html + '<br>' +  subject.relationships.location.results[i].attributes.city;
            }
            if (subject.relationships.location.results[i].attributes.state) {
               html = html + ', ' +  subject.relationships.location.results[i].attributes.state;
            }
            if (subject.relationships.location.results[i].attributes.postalCode) {
               html = html + '  ' +  subject.relationships.location.results[i].attributes.postalCode;
            }

         }

      }
   }

   //This is for location information on the subject object
   if (subject.subject.attributes.street) {
      html = html + '<br>' +  subject.subject.attributes.street;
   }
   if (subject.subject.attributes.city) {
      html = html + '<br>' +  subject.subject.attributes.city;
   }
   if (subject.subject.attributes.state) {
      html = html + ', ' +  subject.subject.attributes.state;
   }
   if (subject.subject.attributes.postalCode) {
      html = html + '  ' +  subject.subject.attributes.postalCode;
   }
   if (subject.subject.attributes.telephone) {
      html = html + '<br><br>' +  subject.subject.attributes.telephone;
   }
   if (subject.subject.attributes.email) {
      html = html + '<br><a href="mailto:'+ subject.subject.attributes.email+'">'+subject.subject.attributes.email+'</a>' ;
   }

   html= html+ '</td></tr></table>';

   $('oc-subject').update(html);


   //Reports To
   html= '';
   if (subject.relationships!=null) {
      if (subject.relationships.organization) {

         htmltop = '<ul id="reportsTosubject">';
         for (var i=0; i < subject.relationships.organization.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.organization.results[i].attributes.lastcommafirst+"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.organization.results[i].uniqueid+"/views/extended/');return false;\">" + subject.relationships.organization.results[i].attributes.lastcommafirst +  ' </a>';
         }
         html = htmltop + html +"</ul>";
      }
   }

   $('tab-reportsTo').update(html);

   //Peers
   html = '';
   htmltop = '';
   $('peers').style.display='none';
   if (subject.relationships!=null) {
      if (subject.relationships.peers) {

         htmltop = '<b>Quantity:  '+subject.relationships.peers.results.length+'</b><ul id="peerssubject">';
         for (var i=0; i < subject.relationships.peers.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.peers.results[i].attributes.lastcommafirst+"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.peers.results[i].uniqueid+"/views/extended/');return false;\">" +
            subject.relationships.peers.results[i].attributes.lastcommafirst + '</a>';
         }
         html = htmltop + html + "</ul>";
         $('tab-peers').update(html);
         $('peers').style.display='block';
      }
   }

   //directReports
   html= '';
   htmltop = '';
   $('directReports').style.display='none';
   if (subject.relationships!=null) {
      if (subject.relationships.directReports) {

         htmltop = '<b>Quantity:  '+subject.relationships.directReports.results.length+'</b><ul id="directReportssubject">';
         for (var i=0; i < subject.relationships.directReports.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.directReports.results[i].attributes.lastcommafirst+"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.directReports.results[i].uniqueid+"/views/extended/');return false;\">" +
            subject.relationships.directReports.results[i].attributes.lastcommafirst +    '</a>';
         }
         html = htmltop + html + "</ul>";
         $('tab-directReports').update(html);
         $('directReports').style.display='block';
      }
   }

   displayOrgChartInfo();
}

function displayOrgChartInfo() {
   var html='';
   var htmltop = '';

   var subject=currentsubject;

   //Reports To
   html= '';
   if (subject.relationships!=null) {
      if (subject.relationships.organization) {

         htmltop = '<b>Reports To:  </b><ul id="oc-reportsTosubject">';
         for (var i=0; i < subject.relationships.organization.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.organization.results[i].attributes.lastcommafirst+"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.organization.results[i].uniqueid+"/views/extended/');return false;\">" + subject.relationships.organization.results[i].attributes.lastcommafirst +  ' </a>';
         }
         html = htmltop + html +"</ul>";
      }
   }

   $('reportsTo').update(html);

   //Peers
   html = '';
   htmltop = '';
   $('peers').style.display='none';
   if (subject.relationships!=null) {
      if (subject.relationships.peers) {

         htmltop = '<b>Peers:  '+subject.relationships.peers.results.length+'</b><ul id="oc-peerssubject">';
         for (var i=0; i < subject.relationships.peers.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.peers.results[i].attributes.lastcommafirst+"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.peers.results[i].uniqueid+"/views/extended/');return false;\">" +
            subject.relationships.peers.results[i].attributes.lastcommafirst +    '</a>';
         }
         html = htmltop + html + "</ul>";
         $('peers').update(html);
         $('peers').style.display='block';
      }
   }

   //directReports
   html= '';
   htmltop = '';
   $('directReports').style.display='none';
   if (subject.relationships!=null) {
      if (subject.relationships.directReports) {

         htmltop = '<b>Direct Reports:  '+subject.relationships.directReports.results.length+'</b><ul id="oc-directReportssubject">';
         for (var i=0; i < subject.relationships.directReports.results.length; i++)
         {

            html = html + "<li><a title=\"User: "+subject.relationships.directReports.results[i].attributes.lastcommafirst +"\" href='#' onClick=\"getResource('subject', 'contexts/"+serverContext+"/subjects/"+subject.relationships.directReports.results[i].uniqueid+"/views/extended/');return false;\">" +
            subject.relationships.directReports.results[i].attributes.lastcommafirst +    '</a>';
         }
         html = htmltop + html + "</ul>";
         $('directReports').update(html);
         $('directReports').style.display='block';
      }
   }


}


function submitform() {
   var thisresource = '';

   $('results').update(' ');

   //clear the existing subject displayed
   clearSubjectDisplay();

   //clear the message
   $('userMessage').update('');


   var form = $('searchForm');
   var offset = form['offset'];
   var searchInput = form['searchVal'];

   if($F(searchInput)){
      thisresource = 'contexts/'+serverContext+'/subjects/?search=' + $F(searchInput);
      getResource('subjects', thisresource);
   }
   else {
      if(searchminchars == "0"){
         //return all users in search only if searchminchars is 0
         thisresource = 'contexts/'+serverContext+'/subjects/';
         getResource('subjects', thisresource);
      }
      else {
         $('userMessage').update('Required search characters: '+searchminchars);
      }


   }
 
}

function clearLogin() {

   //clear login forms
   Form.reset('loginPanelForm');
   Form.reset('fpwdForm1');
   Form.reset('ForgotPassForm');
   Form.reset('fpwdchangePasswordForm');
   Form.reset('registration');

   //hide the login panels

   $('loginpanellight').style.display='none';
   $('loginpanelfade').style.display='none';
   $('forgotpanelinner').style.display='none';
   $('subjectForgotPass').style.display='none';
   $('fpwdchangepassword').style.display='none';
   $('registrationpanellight').style.display='none';
   $('registrationpanelfade').style.display='none'

   clearAll();

}

function clearAll() {
   $('results').update(' ');
   $('listnav').update(' ');

   //clear the existing subject displayed
   clearSubjectDisplay();

   //clear the message
   $('userMessage').update('');

   $('reportsTo').update(' ');
   $('tab-reportsTo').update(' ');

   $('peers').update(' ');
   $('tab-peers').update(' ');

   $('directReports').update(' ');
   $('tab-directReports').update(' ');

   Form.Element.setValue($("searchForm")['searchVal'], '');
   $('searchVal').focus()
}


function paginateResults(offset) {

   var form = $('searchForm');
   var searchInput = form['searchVal'];
   var pagination = "";

   pagination = "&offset="+offset;

   var thisresource = 'contexts/'+serverContext+'/subjects/?search=' + $F(searchInput) + pagination;
   getResource('subjects', thisresource);

}

function clearSubjectDisplay() {
   if ($('upphotodiv')!=null) {
      $('upphotodiv').remove();
   }

   html = ' ';
   $('subject').update(html);

   // Clear subject update fields

   Form.Element.setValue($($("updateSubjectForm")['subjectupdateuniqueid']),'');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatefirstname']),'');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatelastname']),'');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatetitle']),'');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatetelephone']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateemail']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatemanager']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateroles']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdateorganization']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatelocation']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatestreet']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatecity']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatestate']), '');
   Form.Element.setValue($($("updateSubjectForm")['subjectupdatepostalCode']), '');

   $('fpwdupdate').update('');

   $('subject').style.display='none';
   //$('subjectcreate').style.display='none';
   $('subjectedit').style.display='none';
   $('subjectdelete').style.display='none';
   $('subjectchangepassword').style.display='none';
   $('subjectresetpassword').style.display='none';
   $('details').style.display='none';
   $('subjectcontrols').style.display='none';

   resaccordion.toggleReset($('results-accordion-searchtab'));
   subjecttabs.toggleTab("0");
}


function pageLoadTasks() {
   
   getCurrentContext();

   $('searchVal').focus()

   resaccordion = new Accordion({
      id: "results-accordion",
      type: 'vertical'
   });

   subjecttabs = new Tab({
      id: "subject-tabs",
      rounded: 1,
      height: 10
   });

   subjectaccordion = new Accordion({
      id: "subject-accordion",
      type: 'horizontal'
   });

   if (updateLocation == "false")
      {
         $('subject-location-fields').style.display='none';
         $('subject-location-fields2').style.display='none';
         $('subject-location-fields3').style.display='none';
      }

}

document.observe('dom:loaded', pageLoadTasks);
