package com.pluralsight;

import java.sql.*;


public class Main {

    private static BasicDataSource basicDataSource;


    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println(
                    "Application needs three arguments to run: " +
                            "java com.pluralsight.Main <username> <password> <sqlUrl>");
            System.exit(1);
        }

        basicDataSource = getBasicDataSourceFromArgs(args);

        try {
            displayCities(103);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //-----------------------------------------------------------------------------------------------

    //extracting values from args
    public static BasicDataSource getBasicDataSourceFromArgs(String[] args) {
        String username = args[0];
        String password = args[1];
        String connectionString = args[2];

        return new BasicDataSource(connectionString, username, password);
    }

    //----------------------------------------------------------------------------------------------------
    public static void displayCities(int countryID) throws SQLException, ClassNotFoundException {
        //3 nested try block
        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT city FROM city " + "WHERE country_id = ?");) {
            ps.setInt(1, countryID);   //set parameter that query need, because we have parameter we cant consolidate this further

            try (ResultSet results = ps.executeQuery()) { //3rd try to create results
                // process the results
                while (results.next()) {
                    String city = results.getString("city");
                    System.out.println(city);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            {
                System.out.println("Error displaying cities: " + e.getMessage());
            }

        }
    }
}
