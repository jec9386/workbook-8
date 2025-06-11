package com.pluralsight; //this class belongs to this package

import java.sql.*;
//import javax.sql.DataSource;
//import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        //these 3 are DataBase connection information
        String connectString = "jdbc:mysql://localhost:3306/northwind";
        String username = "user_1";
        String password = "password1234";

        Scanner scanner = new Scanner(System.in);
        boolean running = true;//flag variable that controls our menu loop, start with true because we want menu to run at least once

        while (running) {
            System.out.println("\nWhat do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    displayAllProducts(connectString, username, password);
                    break;
                case 2:
                    displayAllCustomers(connectString, username, password);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    //---methods-----------------------------------------------------------------------------------------------------

    public static void displayAllProducts(String connectString, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//loads the MYSQL JDBC driver- tells Java how to talk to MySQL
            connection = DriverManager.getConnection(connectString, username, password);//create actual connection to database with values

            preparedStatement = connection.prepareStatement(
                    "Select ProductId, ProductName, UnitPrice, UnitsInStock FROM products"
            );

            results = preparedStatement.executeQuery();// send SQL to database and get back a ResultSet, assign to variable to loop through results

            //display headers
            System.out.println("\n--- All Products ---");
            System.out.printf("%-3s | %-35s | %-8s | %-5s%n", "Id", "Name", "Price", "Stock");
            System.out.println("--- | ----------------------------------- | -------- | -----");

            while (results.next()) {//get column aligned with headers
                System.out.printf("%-3d | %-35s | $%-7.2f | %-5d%n",
                        results.getInt("ProductId"),
                        results.getString("ProductName"),
                        results.getDouble("UnitPrice"),
                        results.getInt("UnitsInStock"));
            }
        } catch (SQLException | ClassNotFoundException e) {//multi-catch- handles multiple excpetions
            System.out.println("Error displaying products: " + e.getMessage());
        } finally {
            try {
                if (results != null) results.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public static void displayAllCustomers(String connectString, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet results = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(connectString, username, password);

            preparedStatement = connection.prepareStatement(
                    "SELECT ContactName, CompanyName, City, Country, Phone FROM customers ORDER BY Country"
            );

            results = preparedStatement.executeQuery();

            System.out.println("\n--- All Customers (Ordered by Country)2 ---");
            while (results.next()) {
                System.out.println("Contact: " + results.getString("ContactName"));
                System.out.println("Company: " + results.getString("CompanyName"));
                System.out.println("City: " + results.getString("City"));
                System.out.println("Country: " + results.getString("Country"));
                System.out.println("Phone: " + results.getString("Phone"));
                System.out.println("------------------");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error displaying customers: " + e.getMessage());
        } finally {
            try {
                if (results != null) results.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}





