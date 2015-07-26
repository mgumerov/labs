package slicer.proj2;

import java.util.Collection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

  private UserManagement userManagement;
  public void setUserManagement(final UserManagement userManagement) {
    this.userManagement = userManagement;
  }

  @RequestMapping("/users")
  @ResponseBody
  //я не обещал хорошую организацию gui; начну с такой
  public String getUsers(@ModelAttribute("errorClass") final String errorClass) {
    final Collection<UserManagement.UserInfo> users = userManagement.getUsers();

    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("<a href=\"j_spring_security_logout\">Logout</a> <a href=\"msg\">Messaging</a><br/>");
    sb.append("<font color=\"red\">").append(errorClass).append("</font><br/>");
    sb.append("<table>");
    for (final UserManagement.UserInfo user : users) {
      final String action = user.isAdmin() ? "Demote" : "Promote";
      sb.append("<tr><td>").append(user.getLogin()).append("</td>");
      //сорри, некогда тратить врем€ на html-кодирование и url-кодирование логина, лучше будем считать что он english alphanumeric
      //ј вообще, необходимость €вного escaping зависит от конкретного фронтэнда, например вроде бы JSP сам понимает, что нужно
      //при подстановке выполн€ть html-encoding? там вроде и дл€ url какой-то серверный тег есть.
      sb.append("<td><a href='users/delete?login="+user.getLogin()+"'\">Delete</a></td>");
      sb.append("<td><a href='users/"+action+"?login="+user.getLogin()+"'\">"+action+"</a></td>");
      sb.append("</td></tr>");
    }
    sb.append("</table>");
    sb.append("<form method=\"get\" action=\"users/add\"><input name=\"login\"/><input name=\"password\"/><input type=\"submit\" value=\"Add\"/></form>");
    sb.append("</body></html>");
    return sb.toString();
  }

  @RequestMapping("/users/Promote")
  public String getPromote(@RequestParam("login") final String login) {
    userManagement.promoteUser(login);
    return "redirect:/users";
  }

  @RequestMapping("/users/Demote")
  public String getDemote(@RequestParam("login") final String login) {
    userManagement.demoteUser(login);
    return "redirect:/users";
  }

  @RequestMapping("/users/add")
  public String getAdd(@RequestParam("login") final String login, @RequestParam("password") final String password,
    RedirectAttributes redirectAttributes) {
    try {
      userManagement.createUser(login, password);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorClass", e.getClass().getName());
    }
    return "redirect:/users";
  }

  @RequestMapping("/users/delete")
  public String getDelete(@RequestParam("login") final String login) {
    userManagement.deleteUser(login);
    return "redirect:/users";
  }
}