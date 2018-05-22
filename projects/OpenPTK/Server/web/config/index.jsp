<%@include file="header.jsp"%>
<!-- Begin Main Panel -->
<div id="mainpaneladmin">

   <div id="leftborder">

   </div>
   <div id="tabs1" class="tabset">
      <ul class="tabset_tabs">
         <li class="active"><a href="#tab-zero">Home</a></li>
         <li><a href="#tab-one">Contexts</a></li>
         <li><a href="#tab-two">Engine</a></li>
         <li><a href="#tab-three">Clients</a></li>
      </ul>
      <div class="tabset_content_container">
         <div id="tab-zero" class="tabset_content">
            <table width="100%">
               <tbody>
                  <tr>
                     <td>
                        <img src="../images/OpenPTK.png">
                     </td>
                     <td>
                        <br>
                        Select the tabs above to review the running server configuration.
                        <br>
                        <br>
                        See the links below for additional Project OpenPTK references.
                     </td>
                  </tr>
                  <tr><td colspan="2"><hr></td></tr>
                  <tr>
                     <td colspan="2">
                        <center>
                           <table border="0" cellpadding="2" cellspacing="10">
                              <tr>
                                 <td align="right"><a href="http://java.net/projects/openptk" >Information</a>&nbsp;-</td>
                                 <td>Learn about Project OpenPTK and the community.</td>
                              </tr>
                              <tr>
                                 <td align="right"><a href="http://java.net/projects/openptk/downloads" >Downloads</a>&nbsp;-</td>
                                 <td>Download an OpenPTK build and accompanying documentation.</td>
                              </tr>
                              <tr>
                                 <td align="right"><a href="http://java.net/jira/browse/OPENPTK" >Issues</a>&nbsp;-</td>
                                 <td>Browse issues (bugs, enhancements, tasks) related to OpenPTK</td>
                              </tr>
                              <tr>
                                 <td align="right"><a href="http://docs.openptk.org">Documentation</a>&nbsp;-</td>
                                 <td>See all details about Project OpenPTK documentation, from version 1.0 to futures.</td>
                              </tr>
                              <tr>
                                 <td align="right"><a href="http://javadoc.openptk.org">JavaDoc</a>&nbsp;-</td>
                                 <td>Latest Project OpenPTK JavaDocs (<a href="http://javadoc.openptk.org">javadoc.openptk.org</a>)</td>
                              </tr>
                           </table>
                        </center>
                     </td>
                  </tr>
               </tbody>
            </table>
         </div>
         <div id="tab-one" class="tabset_content">
             <table>
                 <tr>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResource("contexts", "contexts" )' title='Return to Top'><img src="../images/clear.png">&nbsp;</a></td>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResourceURLprevious("contexts")' title='Up One Level'><img src="../images/up.png"></a></td>
                 </tr>
             </table>
            <div id="contexts" ></div>
         </div>
         <div id="tab-two" class="tabset_content">      
             <table>
                 <tr>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResource("engine", "engine" )' title='Return to Top'><img src="../images/clear.png">&nbsp;</a></td>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResourceURLprevious("engine")' title='Up One Level'><img src="../images/up.png"></a></td>
                 </tr>
             </table>            
            <div id="engine" > </div>
         </div>
         <div id="tab-three" class="tabset_content">            
             <table>
                 <tr>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResource("clients", "clients")' title='Return to Top'><img src="../images/clear.png">&nbsp;</a></td>
                     <td><a href = "javascript:void(0)" onclick = 'getConfigResourceURLprevious("clients")' title='Up One Level'><img src="../images/up.png"></a></td>
                 </tr>
             </table>             
            <div id="clients" ></div>
         </div>
      </div>
   </div>

   <div id="rightborder">
   </div>
</div>
<!-- End of Main Panel -->
<%@include file="footer.jsp"%>