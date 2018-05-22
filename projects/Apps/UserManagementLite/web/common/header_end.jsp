<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!--
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *      Portions Copyright 2007-2009 Sun Microsystems, Inc.
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE
 * or https://openptk.dev.java.net/OpenPTK.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the reference to
 * trunk/openptk/resource/legal-notices/OpenPTK.LICENSE. If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 -->

<!--
 * Project OpenPTK Founders: Scott Fehrman, Derrick Harcey, Terry Sigle
 -->


<!-- -------- BEGIN HEADER TITLEBAR ------------ -->

<table cellspacing="0" cellpadding="0" width="100%" height="75">
    <tr>
         <td valign=top background="../images/header_gradient_top_left.gif" width=9>
            <img src="../images/top_left_round_corner.gif" width=9 height=9 alt="" />
         </td>
         <td valign=middle background="../images/header_gradient_top_left.gif">&nbsp;&nbsp;&nbsp;</td>
         <td valign=middle background="../images/header_gradient_top_left.gif" nowrap="nowrap">
            <div id="headline">    

            <c:if test="${bannertext != null}">                
                 <span class="headline-background"><c:out value="${bannertext}"/>   </span>
                 <span class="headline-foreground"><c:out value="${bannertext}"/>   </span>             
            </c:if>        

            <c:if test="${bannertext == null}">                
                 <span class="headline-background">User Management Lite</span>
                 <span class="headline-foreground">User Management Lite</span>
            </c:if>         
        
        
            </div>
         </td>
         <td valign="center" background="../images/header_gradient_top_left.gif">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
         <td valign="top" width="10"><img src="../images/header_s.gif" width="10" height="75"></td>
         <td valign="top" background="../images/header_gradient_top.gif" width=100%>
            <table cellspacing="0" cellpadding="0" height="75" width="100%">
               <tr>
                  <td>
                     <table cellspacing="0" cellpadding="0" height="75" width="100%">
                        <tr>
                           <td align="right" valign="middle">
                              <c:if test="${(usertype eq 'USER')or(usertype eq 'SYSTEM') }">
                                 <a href="../anon/index.jsp?mode=menu&logout=true" class="greylink" >Logout</a> |
                              </c:if>

                              <span class="small">

                                 <a class="greylink" href="../help">Help</a> &nbsp; &nbsp; &nbsp;
                                 <a class="greylink" href="../anon/index.jsp?mode=config">Config</a> &nbsp;
                              </span>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>
         </td>
         <td valign="top" background="../images/header_gradient_top.gif" width=9><img src="../images/top_right_round_corner.gif" width=9 height=9></td>
      </tr>
 </table>

<!---------- END HEADER TITLEBAR --------------->
