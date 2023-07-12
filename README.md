# EasyOracleConnector
Lightweight library with no dependencies which simplifies the work with Oracle DB through the JDBC


***Create connector object:***
```java
Connector connector = new Connector(url, userName, password);
```

***Get single value:***
```java
String singleValueSelect = "SELECT room FROM users WHERE id = ?";
int room = connector.getSingleValue(Integer.class, singleValueSelect, "room", 1);
```

***Get result set:***
```java
String multipleRowSelect = "SELECT id, name FROM users WHERE id IN (?, ?, ?)";
List<String> columns = Arrays.asList("id", "name");
List<Map<String, Object>> resultSet = connector.getResultSet(multipleRowSelect, columns, 1, 2, 3);

for (Map<String, Object> row : resultSet) {
  // your code
}
```

***List-as-args:***

You can use any numbers of Lists, placeholder will always be {}

(so far only for SELECT statements)

```java
String multipleRowSelect = "SELECT id, name FROM users WHERE id IN ({})";
List<String> columns = Arrays.asList("id", "name");
List<Map<String, Object>> resultSet = connector.getResultSet(multipleRowSelect, columns, Arrays.asList(1, 2, 3));
```
