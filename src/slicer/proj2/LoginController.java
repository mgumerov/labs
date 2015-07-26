package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

  private UserManagement userManagement;
  public void setUserManagement(final UserManagement userManagement) {
    this.userManagement = userManagement;
  }

  @RequestMapping("/auth")
  @ResponseBody
  public String getLoginPage(@RequestParam(value="error", required=false) final String error,
    @RequestParam(value="logout", required=false) final String logout, final HttpServletRequest request)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("Authentication is required<br>");
    sb.append("<font color=\"red\">");
    if (error != null) {
      final Exception exc = (Exception)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
      sb.append(exc.getMessage());
    }
    sb.append("</font><br/>");
    sb.append("<form action=\"j_spring_security_check\" method=\"post\">");
    sb.append("<p><label for=\"username\">Username</label><input type=\"text\" id=\"username\" name=\"j_username\"/></p>");
    sb.append("<p><label for=\"password\">Password</label><input type=\"password\" id=\"password\" name=\"j_password\"/></p>");
    sb.append("<button type=\"submit\">Log in</button><br/>");
    sb.append("<a href=\"newUser\">New user</a>");
    sb.append("</form></body></html>");
    return sb.toString();
  }

  @RequestMapping("/loggedOut")
  @ResponseBody
  public String getLogoutConfirmationPage() {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("<font color=\"green\">Logged out</font><br/>");
    sb.append("<a href=\"msg\">Continue</a>");
    sb.append("</body></html>");
    return sb.toString();
  }

  @RequestMapping("/newUser")
  @ResponseBody
  //Не буду делать ссылку на главную страницу, она не имеет смысла - находясь на странице регистрации, мы один 
  //черт не имеем полномочий для просмотра главной страницы; вместо этого сделаю ссылку на страницу логина, что
  //по факту будет давать тот же эффект, но построено будет логичнее
  public String getNewUserPage(@ModelAttribute("errorClass") final String errorClass) {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("<a href=\"auth\">Back to login page</a><br/>");
    sb.append("<font color=\"red\">").append(errorClass).append("</font><br/>");
    sb.append("<form action=\"newUser/add\" method=\"post\">");
    sb.append("<p><label for=\"username\">Username</label><input type=\"text\" id=\"username\" name=\"username\"/></p>");
    sb.append("<p><label for=\"password\">Password</label><input type=\"password\" id=\"password\" name=\"password\"/></p>");
    sb.append("<button type=\"submit\">Create</button><br/>");
    sb.append("</form></body></html>");
    return sb.toString();
  }

  @RequestMapping("/newUser/add")
  public String getAdd(@RequestParam("username") final String username, @RequestParam("password") final String password,
    RedirectAttributes redirectAttributes) {
    try {
      userManagement.createUser(username, password);
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorClass", e.getClass().getName());
      return "redirect:/newUser";
    }
    //TODO maybe return to a page which had been requested before interception with login page (from which we got to New User)
    return "redirect:/auth";
  }
}