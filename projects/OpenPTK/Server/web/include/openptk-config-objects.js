
var baseuri = contextPath+"/resources/";

function getConfigResource(resourcetype, resource) {
   var url = baseuri + resource;
   currenturi=url;
   $('Message').update("");   
   new Ajax.Request(url, {
      method:'get',
      requestHeaders: {
         Accept: 'text/html'
      },
      parameters:'',
      onSuccess: function(transport){

         switch(resourcetype)
         {
            case 'contexts':
               $('contexts').update(transport.responseText);
               break;
            case 'clients':
               $('clients').update(transport.responseText);
               break;
            case 'engine':
               $('engine').update(transport.responseText);
               break;
            default:
               $('Message').update("Invalid Resource Type : "+resourcetype);    
         }

      },
      onFailure:function(){
         $('Message').update('ERROR: xmlhttprequest failed');
      }
   });

}

function getConfigResourceURLprevious(resourcetype) {    
    var baseuritest=null;
    
    if (currenturi !=null){
        previousuri = currenturi.substring( 0, currenturi.lastIndexOf("/") );
        baseuritest = previousuri.substring(previousuri.lastIndexOf('/')+1);
       
        if (baseuritest != "resources"){
            getConfigResourceURL(resourcetype, previousuri);
            currenturi = previousuri;
        }
        else
        {
                $('Message').update("Already at top of resource URI: "+baseuritest);
        }
    }
}

function getConfigResourceURL(resourcetype, resourceURL) {
   var url = resourceURL;
   $('Message').update("");
   new Ajax.Request(url, {
      method:'get',
      requestHeaders: {
         Accept: 'text/html'
      },
      parameters:'',
      onSuccess: function(transport){

         switch(resourcetype)
         {
            case 'context':
               displayContext(resourceresponse);
               break;
            case 'contexts':
               $('contexts').update(transport.responseText);
               break;
            case 'clients':
               $('clients').update(transport.responseText);
               break;
            case 'engine':
               $('engine').update(transport.responseText);
               break;
            default:
               $('Message').update("Invalid Resource Type : "+resourcetype);
         }

      },
      onFailure:function(){
         $('Message').update('ERROR: xmlhttprequest failed');
      }
   });

}
