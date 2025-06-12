package com.pluralsight; //this class belongs to this package- organizes code into namespace

import java.sql.*; //imports ALl classes from java.sql package(Connection, PreparedStatement, ResultSet)- gives access to all JDBC classes needed for database operations
import java.util.Scanner;//for reading user input from console

//import javax.sql.DataSource;
//import com.mysql.cj.jdbc.MysqlDataSource;

public class Main {
    public static void main(String[] args) {

        //check if we have the required command line arguments- exactly 3, if not it prints out usage message and exits(defensive programming)- we try to validate inputs early before using them
        if (args.length != 3) {
            System.out.println("Application needs three arguments to run: " +
                    "java com.pluralsight.Main <connectString> <username> <password>");
            System.exit(1);
        }

        //these 3 are DataBase connection information
        String connectString = args[0]; //"jdbc:mysql://localhost:3306/northwind";- connects to northwind database(is the JDBC URL, specifies database type, location and name)
        String username = args[1]; //"user_1"; (username and password are Database credentials)
        String password = args[2]; //"password1234";
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
                    displayAllProducts(connectString, username, password);
                    break;
                case 2:
                    displayAllCustomers(connectString, username, password);
                    break;
                case 3:
                    displayAllCategories(connectString, username, password);
                    break;
                case 0:
                    System.out.println("Goodbye!");
                    running = false;// this exits loop because it's no longer true
                    break;
                default: // for invalid inputs
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    //---methods-----------------------------------------------------------------------------------------------------

    //METHOD 1
    public static void displayAllProducts(String connectString, String username, String password) {
        //Declare 3 key JDBC objects as null- will hold our database connection, SQL statement and results
        //for this way of programming always declare these objects outside of the try block so that we can close them
        Connection connection = null;
        PreparedStatement preparedStatement = null;//MUST use this to prevent SQL injection(THIS IS ABOUT SECURITY!!!)
        ResultSet results = null;// like a cursor that moves through rows

        try {
            //below step is like a handshake between Java and MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");//loads the MYSQL JDBC driver into memory- tells Java how to talk to MySQL
            connection = DriverManager.getConnection(connectString, username, password);//create actual connection to database with values

            //creates a compiled SQL statement
            preparedStatement = connection.prepareStatement(
                    "Select ProductId, ProductName, UnitPrice, UnitsInStock FROM products"
            );

            results = preparedStatement.executeQuery();// send SQL to database and get back a ResultSet


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
        } catch (SQLException | ClassNotFoundException e) {//multi-catch- handles multiple exceptions in this case SQL errors and driver loading errors
            System.out.println("Error displaying products: " + e.getMessage());
        } finally {// close resources in reverse order- finally block Always executes even if exception occurs. NEED TO CLOSE database connections
            try {
                if (results != null) results.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------

    //METHOD 2 ---------------------------------------------
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

    //------------------------------------------------------------------------------------------------------------------
    //METHOD 3 ---------------------------------------------------------
    //2 step database operation
    public static void displayAllCategories(String connectString, String username, String password) {
        Scanner scanner = new Scanner(System.in);
        int categoryId;// declared outside because if declared in try block it only exists there

        //use try-with-resources which automatically closes resources when try block exits. MODERN AND PREFERRED WAY
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//Find the MySQL driver class file, Load it into memory, initialize it

            // First query: Get all categories//try automatically closes resources in try parentheses
            try (Connection connection = DriverManager.getConnection(connectString, username, password);//return a Connection object representing the database session, active link, can execute SQL statements
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT CategoryId, CategoryName FROM categories ORDER BY CategoryId");//analyze SQL statement, creates execution plan, optimizes query, store in memory
                 ResultSet results = preparedStatement.executeQuery()) {//execute query and ResultSet object w/ the data we queried returned to Java

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
            categoryId = scanner.nextInt();

            // Second query: Get products for selected category- uses ? a parameterized query "?" a placeholder
            try (Connection connection = DriverManager.getConnection(connectString, username, password);
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

        } catch (ClassNotFoundException e) {//for Driver loading fails
            System.out.println("Error loading database driver: " + e.getMessage());
        }
    }

}









