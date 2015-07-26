package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
public class AdminController {

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  private class UserInfo {
    private final String login;
    public final boolean isAdmin;

    public UserInfo(final String login, final boolean isAdmin) {
      this.login = login;
      this.isAdmin = isAdmin;
    }

    public String getLogin() { return login; }
    public boolean isAdmin() { return isAdmin; }
  }

  @RequestMapping("/users")
  @ResponseBody
  //я не обещал хорошую организацию gui; начну с такой
  public String getUsers(@ModelAttribute("errorClass") final String errorClass) {
    final List<UserInfo> users = namedParameterJdbcTemplate.query("select username, isadmin from users order by username", Collections.<String,String>emptyMap(),
      new RowMapper<UserInfo>() {
        @Override public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException { return new UserInfo(rs.getString(1), rs.getBoolean(2)); }
      });

    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body>");
    sb.append("<a href=\"j_spring_security_logout\">Logout</a> <a href=\"msg\">Messaging</a><br/>");
    sb.append("<font color=\"red\">").append(errorClass).append("</font><br/>");
    sb.append("<table>");
    for (final UserInfo user : users) {
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
    namedParameterJdbcTemplate.update("update users set isadmin = 1 where username=:username",
      new MapSqlParameterSource("username", login));

    return "redirect:/users";
  }

  @RequestMapping("/users/Demote")
  public String getDemote(@RequestParam("login") final String login) {
    namedParameterJdbcTemplate.update("update users set isadmin = 0 where username=:username",
      new MapSqlParameterSource("username", login));

    return "redirect:/users";
  }

  @RequestMapping("/users/add")
  public String getAdd(@RequestParam("login") final String login, @RequestParam("password") final String password,
    RedirectAttributes redirectAttributes) {
    try {
      //TODO restrict allowed characters in a login
      namedParameterJdbcTemplate.update("insert into users(username, password, isadmin) values(:username, :password, 0)",
        new MapSqlParameterSource().addValue("username", login).addValue("password", password));
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorClass", e.getClass().getName());
    }
    return "redirect:/users";
  }

  @RequestMapping("/users/delete")
  public String getDelete(@RequestParam("login") final String login) {
    namedParameterJdbcTemplate.update("delete from users where username=:username",
      new MapSqlParameterSource("username", login));

    return "redirect:/users";
  }
}