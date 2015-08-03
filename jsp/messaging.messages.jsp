<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:url var="urlRenderTable" value="/msg.table.do"/>
<c:url var="urlLogout" value="/j_spring_security_logout"/>
<c:url var="urlAdmin" value="/users.do"/>

<html>
<head>
<script src="resources/jquery-1.11.3.min.js"></script>
<script type="text/javascript">   
function requestTable(sortkey)  
{  
    jQuery.ajax({  
      type: "GET",  
      dataType: "html",  
      url: "${urlRenderTable}",
      data: {sort: sortkey}, //An efficient way to automate HTML-, URL- and JS- escaping: delegate full GET-URL construction to jQuery
      success: function(html) {  
          document.getElementById('dynamic').innerHTML=html;  
      },  
      error: function() {  
          alert('Failure to load list of messages');
      }  
    });  
}
</script>
</head>

<!--TODO: request data only and format them here, instead of requesting formatted data. Also in that case we can put column def + sort urls right in this JSP, not in another one.-->
<body>
<a href="${urlLogout}">Logout</a> <a href="${urlAdmin}">Admin</a><br/>
<!--font color="red"><c:out value="${errorClass}" escapeXml="true"/></font><br/-->
<span id="dynamic">
<!--renderMessagesTable("requestTable", MessageManagement.SortKey.TIMESTAMP, getUsernameFilter(request))); Propose initial content at once-->
<input id="sortkey" type="hidden" value="${fn:escapeXml(sortKey)}"/>
</span>
<button onClick="requestTable(document.getElementById('sortkey').value)">Refresh</button>
</body>
</html>
