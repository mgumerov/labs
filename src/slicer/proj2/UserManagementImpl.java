package slicer.proj2;

//java 1.7
import org.springframework.jdbc.core.RowMapper;
import java.sql.SQLException;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.sql.ResultSet;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class UserManagementImpl implements UserManagement {

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  @Override
  public Collection<UserInfo> getUsers() {
    final List<UserInfo> users = namedParameterJdbcTemplate.query("select username, isadmin from users order by username", Collections.<String,String>emptyMap(),
      new RowMapper<UserInfo>() {
        @Override public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException { return new UserInfo(rs.getString(1), rs.getBoolean(2)); }
      });
    return Collections.unmodifiableList(users);
  }

  @Override
  public void promoteUser(final String login) {
    namedParameterJdbcTemplate.update("update users set isadmin = 1 where username=:username",
      new MapSqlParameterSource("username", login));
  }

  @Override
  public void demoteUser(final String login) {
    namedParameterJdbcTemplate.update("update users set isadmin = 0 where username=:username",
      new MapSqlParameterSource("username", login));
  }

  @Override
  public void createUser(final String login, final String password) {
    //TODO restrict allowed characters in a login
    namedParameterJdbcTemplate.update("insert into users(username, password, isadmin) values(:username, :password, 0)",
      new MapSqlParameterSource().addValue("username", login).addValue("password", password));
  }

  @Override
  public void deleteUser(final String login) {
    namedParameterJdbcTemplate.update("delete from users where username=:username",
      new MapSqlParameterSource("username", login));
  }
}