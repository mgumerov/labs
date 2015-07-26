package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.security.Principal;

@Controller
public class MessagingController {

  private MessageManagement messageManagement;
  public void setMessageManagement(final MessageManagement messageManagement) {
    this.messageManagement = messageManagement;
  }

  @RequestMapping("/")
  public String getRoot() {
   return "redirect:/msg";
  }

  @RequestMapping("/msg")
  @ResponseBody
  public String getMessages(final HttpServletRequest request) {
    //final Collection<String> messages = userManagement.getMessageSubjects(null);

    //TODO retrieve this via AJAX
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head>");

    sb.append("<script>");
    sb.append("var request;  ");
    sb.append("function requestTable(sortkey)  ");
    sb.append("{  ");
    sb.append("var url=\"msg/table?sort=\"+sortkey;  ");
//    sb.append("alert(url);");
    sb.append("if(window.XMLHttpRequest){ request=new XMLHttpRequest(); }  ");
    sb.append("else if(window.ActiveXObject){ request=new ActiveXObject(\"Microsoft.XMLHTTP\"); }  ");
    sb.append("try {");  
    sb.append("request.onreadystatechange=getInfo;  ");
    sb.append("request.open(\"GET\",url,true);  ");
    sb.append("request.send();  ");
    sb.append("} catch(e)  {  alert(\"Unable to connect to server\");}  ");
    sb.append("} ");
    sb.append("function getInfo(){  ");
    sb.append("if(request.readyState==4){  ");
    sb.append("var val=request.responseText;  ");
    sb.append("document.getElementById('dynamic').innerHTML=val;  ");
    sb.append("} }  ");
    sb.append("</script>");

    sb.append("</head><body><a href=\"j_spring_security_logout\">Logout</a> <a href=\"users\">Admin</a><br/>");
    //sb.append("<font color=\"red\">").append(errorClass).append("</font><br/>")

    sb.append("<span id=\"dynamic\">");
    sb.append(getMessagesTable(MessageManagement.SortKey.TIMESTAMP.toString(), request));  //Propose initial content at once
    sb.append("</span>");

    sb.append("<button onClick=\"requestTable(document.getElementById('sortkey').value)\">Refresh</>");  

    sb.append("</body></html>");
    return sb.toString();
  }

  @RequestMapping("/msg/table")
  @ResponseBody
  public String getMessagesTable(@RequestParam(value="sort") final String _sortKey, final HttpServletRequest request) {
    //TODO perhaps it would be better to create our own UserDetails with isAdmin predicate. Or perhaps no.
    final boolean omniscient = request.isUserInRole("ROLE_ADMIN");

    final MessageManagement.SortKey sortKey = MessageManagement.SortKey.valueOf(_sortKey);
    final Collection<MessageManagement.Message> messages =
      messageManagement.getMessages(omniscient ? null : request.getUserPrincipal().getName(), sortKey);

    final StringBuilder sb = new StringBuilder();
    sb.append("<table>");
    sb.append("<tr>");
    sb.append("<td><a href=\"#\" onclick=\"requestTable('TIMESTAMP');return false\">Sent</a></td>");
    sb.append("<td><b>Sender</b></td>");
    sb.append("<td><a href=\"#\" onclick=\"requestTable('SUBJECT');return false\">Subject</a></td>");
    if (omniscient)
      sb.append("<td><b>Recipient</b></td>");
    sb.append("</tr>");
    for (final MessageManagement.Message message : messages) {
      sb.append("<tr><td>"+message.getSentOn()+"</td><td>"+message.getSender()+"</td><td>"+message.getSubject());
      if (omniscient)
        sb.append("</td><td>"+message.getRecipient()+"</td>");
      sb.append("</td></tr>");
    }
    sb.append("</table>");
    sb.append("<input id=\"sortkey\" type=\"hidden\" value=\"").append(sortKey).append("\"/>");
    return sb.toString();
  }
}