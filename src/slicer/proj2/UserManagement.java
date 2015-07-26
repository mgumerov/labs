package slicer.proj2;

import javax.sql.DataSource;
import java.util.Collection;

public interface UserManagement {

  public class UserInfo {
    private final String login;
    private final boolean isAdmin;

    public UserInfo(final String login, final boolean isAdmin) {
      this.login = login;
      this.isAdmin = isAdmin;
    }

    public String getLogin() { return login; }
    public boolean isAdmin() { return isAdmin; }
  }

  Collection<UserInfo> getUsers();
  void promoteUser(String login);
  void demoteUser(String login);
  void createUser(String login, String password);
  void deleteUser(String login);
}