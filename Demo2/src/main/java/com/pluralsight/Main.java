package com.pluralsight;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
import java.util.Scanner;
import javax.sql.DataSource;

public class Main {

    private static sqlConnectionInfo sqlConnectionInfo;


    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(
                    "Application needs three arguments to run: " +
                            "java com.pluralsight.Main <username> <password> <sqlUrl>");
            System.exit(1);
        }

        sqlConnectionInfo = getSqlConnectionInfoFromArgs(args);

        displayCities(103);

    }

    //-----------------------------------------------------------------------------------------------

    //extracting values from args
    public static sqlConnectionInfo getSqlConnectionInfoFromArgs(String[] args) {
        String username = args[0];
        String password = args[1];
        String connectionString = args[2];

        return new sqlConnectionInfo(connectionString, username, password);
    }

    //----------------------------------------------------------------------------------------------------
    public static void displayCities(int countryID) {

        try {
            // load/ initialize the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");


            //3 nested try block
            try (//first try is to try the connection
                 // 1. open a connection to the database
                 // use the database URL to point to the correct database
                 Connection connection = DriverManager.getConnection(
                         sqlConnectionInfo.getConnectionString(),
                         sqlConnectionInfo.getUserName(),
                         sqlConnectionInfo.getPassword());
                 PreparedStatement ps = connection.prepareStatement("SELECT city FROM city " + "WHERE country_id = ?");
            ) {
                ps.setInt(1, countryID);   //set parameter that query need, because we have parameter we cant consolidate this further

                try (ResultSet results = ps.executeQuery()) { //3rd try to create results
                    // process the results
                    while (results.next()) {
                        String city = results.getString("city");
                        System.out.println(city);
                    }
                }
            }
        }catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error displaying cities: " + e.getMessage());
        }

    }
}
