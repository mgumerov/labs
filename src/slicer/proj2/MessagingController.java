package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.security.Principal;

@Controller
public class MessagingController {

  public final static String PAGE_USER_MGMT = "users.do";
  public final static String PAGE_MESSAGING = "msg.do";
  public final static String PAGE_MSG_TABLE = "msg.table.do";

  private MessageManagement messageManagement;
  public void setMessageManagement(final MessageManagement messageManagement) {
    this.messageManagement = messageManagement;
  }

  @RequestMapping("/"+PAGE_MESSAGING)
  public ModelAndView getMessages(final HttpServletRequest request) {
    return new ModelAndView("messaging.messages", "sortKey", MessageManagement.SortKey.TIMESTAMP);
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