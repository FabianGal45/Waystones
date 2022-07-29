package eu.ovmc.waystones;
import java.sql.*;

public class SQLiteJDBC {
    public Connection connectWDB(){
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
}
