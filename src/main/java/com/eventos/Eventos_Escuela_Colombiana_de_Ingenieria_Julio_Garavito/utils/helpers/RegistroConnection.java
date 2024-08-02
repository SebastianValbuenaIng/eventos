package com.eventos.Eventos_Escuela_Colombiana_de_Ingenieria_Julio_Garavito.utils.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class RegistroConnection {
    @Value("${db3.datasource.jdbc.url}")
    private String jdbc;
    @Value("${db3.datasource.username}")
    private String user;
    @Value("${db3.datasource.password}")
    private String password;

    public Map<String, Object> executeSelectSql(String query) throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(jdbc, user, password);

            var statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            Map<String, Object> response = new HashMap<>();

            while (rs.next()) {
                Map<String, String> dataRow = new HashMap<>();
                ResultSetMetaData rsmd = rs.getMetaData();
                int numColumns = rsmd.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    dataRow.put(rsmd.getColumnName(i), rs.getString(i));
                }

                response.putAll(dataRow);
            }

            rs.close();
            statement.close();
            connection.close();

            return response;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public void executeSql(String query) throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(jdbc, user, password);

            var statement = connection.prepareStatement(query);
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }
}