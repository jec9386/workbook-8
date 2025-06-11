package com.pluralsight;

import java.sql.*;
import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;


public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println("Hello, World!");
        String connectString = "jdbc:mysql://localhost:3306/northwind";
        String username = "user_1";
        String password = "password1234";

        Class.forName("com.mysql.cj.jdbc.Driver"); //Run MySQL JDBC driver- ensure Java talks to MySQL

        Connection connection; //object represents live connection to the database
        connection = DriverManager.getConnection(connectString, username, password);//DriverManager actually opens the network connection to MySQL
        //at this point you have connected Java to MySQL database


        PreparedStatement preparedStatement = connection.prepareStatement(
               "Select ProductId, ProductName, UnitPrice, UnitsInStock FROM products"
        );

        ResultSet results = preparedStatement.executeQuery();//execute, send query to database, and get back  ResultSet- results/table you can go through

//        while(results.next()){
//            int productId = results.getInt("ProductId");
//            String productName = results.getString("ProductName");
//            double unitPrice = results.getDouble("UnitPrice");
//            int unitsInStock = results.getInt("UnitsInStock");
////
////            System.out.println("Product Id: " + productId);
////            System.out.println("Name: " + productName);
////            System.out.println("Price: " + unitPrice);
////            System.out.println("Stock: " + unitsInStock);
////            System.out.println("------------------");


        //option 2 of formation
        System.out.printf("%-3s | %-35s | %-8s | %-5s%n", "Id", "Name", "Price", "Stock");
        System.out.println("--- | ----------------------------------- | -------- | -----");

        while (results.next()) {
            System.out.printf("%-3d | %-35s | $%-7.2f | %-5d%n",
                    results.getInt("ProductId"),
                    results.getString("ProductName"),
                    results.getDouble("UnitPrice"),
                    results.getInt("UnitsInStock"));
        }

        //option 1 and 2 are nice depending on how you want to view the result, option 2 can take alittle more time to adjust the formating and length

        // use variable name to close not class name
        results.close();
        preparedStatement.close();
        connection.close();// close database connection, prevent leakage, Must and always close when done.
    }
}