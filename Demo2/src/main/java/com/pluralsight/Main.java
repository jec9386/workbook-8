package com.pluralsight;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.*;
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

        try{
            displayCities(103);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //extracting values from args
    public static sqlConnectionInfo getSqlConnectionInfoFromArgs(String[] args){
        String username = args[0];
        String password = args[1];
        String connectionString = args[2];

        return new sqlConnectionInfo(connectionString, username, password);
    }

    public static void displayCities(int countryID) throws SQLException{
        //3 variables that contains connection, result, PreparedStatement
        Connection connection= null;
        ResultSet results = null;
        PreparedStatement ps = null;

        //attempt to
        try{
            // load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //create connection
            connection = DriverManager.getConnection(sqlConnectionInfo.getConnectionString(), sqlConnectionInfo.getUserName(), sqlConnectionInfo.getPassword());

            // define your query
            String query = "SELECT city FROM city " +
                    "WHERE country_id = ?";

            ps= connection.prepareStatement(query);
            //set parameter that query needs
            ps.setInt(1, countryID);


            // 2. Execute your query
            results = ps.executeQuery();
            // process the results
            while (results.next()) {
                String city = results.getString("city");
                System.out.println(city);
            }
        }

        catch(Exception e){
            e.printStackTrace();
        }




        // 3. Close the connection
        finally{
            if (results != null)results.close();
            if (ps != null)ps.close();
            if (connection != null)connection.close();
        }

    }
}