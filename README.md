Gmail4J - Gmail API for Java
============================
Gmail4J is a simple, object oriented library for accessing Gmail services from Java.

Use with Maven
--------------
Add the dependency to your pom.xml:

```xml
<dependency>
  <groupId>com.googlecode.gmail4j</groupId>
  <artifactId>gmail4j</artifactId>
  <version>0.4</version>
</dependency>
```

Or use the latest and greatest snapshot:

```xml
<dependency>
  <groupId>com.googlecode.gmail4j</groupId>
  <artifactId>gmail4j</artifactId>
  <version>0.5-SNAPSHOT</version>
</dependency>
```

Example code
------------

### Get unread messages

```java
GmailClient client = new RssGmailClient();
GmailConnection connection = new HttpGmailConnection(LoginDialog.getInstance().show("Enter Gmail Login"));
client.setConnection(connection);
final List<GmailMessage> messages = client.getUnreadMessages();
for (GmailMessage message : messages) {
    System.out.println(message);
}
```

### Get messages by subject

```java
GmailClient client = new RssGmailClient();
GmailConnection connection = new HttpGmailConnection(LoginDialog.getInstance().show("Enter Gmail Login"));
client.setConnection(connection);
final List<GmailMessage> messages = client.getMessagesBy(GmailClient.EmailSearchStrategy.SUBJECT,
        "Test mail subject. Unicode: ąžuolėlį");
for (GmailMessage message : messages) {
    System.out.println(message);
}
```

### Get messages by sent date greater than

```java
GmailClient client = new RssGmailClient();
GmailConnection connection = new HttpGmailConnection(LoginDialog.getInstance().show("Enter Gmail Login"));
client.setConnection(connection);
final List<GmailMessage> messages = client.getMessagesBy(GmailClient.EmailSearchStrategy.DATE_GT,
        new Date().toString());
for (GmailMessage message : messages) {
    System.out.println(message);
}
```

### Get messages by keyword

```java
GmailClient client = new RssGmailClient();
GmailConnection connection = new HttpGmailConnection(LoginDialog.getInstance().show("Enter Gmail Login"));
client.setConnection(connection);
final List<GmailMessage> messages = client.getMessagesBy(
        GmailClient.EmailSearchStrategy.KEYWORD,"Unicode");
for (GmailMessage message : messages) {
    System.out.println(message);
}
```

### Get unread messages via proxy

```java
GmailClient client = new RssGmailClient();
HttpGmailConnection connection = new HttpGmailConnection(LoginDialog.getInstance().show("Enter Gmail Login"));
connection.setProxy("proxy.example.com", 8080);
connection.setProxyCredentials(LoginDialog.getInstance().show("Enter Proxy Login"));
client.setConnection(connection);
final List<GmailMessage> messages = client.getUnreadMessages();
for (GmailMessage message : messages) {
    System.out.println(message);
}
```

There are more examples in API docs.

Maven Site
----------
For reports, API docs and more, please check [Gmail4j Maven Site](http://spajus.github.com/gmail4j/0.4/).

[Changelog](https://github.com/spajus/gmail4j/blob/master/changelog.txt)
