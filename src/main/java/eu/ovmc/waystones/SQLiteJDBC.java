package eu.ovmc.waystones;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

public class SQLiteJDBC {
    private Connection con = getCon();
    public Connection getCon(){
        Connection connection = null;
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/Waystones/waystones.sqlite.db");

        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e +" Database Connection FAILED!");
        }

        return connection;
    }

    public void createTables(){
        Statement stmt;
        try {
            stmt = con.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " + //Creates the users table
                    "(uuid varchar(255)," +
                    " user_name VARCHAR(255)," +
                    " private_ws INT(255)," +
                    " public_ws INT(255)," +
                    " PRIMARY KEY(uuid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS private_waystones " + //Creates the private_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " PRIMARY KEY(location)" +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(uuid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS public_waystones " +  //Creates the public_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " name VARCHAR(255)," +
                    " cost DOUBLE(40, 2)," +
                    " rating DOUBLE(40, 2)," +
                    " PRIMARY KEY(location)" +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(uuid))";
            stmt.executeUpdate(sql);
//            stmt.close();

            //Prints the table names to console to check if they exist
            DatabaseMetaData meta = con.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, null, new String[]{"TABLE"});
            while(resultSet.next()){
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.println("JDBC> This table exists: "+ tableName);
            }

            
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Database Failed to Create tables");
            System.exit(0);
        }
    }

    public User getDatafromUser(Player p) {
        Statement stmt;
        String uuid = p.getUniqueId().toString();
        User user = null;
        try{
            stmt = con.createStatement();
            String sql = "SELECT * FROM users WHERE uuid = '"+uuid +"'";

            try {
                ResultSet rs = stmt.executeQuery(sql);
                user = new User(rs.getString("uuid"), rs.getString("user_name"), rs.getInt("private_ws"), rs.getInt("public_ws"));
            }catch (SQLException e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.out.println(e +" No users in the database.");
            }
//            System.out.println(rs.getString("uuid"));

            //Todo: Somehow it does not recognize the fact that there is already a player in the database

//            stmt.close();
            con.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve user from users table.");
            System.exit(0);
        }

        return user;
    }

    public void regPlayer(Player p){
        try{
            //Query to insert data into tutorials_data table
            String query = "INSERT INTO users (" +
                    " uuid," +
                    " user_name," +
                    " private_ws," +
                    " public_ws)" +
                    " VALUES(?, ?, ?, ?)";

            //Creating the preparedStatement object
            PreparedStatement pstmt = con.prepareStatement(query);

            pstmt.setString(1, p.getUniqueId().toString());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, 1);//A player must have placed a private waystone to be registered
            pstmt.setInt(4, 0);
            pstmt.execute();
            con.close();
            
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Could not register player.");
            System.exit(0);
        }
    }

    public void updateUser(User user){
        Statement stmt;
        try{
            stmt = con.createStatement();
            String sql = "UPDATE users" +
                    " SET private_ws = " + user.getPrivateWs() +
                    " WHERE uuid = '" + user.getUuid() +"'";
            stmt.executeUpdate(sql);
//            stmt.close();
            con.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to update the private waystone number to the user.");
            System.exit(0);
        }


    }

    public void regWaystone(Waystone ws){

        try{
            //Query to insert data into tutorials_data table
            String query = "INSERT INTO private_waystones ("
                    + "location, "
                    + "owner) VALUES(?, ?)";

            //Creating the preparedStatement object
            PreparedStatement pstmt = con.prepareStatement(query);
            String location = ws.getLocation().toString();
            String owner = ws.getOwner().toString();

            pstmt.setString(1, location);
            pstmt.setString(2, owner);
            pstmt.execute();
            con.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +"Could not register waystone.");
            System.exit(0);
        }
    }







}
