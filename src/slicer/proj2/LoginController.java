package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

//java 1.7
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.sql.ResultSet;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Controller
public class LoginController {

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @RequestMapping("/auth")
  @ResponseBody
  //Не буду делать ссылку на главную страницу, она не имеет смысла - находясь на странице регистрации, мы один 
  //черт не имеем полномочий для просмотра главной страницы
  public String getLoginPage(@RequestParam(value="error", required=false) final String error,
    @RequestParam(value="logout", required=false) final String logout, final HttpServletRequest request)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("Authentication is required<br>");
    if (error != null) {
      final Exception exc = (Exception)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
      sb.append("<font color=\"red\">").append(exc.getMessage()).append("</font><br/>");
    }
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
  public String getNewUserPage() {
    return "redirect:/auth";
  }

  @RequestMapping("/newUser/add")
  public String getAdd(@RequestParam("username") final String username, @RequestParam("password") final String password,
    RedirectAttributes redirectAttributes) {
    try {
      //TODO restrict allowed characters in a login
      namedParameterJdbcTemplate.update("insert into users(username, password, isadmin) values(:username, :password, 0)",
        new MapSqlParameterSource().addValue("username", username).addValue("password", password));
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorClass", e.getClass().getName());
      return "redirect:/newUser";
    }
    return "redirect:/msg";
  }
}