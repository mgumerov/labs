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

public class MessageManagementImpl implements MessageManagement {

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  public void setDataSource(final DataSource dataSource) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  //No use moving this to Spring config unless we are moving all the SQL queries as well
  private final java.util.Map<SortKey, String> sortColumns;
                                                
  public MessageManagementImpl() {
    sortColumns = new java.util.HashMap<SortKey, String>();
    sortColumns.put(SortKey.TIMESTAMP, "sent");
    sortColumns.put(SortKey.SUBJECT, "subject");
  }

  @Override
  public Collection<Message> getMessages(final String recipient, final SortKey sortKey) {
    final String sortColumn = sortColumns.get(sortKey);
    if (sortColumn == null) throw new NullPointerException("Sort order is not supported: " + sortKey);

    final List<Message> messages = namedParameterJdbcTemplate.query(
      "select subject, sent, sender, recipient from messages where recipient = nvl(:recipient, recipient) order by "+sortColumn+" "+ (sortKey.isAscending() ? "ASC" : "DESC"),
      new MapSqlParameterSource().addValue("recipient", recipient),
      new RowMapper<Message>() {
        @Override public Message mapRow(ResultSet rs, int rowNum) throws SQLException { 
          return new Message(rs.getString(1), rs.getTimestamp(2), rs.getString(3), rs.getString(4));
        }
      });
    return Collections.unmodifiableList(messages);
  }
}