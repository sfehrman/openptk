


document.observe('dom:loaded', function() {
   new Tab({
      id: "tabs1",
      rounded: 1,
      height: 1
   });

    getConfigResource("contexts", "contexts" );
    getConfigResource("engine", "engine" );
    getConfigResource("clients", "clients" );


});


function init()
{
    $$('#contexts').invoke('observe', 'click', contextsClicked);
    $$('#engine').invoke('observe', 'click', engineClicked);
    $$('#clients').invoke('observe', 'click', clientsClicked);

}
function contextsClicked(evt)
{
    var elm;
    var href;

    elm = evt.element();
    href = elm.href || '(no href)';  
    getConfigResourceURL('contexts', href);
    currenturi = href;      
    evt.stop();
}
function engineClicked(evt)
{
    var elm;
    var href;
        
    elm = evt.element();
    href = elm.href || '(no href)';
    getConfigResourceURL('engine', href);
    currenturi = href;    
    evt.stop();
}
function clientsClicked(evt)
{
    var elm;
    var href;

    elm = evt.element();
    href = elm.href || '(no href)'; 
    getConfigResourceURL('clients', href);
    currenturi = href;       
    evt.stop();
}

document.observe('dom:loaded', init);
	


function displayContext(context)
{
   html = '<h2>Context</h2><ul>\n' +
   '<li>Name: ' + context.response.context.uniqueid + '</li>' +
   "<li>URI:  "+ context.response.uri + '</li>' +
   "<li>Description:  "+ context.response.context.description + '</li>' +
   "<li>Operations:  "+ context.response.context.operations + '</li>' +
   '</ul>';
   $('subject').update(html);
   $$('#subject li:nth-child(2n)').invoke('addClassName', 'alternate');
}

function displayContexts(contexts)
{
   html = "";
   htmltop = '<h2>Contexts</h2>'+
   'Length:  '+ contexts.response.length + "<br>URI:  "+ contexts.response.uri +
   '<ul>\n';
   for (var i=0; i < contexts.response.length; i++)
   {
      html = html + '<li>' +
      contexts.response.results[i].context.uniqueid + ', ' +
      contexts.response.results[i].context.uri + '</li>';
   }
   html = htmltop + html+'</ul>';

   $('subject').update(html);
   $$('#subject li:nth-child(2n)').invoke('addClassName', 'alternate');
}

