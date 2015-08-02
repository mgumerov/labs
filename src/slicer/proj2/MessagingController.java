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

  private final static String PAGE_USER_MGMT = "users";
  private final static String PAGE_MESSAGING = "msg";
  private final static String PAGE_MSG_TABLE = "msg/table";

  private MessageManagement messageManagement;
  public void setMessageManagement(final MessageManagement messageManagement) {
    this.messageManagement = messageManagement;
  }

  @RequestMapping("/")
  public String getRoot() {
   return "redirect:/msg";
  }

  @RequestMapping("/"+PAGE_MESSAGING)
  @ResponseBody
  public String getMessages(final HttpServletRequest request) {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head>\n");

    sb.append("<script src=\"resources/jquery-1.11.3.min.js\">");
    sb.append("</script>\n");
    sb.append("<script type=\"text/javascript\">   \n");
    sb.append("function requestTable(sortkey)  \n");
    sb.append("{  \n");
    sb.append("    jQuery.ajax({  \n");
    sb.append("      type: \"GET\",  \n");
    sb.append("      dataType: \"html\",  \n");
    sb.append("      url: \"" + PAGE_MSG_TABLE + "?sort=\"+sortkey,  \n");
    sb.append("      success: function(html) {  \n");
    sb.append("          document.getElementById('dynamic').innerHTML=html;  \n");
    sb.append("      },  \n");
    sb.append("      error: function() {  \n");
    sb.append("          alert('Failure to load list of messages');\n");
    sb.append("      }  \n");
    sb.append("    });  \n");
    sb.append("}\n");
    sb.append("</script>\n");

    sb.append("</head><body><a href=\"j_spring_security_logout\">Logout</a> <a href=\"" + PAGE_USER_MGMT + "\">Admin</a><br/>");
    //sb.append("<font color=\"red\">").append(errorClass).append("</font><br/>")

    sb.append("<span id=\"dynamic\">");
    sb.append(renderMessagesTable(MessageManagement.SortKey.TIMESTAMP, getUsernameFilter(request))); //Propose initial content at once
    sb.append("</span>");

    sb.append("<button onClick=\"requestTable(document.getElementById('sortkey').value)\">Refresh</button>");  

    sb.append("</body></html>");
    return sb.toString();
  }

  @RequestMapping("/"+PAGE_MSG_TABLE)
  @ResponseBody
  public String getMessagesTable(@RequestParam(value="sort") final String _sortKey, final HttpServletRequest request) {
    final MessageManagement.SortKey sortKey = MessageManagement.SortKey.valueOf(_sortKey);
    return renderMessagesTable(sortKey, getUsernameFilter(request));
  }

  private String renderMessagesTable(final MessageManagement.SortKey sortKey, final String usernameFilter) {
    final Collection<MessageManagement.Message> messages = messageManagement.getMessages(usernameFilter, sortKey);
    final boolean omniscient = (usernameFilter == null);

    final StringBuilder sb = new StringBuilder();
    sb.append("<table>");
    sb.append("<tr>");
    sb.append("<td><a href=\"#\" onclick=\"requestTable('" + MessageManagement.SortKey.TIMESTAMP + "');return false\">Sent</a></td>");
    sb.append("<td><b>Sender</b></td>");
    sb.append("<td><a href=\"#\" onclick=\"requestTable('" + MessageManagement.SortKey.SUBJECT +"');return false\">Subject</a></td>");
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

  private String getUsernameFilter(final HttpServletRequest request) {
    return request.isUserInRole("ROLE_ADMIN") ? null : request.getUserPrincipal().getName();
  }
}