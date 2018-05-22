<!-- oper_detail.jsp -->

<script type="text/javascript" src="../common/uml-ui.js"></script>
   
<c:if test="${ptkError == 'false'}">

    <ptk:setInput var="myinput"/>
    <ptk:setUniqueId input="myinput" value="${uid}"/>
    <ptk:doRead connection="umlConn" input="myinput" output="myoutput"/>

    <c:if test="${ptkError == 'false'}">

        <form action="operations.jsp">
            <table cellpadding="1" cellspacing="2" border="0" width="100%">
                <tr><td colspan="2"><b>User&nbsp;Details:</b></td></tr>
                <tr>
                    <td align=right width="50%">Id:</td>
                    <td align=left><b>${param.uid}</b></td>
                </tr>
                <tr>
                    <td align=right>First:</td>
                    <td align=left><b><ptk:getValue output='myoutput' name="firstname"/></b></td>
                </tr>
                <tr>
                    <td align=right>Last:</td>
                    <td align=left><b><ptk:getValue output='myoutput' name="lastname"/></b></td>
                </tr>
                <tr>
                    <td align=right>Fullname:</td>
                    <td align=left><b><ptk:getValue output='myoutput' name="fullname"/></b></td>
                </tr>
                <tr>
                    <td align=right>Email:</td>
                    <td align=left>
                        <input type="text" size="24" align="left" name="email" value="<ptk:getValue output='myoutput' name='email'/>"/>
                    </td>
                </tr>
                <tr>
                    <td align=right>Telephone:</td>
                    <td align=left>
                        <input type="text" size="24" align="left" name="telephone" value="<ptk:getValue output='myoutput' name='telephone'/>"/>
                    </td>
                </tr>
                <tr>
                    <td align=right>Title:</td>
                    <td align=left>
                        <input type="text" size="24" align="left" name="title" value="<ptk:getValue output='myoutput' name='title'/>"/>
                    </td>
                </tr>
                <tr>
                    <td align=right>Role:</td>
                    <td align=left>
                        <select id="currentRoles" name="roles" STYLE="Width: 100" multiple>

                            <ptk:getAttribute var="attrRoles" name="roles" output="myoutput"/>
                            <ptk:getValuesList attribute="attrRoles" var="roleValues" sizevar="rolesSize"/>

                            <!-- ******************************************* -->
                            <!-- Loop through the avaiable roles and match up -->
                            <!-- with roles set from service and select      -->
                            <!-- ******************************************* -->
                            <c:forTokens var="availableRole" items="Admin,Business,Consumer" delims=",">
                                <c:set var="selected" value=""/>
                                <c:forEach items="${roleValues}" var="roleValue">
                                    <c:if test="${roleValue == availableRole}">
                                        <c:set var="selected" value="selected"/>
                                    </c:if>
                                </c:forEach>

                                <option ${selected}>${availableRole}</option>

                            </c:forTokens>

                        </select>

                        <a href="#" onclick='UnselectOptions("currentRoles");return false'>Clear Roles</a>

                    </td>
                </tr>
                <tr>
                    <td align=right>Manager:</td>
                    <td align=left>
                        <input type="text" size="24" align="left" name="manager" value="<ptk:getValue output='myoutput' name='manager'/>"/>
                    </td>
                </tr>
                <tr>
                    <td align=right>Organization:</td>
                    <td align=left><b><ptk:getValue output='myoutput' name="organization"/></b></td>
                </tr>
                <tr>
                    <td align=left>
                        <input type="hidden" name="uid" value="${uid}"/>
                        <input type="hidden" name="firstname" value="<ptk:getValue output='myoutput' name='firstname'/>"/>
                        <input type="hidden" name="lastname" value="<ptk:getValue output='myoutput' name='lastname'/>"/>
                        <input type="hidden" name="mode" value="update"/>
                    </td>
                    <td>
                        <input type="submit" value="Update" onclick="return(window.confirm('Are you sure, UPDATE this record?'));"/>
                    </td>
                </tr>
            </table>
        </form>
    </c:if>
</c:if>