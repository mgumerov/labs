package slicer.proj2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.sql.ResultSet;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Controller
public class MessagingController {

  //private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  //public void setDataSource(final DataSource dataSource) {
  //  this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  //}

  @RequestMapping("/")
  public String getRoot() {
   return "redirect:/msg";
  }

  @RequestMapping("/msg")
  @ResponseBody
  public String getMessages() {
    final StringBuilder sb = new StringBuilder();
    sb.append("<html><head/><body><a href=\"j_spring_security_logout\">Logout</a> <a href=\"users\">Admin</a><br/>");
    sb.append("TODO: Messages view");
    sb.append("</body></html>");
    return sb.toString();
  }
}