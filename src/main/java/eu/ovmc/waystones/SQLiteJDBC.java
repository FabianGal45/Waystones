package eu.ovmc.waystones;
import java.sql.*;

public class SQLiteJDBC {
    public Connection getCon(){
        Connection con = null;
        try{
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:plugins/Waystones/waystones.sqlite.db");
            System.out.println("Connected to the waystones database.");

        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e +" Database Connection FAILED!");
        }

        return con;
    }

    public void createTables(Connection c){
        Statement stmt;
        try {
            stmt = c.createStatement();
            String sqlPublic = "CREATE TABLE IF NOT EXISTS public_waystones " +  //Creates the public_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " name VARCHAR(255)," +
                    " cost DOUBLE(40, 2)," +
                    " rating DOUBLE(40, 2)," +
                    " PRIMARY KEY(location))";
            stmt.executeUpdate(sqlPublic);
            String sqlPrivate = "CREATE TABLE IF NOT EXISTS private_waystones " + //Creates the private_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " PRIMARY KEY(location))";
            stmt.executeUpdate(sqlPrivate);
            stmt.close();

            //Prints the table names to console to check if they exist
            DatabaseMetaData meta = c.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
            while(resultSet.next()){
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.println("JDBC> This table exists: "+ tableName);
            }

            c.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            System.out.println(e +" Database Failed to Create table");
        }
    }







}
