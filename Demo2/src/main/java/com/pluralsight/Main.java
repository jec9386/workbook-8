package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

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

        try{
            displayCities(103);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }




    }

    public static BasicDataSource getBasicDataSourceFromArgs(String[] args){
        // get the user name and password from the command line args
        String username = args[0];
        String password = args[1];

        String connectionString = args[2];
        BasicDataSource result = new BasicDataSource();
        result.setUsername(username);
        result.setPassword(password);
        result.setUrl(connectionString);

        return result;
    }


    public static void displayCities(int countryId) throws SQLException, ClassNotFoundException {

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT city FROM city WHERE country_id = ?");)
        {
            ps.setInt(1, countryId);

            try(ResultSet results = ps.executeQuery()) {
                while (results.next()) {
                    String city = results.getString("city");
                    System.out.println(city);
                }
            }
            finally{
                //close the results ResultSet
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //close the ps PreparedStatement
            //close the connection Connection
        }

    }

    public static void displayAllCities() throws SQLException, ClassNotFoundException {


        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT city FROM city where country_id");
             ResultSet results = ps.executeQuery();
        )
        {
            while (results.next()) {
                String city = results.getString("city");
                System.out.println(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            //close the resultset
            //close the prepared statement
            //close the connection
        }

    }


}

