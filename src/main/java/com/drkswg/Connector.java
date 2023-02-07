package com.drkswg;

import com.drkswg.exceptions.NoDataFoundException;

import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class Connector {
    private final String url;
    private final String userName;
    private final char[] password;
    private Connection connection;

    public Connector(String url, String userName, char[] password) throws SQLException {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.connection = DriverManager.getConnection(url, userName, String.valueOf(password));
    }

    public void reconnect() throws SQLException {
        this.connection = DriverManager.getConnection(url, userName, Arrays.toString(password));
    }

    public void closeConnection() throws SQLException {
        this.connection.close();
    }

    public String formatDate(java.sql.Date date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        if (date == null) return null;

        return formatter.format(date);
    }

    public java.sql.Date getSqlDate(String date, String pattern) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        if (date == null) return null;

        return new java.sql.Date(formatter.parse(date).getTime());
    }

    public String getDateAsString(Object date, String datePattern) {
        String returnDate = "";

        if (date != null) {
            Date tempDate = (Date) date;
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            returnDate = formatter.format(tempDate);
        }

        return returnDate;
    }

    public String getString(Object text) throws SQLException {
        String returnString = "";

        if (text != null) {
            if (text.getClass().getName().equals("oracle.sql.CLOB")) {
                Clob clob = (Clob) text;
                returnString =  clob.getSubString(1, (int) clob.length());
            } else {
                returnString = text.toString();
            }
        }

        return returnString;
    }

    public int getInteger(Object value) {
        int returnValue = 0;
        BigDecimal tempBigDecimal = (BigDecimal) value;

        if (tempBigDecimal != null) {
            returnValue = tempBigDecimal.intValue();
        }

        return returnValue;
    }

    @SafeVarargs
    public final <T,A> T getSingleValue(Class<T> type, String query, String column, A... args)
            throws SQLException {
        List<String> columns = Collections.singletonList(column);
        List<Map<String, Object>> resultSet = getResultSet(query, columns, args);

        if (resultSet.isEmpty()) {
            throw new NoDataFoundException("No data found");
        }

        Object resultSetValue = resultSet.get(0).get(columns.get(0));

        if (resultSetValue == null) {
            throw new NoDataFoundException("No data found");
        }

        String className = resultSetValue.getClass().getName();

        if (className.equals("java.math.BigDecimal") & !type.getCanonicalName().equals("java.math.BigDecimal")) {
            BigDecimal value = (BigDecimal) resultSetValue;

            switch (type.getCanonicalName()) {
                case "java.lang.Integer":
                    return type.cast(value.intValue());
                case "java.lang.Double":
                    return type.cast(value.doubleValue());
                case "java.lang.Float":
                    return type.cast(value.floatValue());
                case "java.lang.Short":
                    return type.cast(value.shortValue());
                default:
                    return type.cast(resultSetValue);
            }
        } else {
            return type.cast(resultSetValue);
        }
    }

    @SafeVarargs
    public final <A> List<Map<String, Object>> getResultSet(String query, List<String> columns, A... args)
            throws SQLException {
        List<Map<String, Object>> returnSet = new ArrayList<>();
        PreparedStatement preparedSelect = connection.prepareStatement(query);

        for (int i = 0; i < args.length; i++) {
            preparedSelect.setObject(i + 1, args[i]);
        }

        ResultSet resultSet = preparedSelect.executeQuery();

        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>();

            for (String column : columns) {
                Object cell = resultSet.getObject(column);
                row.put(column, cell);
            }

            returnSet.add(row);
        }

        preparedSelect.close();

        return returnSet;
    }

    @SafeVarargs
    public final <A> void executeDml(String query, A... args) throws SQLException {
        PreparedStatement preparedInsert = connection.prepareStatement(query);

        for (int i = 0; i < args.length; i++) {
            preparedInsert.setObject(i + 1, args[i]);
        }

        preparedInsert.executeUpdate();
        preparedInsert.close();
    }
}
