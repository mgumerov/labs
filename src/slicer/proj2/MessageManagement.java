package slicer.proj2;

import java.util.Collection;
import java.util.Date;

public interface MessageManagement {

  public class Message {
    private final String subject;
    private final Date sentOn;
    private final String sender;
    private final String recipient;

    public Message(final String subject, final Date sentOn, final String sender, final String recipient) {
      this.subject = subject;
      this.sentOn = (Date)sentOn.clone();
      this.recipient = recipient;
      this.sender = sender;
    }

    public String getSubject() { return subject; }
    public String getRecipient() { return recipient; }
    public String getSender() { return sender; }
    public Date getSentOn() { return (Date)sentOn.clone(); }
  }

  public enum SortKey {
    TIMESTAMP(false),
    SUBJECT(true);

    private final boolean isAscending;
    private SortKey(final boolean isAscending) { this.isAscending = isAscending; }
    public boolean isAscending() { return isAscending; }
  };

  Collection<Message> getMessages(String recipient, SortKey sortKey);
}