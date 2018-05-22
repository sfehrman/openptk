<!-- debug.jsp -->

<table class=debug width="100%">
   <tr>
      <td align="right">UniqueId:</td>
      <td>&nbsp</td>
      <td align="left"><b><idm:getUniqueId var="${dump}"/></b></td>
   </tr>
   <tr>
      <td align="right">Category:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="category"/></td>
   </tr>
   <tr>
      <td align="right">Description:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="desc"/></td>
   </tr>
   <tr>
      <td align="right">Debug:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="debug"/></td>
   </tr>
   <tr>
      <td align="right">Debug Level:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="level"/></td>
   </tr>
   <tr>
      <td align="right">Status:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="status"/></td>
   </tr>
   <tr>
      <td align="right">State:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="state"/></td>
   </tr>
   <tr>
      <td align="right">Error:</td>
      <td>&nbsp</td>
      <td align="left"><idm:getMetadata var="${dump}" type="error"/></td>
   </tr>
</table>

