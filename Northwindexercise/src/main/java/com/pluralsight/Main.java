package com.pluralsight; //this class belongs to this package- organizes code into namespace

import java.sql.*; //imports ALl classes from java.sql package(Connection, PreparedStatement, ResultSet)- gives access to all JDBC classes needed for database operations
import java.util.Scanner;//for reading user input from console

//import DataSource classes- enables connection pooling functionality
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

public class Main {

    //create single DataSource for entire application, create once, used throughout application, new centralized connection management
    private static DataSource dataSource;

    public static void main(String[] args) {

        //check if we have the required command line arguments- exactly 3, if not it prints out usage message and exits(defensive programming)- we try to validate inputs early before using them
        if (args.length != 3) {
            System.out.println("Application needs three arguments to run: " +
                    "java com.pluralsight.Main <connectString> <username> <password>");
            System.exit(1);//error code 1 indicates failure
        }

        //these 3 are DataBase connection information
        String connectString = args[0]; //"jdbc:mysql://localhost:3306/northwind";- connects to northwind database(is the JDBC URL, specifies database type, location and name)
        String username = args[1]; //"user_1"; (username and password are Database credentials)
        String password = args[2]; //"password1234";

        //--------------------------------------------------
        //Initialize DataSource with connection pooling
        //early validation that databse is connected- dont start menu otherwise
        try {
            initializeDataSource(connectString, username, password);
            System.out.println("Database connection pool initialized succeddfully");
        } catch (Exception e) {
            System.out.print("Failed to initialize databse connection: " + e.getMessage());
            System.exit(1);
        }

        //-------------------------------------------------
        //Main menu loop- Menu-Driven Programming pattern- common in console applications
        Scanner scanner = new Scanner(System.in);
        boolean running = true;//flag variable that controls our menu loop, start with true because we want menu to run at least once

        while (running) {
            System.out.println("\nWhat do you want to do?");
            System.out.println("1) Display all products");
            System.out.println("2) Display all customers");
            System.out.println("3) Display all categories");
            System.out.println("0) Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();// create Scanner to read user input

            switch (choice) {//route user choice + connect database info to each method, this is also called method delegation- we create specialized methods for specific task
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    displayAllCustomers();
                    break;
                case 3:
                    displayAllCategories();
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    closeDataSource();
                    running = false;// this exits loop because it's no longer true
                    break;
                default: // for invalid inputs
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    //---methods-----------------------------------------------------------------------------------------------------

    // Initialize Apache w/ connection pooling method, all setting in one place- fail immediately if database is unreachable
    private static void initializeDataSource(String connectString, String username, String password) {
        //Create BasicDataSource
        BasicDataSource basicDataSource = new BasicDataSource();
        // Basic connection settings- tells datasource how to connect to database
        basicDataSource.setUrl(connectString);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //Connection pool settings
        basicDataSource.setInitialSize(5);
        basicDataSource.setMaxTotal(20);
        basicDataSource.setMaxIdle(10);
        basicDataSource.setMinIdle(5);
        //Connection validation setting- test that connection is working and replace broken connection
        basicDataSource.setValidationQuery("Select 1");
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setTestWhileIdle(true);
        //Timeout setting- this waits up to 30 seconds
        basicDataSource.setMaxWaitMillis(30000);
        //Assign to static field- make available to all methods in class
        dataSource = basicDataSource;
        //Test connection
        try (Connection testConnection = dataSource.getConnection()) {
            System.out.println("Databse connection test successful!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to databse", e);
        }
    }

    //clean shutdown of DataSource
    private static void closeDataSource() {
        if (dataSource instanceof BasicDataSource) {
            try {
                ((BasicDataSource) dataSource).close();
                System.out.println("Database connection pool closed successfully");
            } catch (SQLException e) {
                System.out.println("Error closing database connection pool: " + e.getMessage());
            }
        }
    }


    //METHOD 1
    public static void displayAllProducts() {

        try {
            Connection connection = dataSource.getConnection();//create actual connection to database with values

            //creates a compiled SQL statement
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select ProductId, ProductName, UnitPrice, UnitsInStock FROM products"
            );

            ResultSet results = preparedStatement.executeQuery();// send SQL to database and get back a ResultSet


            //--------------------------------------------------------------------
            //Processing results
            //formatted display headers
            System.out.println("\n--- All Products ---");
            System.out.printf("%-3s | %-35s | %-8s | %-5s%n", "Id", "Name", "Price", "Stock");
            System.out.println("--- | ----------------------------------- | -------- | -----");

            while (results.next()) {//get column aligned with headers, moves cursor to next row(return false when no more rows to stop loop)
                System.out.printf("%-3d | %-35s | $%-7.2f | %-5d%n",
                        results.getInt("ProductId"),  //these gets are extracting typed data from columns
                        results.getString("ProductName"),
                        results.getDouble("UnitPrice"),
                        results.getInt("UnitsInStock"));
            }
        } catch (
                SQLException e) {//multi-catch- handles multiple exceptions in this case SQL errors and driver loading errors
            System.out.println("Error displaying products: " + e.getMessage());
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------

    //METHOD 2 ---------------------------------------------
    public static void displayAllCustomers() {
        try {
            Connection connection = dataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ContactName, CompanyName, City, Country, Phone FROM customers ORDER BY Country"
            );

            ResultSet results = preparedStatement.executeQuery();

            System.out.println("\n--- All Customers (Ordered by Country)2 ---");
            while (results.next()) {
                System.out.println("Contact: " + results.getString("ContactName"));
                System.out.println("Company: " + results.getString("CompanyName"));
                System.out.println("City: " + results.getString("City"));
                System.out.println("Country: " + results.getString("Country"));
                System.out.println("Phone: " + results.getString("Phone"));
                System.out.println("------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error displaying customers: " + e.getMessage());
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //METHOD 3 ---------------------------------------------------------
    //2 step database operation
    public static void displayAllCategories() {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT CategoryId, CategoryName FROM categories ORDER BY CategoryId");
             ResultSet results = preparedStatement.executeQuery()) {

            //Display categories heading  format
            System.out.println("\n--- All Categories ---");
            System.out.printf("%-3s | %-15s%n", "ID", "Category Name");
            System.out.println("--- | ---------------");

            //process results
            while (results.next()) {//cursor movement, starts before first rows and next() continues moves onto next row until false(no more rows)
                System.out.printf("%-3d | %-15s%n",//%n is the same as /n  but better for cross-platform compatibility
                        results.getInt("CategoryId"),//get() method called finds column with parameter name in current row, convert data value, and return
                        results.getString("CategoryName"));
            }

        } catch (SQLException e) {
            System.out.println("Error displaying categories: " + e.getMessage());
            return;
        }

        // Get user input and show results
        System.out.print("\nEnter a Category ID to see products: "); // <-- ADD THESE
        int categoryId = scanner.nextInt();

        // Second query: Get products for selected category- uses ? a parameterized query "?" a placeholder
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT ProductId, ProductName, UnitPrice, UnitsInStock FROM products WHERE CategoryId = ? ORDER BY ProductId")
        ) {
            // safely sets the first parameter, this prevents SQL injection attacks- never concatenate user input directly into SQL string- always use parameters
            preparedStatement.setInt(1, categoryId);

            try (ResultSet results = preparedStatement.executeQuery()) {
                System.out.println("\n--- Products in Category " + categoryId + " ---");
                System.out.printf("%-3s | %-35s | %-8s | %-5s%n", "ID", "Product Name", "Price", "Stock");
                System.out.println("--- | ----------------------------------- | -------- | -----");

                boolean hasProducts = false;
                while (results.next()) {
                    hasProducts = true;
                    System.out.printf("%-3d | %-35s | $%-7.2f | %-5d%n",
                            results.getInt("ProductId"),
                            results.getString("ProductName"),
                            results.getDouble("UnitPrice"),
                            results.getInt("UnitsInStock"));
                }

                if (!hasProducts) {
                    System.out.println("No products found in this category.");
                }
            }

        } catch (SQLException e) {//for Database connection/query fails
            System.out.println("Error displaying products: " + e.getMessage());
        }

    }
}











