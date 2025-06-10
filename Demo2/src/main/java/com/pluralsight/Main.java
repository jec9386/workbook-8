package com.pluralsight;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import javax.sql.DataSource;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("Hello, World!");
        String connectionString = "jdbc:mysql://localhost:3306/sakila";
        String username = "user_1";
        String password = "password1234";

        // load the MySQL Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        //create connection
        Connection connection;
        connection = DriverManager.getConnection(connectionString, username, password);

        // create statement
        // the statement is tied to the open connection
        Statement statement = connection.createStatement();

        // define your query
        String query = "SELECT city FROM city " +
                "WHERE country_id = 103";
        // 2. Execute your query
        ResultSet results = statement.executeQuery(query);
        // process the results
        while (results.next()) {
            String city = results.getString("city");
            System.out.println(city);
        }
        // 3. Close the connection
        connection.close();


    }
}