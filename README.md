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

***Get value from result set (returns null if empty):***
```java
int id = connector.getInteger(row.get("id"));
String name = connector.getString(row.get("name"));
```
