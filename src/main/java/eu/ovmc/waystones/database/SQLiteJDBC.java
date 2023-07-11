package eu.ovmc.waystones.database;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class SQLiteJDBC {
    private Connection con;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public Connection getCon(){

        //If there is a connection created then return that connection.
        if(con != null){
            return con;
        }
        else{
            try{
                //If connection does not exist then create a new connection;
                Class.forName("org.sqlite.JDBC");
                Connection con = DriverManager.getConnection("jdbc:sqlite:plugins/Waystones/waystones.sqlite.db");
                this.con = con;

            }catch(ClassNotFoundException | SQLException e){
                System.out.println(e +" Database Connection FAILED!");
            }

        }
        return con;
    }

    //If there is no tables this will create them
    public void createTables(){
        Statement stmt;
        try {
            stmt = getCon().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " + //Creates the users table
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " uuid TEXT," +
                    " user_name TEXT," +
                    " private_ws INTEGER," +
                    " public_ws INTEGER," +
                    " purchased_private_ws INTEGER," +
                    " acquired_private_ws INTEGER," +
                    " acquired_public_ws INTEGER)";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS private_waystones " + //Creates the private_waystones table
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " location TEXT," +
                    " owner TEXT," +
                    " name TEXT," +
                    " tp_location TEXT," +
                    " priority INTEGER," +
                    " custom_item TEXT," +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS public_waystones " +  //Creates the public_waystones table
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " location TEXT," +
                    " owner TEXT," +
                    " name TEXT," +
                    " tp_location TEXT," +
                    " priority INTEGER," +
                    " custom_item TEXT," +
                    " cost REAL," +
                    " rating REAL," +
                    " category TEXT," +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(id))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS ratings " +  //Creates the ratings table
                    "(pub_ws_id INTEGER," +
                    " player_id INTEGER," +
                    " rate INTEGER," +
                    " FOREIGN KEY (pub_ws_id)" +
                    "  REFERENCES public_waystones(id))";
            stmt.executeUpdate(sql);
            stmt.close();


        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Database Failed to Create tables");
            System.exit(0);
        }
    }

    public void checkForDBupdate(){
        //check to see if there are values missing in the current database
        try{
            DatabaseMetaData metaData = con.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "private_waystones","id");

            if(columns.next()){
                System.out.println("Column id exists in table private waystone. No update required");
            }
            else{
                //IF values are missing create a backup of the current database
                String databasePath = "plugins/Waystones/waystones.sqlite.db";
                String backupPath = "plugins/Waystones/waystones_backup.sqlite.db";

                File originalFile = new File(databasePath);
                File backupFile = new File(backupPath);
                Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                //Change the names of the current tables to "old_table"
                // Retrieve table information
                ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("[][][]>> table name before: "+tableName);

                    Statement stmt = getCon().createStatement();
                    String sql = "ALTER TABLE "+ tableName +" RENAME TO old_"+tableName;
                    stmt.executeUpdate(sql);
                    stmt.close();
                }

                tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
                while(tables.next()){
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("[][][]>> table name after: "+tableName);
                }

                //Create the new tables
                createTables();
                System.out.println("Created updated tables");

                //Copy the data from the old table into the new tables //todo <<<<<<<<<<<<<


                //drop the old tables


                //close metasata
                columns.close();
                tables.close();
            }
        }catch (SQLException | IOException e){
            e.printStackTrace();
        }








    }

    public void regPlayer(Player p){
        try{
            //Query to insert data into tutorials_data table
            String query = "INSERT INTO users (" +
                    " uuid," +
                    " user_name)" +
                    " VALUES(?, ?)";

            //Creating the preparedStatement object
            PreparedStatement pstmt = getCon().prepareStatement(query);

            pstmt.setString(1, p.getUniqueId().toString());
            pstmt.setString(2, p.getName());
            pstmt.execute();
            pstmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Could not register player.");
            System.exit(0);
        }
    }

    public void regWaystone(PrivateWaystone ws, User user){

        if(ws instanceof PublicWaystone){

            try{
                //Query to insert data into tutorials_data table
                String query = "INSERT INTO public_waystones (" +
                        "location," +
                        "owner," +
                        "name," +
                        "tp_location) VALUES(?, ?, ?, ?)";

                //Creating the preparedStatement object
                PreparedStatement pstmt = getCon().prepareStatement(query);
                String location = ws.getLocation();
                String owner = ws.getOwner();
                String name = ws.getName();
                String tpLocation = ws.getTpLocation();

                pstmt.setString(1, location);
                pstmt.setString(2, owner);
                pstmt.setString(3, name);
                pstmt.setString(4, tpLocation);

                pstmt.execute();
                pstmt.close();

                //Update the user data.
                updateUser(user);

            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.out.println(e +"Could not register waystone.");
                System.exit(0);
            }

        }
        else{
            try{
                //Query to insert data into tutorials_data table
                String query = "INSERT INTO private_waystones (" +
                        "location," +
                        "owner," +
                        "name," +
                        "tp_location) VALUES(?, ?, ?, ?)";

                //Creating the preparedStatement object
                PreparedStatement pstmt = getCon().prepareStatement(query);
                String location = ws.getLocation();
                String owner = ws.getOwner();
                String name = ws.getName();
//                System.out.println("Reg - TP loc: "+ws.getTpLocation());
                String tpLocation = ws.getTpLocation();

                pstmt.setString(1, location);
                pstmt.setString(2, owner);
                pstmt.setString(3, name);
                pstmt.setString(4, tpLocation);
                pstmt.execute();
                pstmt.close();

                //Update the user data.
                updateUser(user);

            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.out.println(e +"Could not register waystone.");
                System.exit(0);
            }
        }
    }


    public void regRate(PublicWaystone ws, Player player, int rate){
        try{

            //Update the ratings table
            String query = "INSERT INTO ratings (" +
                    "pub_ws_id,"+
                    "player_id," +
                    "rate) VALUES(?, ?, ?)";

            //Creating the preparedStatement object
            PreparedStatement pstmt = getCon().prepareStatement(query);
            String playerUUID = String.valueOf(player.getUniqueId());
            int userId = getUserFromDB(playerUUID).getId();

            pstmt.setInt(1, ws.getId());
            pstmt.setInt(2, userId);
            pstmt.setInt(3, rate);
            pstmt.execute();
            pstmt.close();

            //Start calculating the final rate based on all entries. Then update the Public waystones table
            ArrayList<Integer> ratesList = getAllRatesForPubWs(ws);

            int a = 0, b = 0, c = 0, d = 0, e = 0, r = ratesList.size();
            for(Integer i :ratesList){
                System.out.println("RatesList: "+ ratesList.size()+ ", "+i);
                if(i==1){
                    a++;
                } else if (i==2){
                    b++;
                } else if (i==3) {
                    c++;
                } else if (i==4){
                    d++;
                } else if (i==5){
                    e++;
                }
            }

            double finalRate = (double)(a+2*b+3*c+4*d+5*e)/r;
            ws.setRating(Double.parseDouble(df.format(finalRate)));
            updateWaystone(ws);

        }catch (Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +"Could not register waystone.");
            System.exit(0);
        }
    }

    public boolean hasPlayerRated(Player player, PublicWaystone ws){
        boolean result = false;
        Statement stmt;
        try{
            int userId = getUserFromDB(player.getUniqueId().toString()).getId();
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM ratings WHERE pub_ws_id = '"+ ws.getId() +"' and player_id = '"+ userId +"';";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                result = true;
            }
            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to check if player has rated before.");
            System.exit(0);
        }
        return result;
    }

    public ArrayList<Integer> getAllRatesForPubWs(PublicWaystone ws){
        ArrayList<Integer> arrayList = new ArrayList<>();

        Statement stmt;
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM ratings WHERE pub_ws_id = '"+ ws.getId() +"';";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                arrayList.add(rs.getInt("rate"));
            }

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve rate from rates table.");
            System.exit(0);
        }

        return arrayList;
    }


    public User getUserFromDB(String uuid) {
        Statement stmt;
//        String uuid = p.getUniqueId().toString();
        User user = null;
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM users WHERE uuid = '"+ uuid +"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                user = new User(rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("user_name"),
                        rs.getInt("private_ws"),
                        rs.getInt("public_ws"),
                        rs.getInt("purchased_private_ws"),
                        rs.getInt("acquired_private_ws"),
                        rs.getInt("acquired_public_ws"));
            }

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve user from users table.");
            System.exit(0);
        }

        return user;
    }

    public ArrayList<User> getAllUsersFromDB() {
        Statement stmt;
        User user = null;
        ArrayList<User> userArrayList = new ArrayList<>();
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM users";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                user = new User(rs.getInt("id"),
                        rs.getString("uuid"),
                        rs.getString("user_name"),
                        rs.getInt("private_ws"),
                        rs.getInt("public_ws"),
                        rs.getInt("purchased_private_ws"),
                        rs.getInt("acquired_private_ws"),
                        rs.getInt("acquired_public_ws"));
                userArrayList.add(user);
            }

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve user from users table.");
            System.exit(0);
        }

        return userArrayList;
    }

    public PrivateWaystone getWaystone(String location){
        PrivateWaystone ws = null;

        Statement stmt;
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM private_waystones WHERE location = '"+ location +"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                ws = new PrivateWaystone(rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("tp_location"),
                        rs.getInt("priority"),
                        rs.getString("custom_item"));
            }

            sql = "SELECT * FROM public_waystones WHERE location = '"+ location +"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                ws = new PublicWaystone(rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("tp_location"),
                        rs.getInt("priority"),
                        rs.getString("custom_item"),
                        rs.getDouble("rating"),
                        rs.getInt("cost"),
                        rs.getString("category"));
            }


            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve Waystone from private_waystones table.");
            System.exit(0);
        }

        return ws;
    }

    public ArrayList<PrivateWaystone> getAllPrivateWaystones(String uuid){
        ArrayList<PrivateWaystone> privWs = new ArrayList<>();
        PrivateWaystone ws = null;
        Statement stmt;

        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM private_waystones WHERE owner = '"+ uuid +"';";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                ws = new PrivateWaystone(rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("tp_location"),
                        rs.getInt("priority"),
                        rs.getString("custom_item"));
                privWs.add(ws);
            }

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve Waystone from private_waystones table.");
            System.exit(0);
        }

        return privWs;
    }

    public ArrayList<PublicWaystone> getAllPublicWaystones(){
        ArrayList<PublicWaystone> pubWs = new ArrayList<>();
        PublicWaystone ws = null;
        Statement stmt;

        try{
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM public_waystones;";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                ws = new PublicWaystone(rs.getInt("id"),
                        rs.getString("location"),
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("tp_location"),
                        rs.getInt("priority"),
                        rs.getString("custom_item"),
                        rs.getDouble("rating"),
                        rs.getInt("cost"),
                        rs.getString("category"));
                pubWs.add(ws);
            }

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to retrieve Waystone from public_waystones table.");
            System.exit(0);
        }

        return pubWs;
    }

    public void updateUser(User user){
        Statement stmt;

        //Count the number of waystones a user has
        int privateCount = countPrivateWs(user);
        int publicCount = countPublicWs(user);
        int purchasedPrivateWs = user.getPurchasedPrivateWs();
        int acquiredPrivateWs = user.getAcquiredPrivateWs();
        int acquiredPublicWs = user.getAcquiredPublicWs();

        try{
            stmt = getCon().createStatement();
            String sql = "UPDATE users" +
                    " SET private_ws = " + privateCount + ", public_ws = "+ publicCount +  ", purchased_private_ws = "+ purchasedPrivateWs + ", acquired_private_ws = " + acquiredPrivateWs +", acquired_public_ws = " + acquiredPublicWs +
                    " WHERE uuid = '" + user.getUuid() +"'";
            stmt.executeUpdate(sql);
//            System.out.println("User "+ user.getUuid() +" updated!");
            stmt.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to update the private waystone number to the user.");
            System.exit(0);
        }


    }

    public void updateWaystone(PrivateWaystone ws){

        if(ws instanceof PublicWaystone){
            System.out.println("PUBWS: Rating: "+ ((PublicWaystone) ws).getRating() );
            try{
                String sql = "UPDATE public_waystones" +
                        " SET name = ?, tp_location = ?, cost = ?, rating = ?" +
                        " WHERE location = ?;";


                PreparedStatement pstmt = getCon().prepareStatement(sql);
                pstmt.setString(1, ws.getName());
                pstmt.setString(2, ws.getTpLocation());
                pstmt.setDouble(3, ((PublicWaystone) ws).getCost());
                pstmt.setDouble(4, ((PublicWaystone) ws).getRating());
                pstmt.setString(5, ws.getLocation());

                pstmt.execute();
                pstmt.close();
            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.out.println(e +" Failed to update Public waystone!");
                System.exit(0);
            }


        }
        else{
            try{
                String sql = "UPDATE private_waystones" +
                        " SET name = ?, tp_location = ?" +
                        " WHERE location = ?;";


                PreparedStatement pstmt = getCon().prepareStatement(sql);
                pstmt.setString(1, ws.getName());
                pstmt.setString(2, ws.getTpLocation());
                pstmt.setString(3, ws.getLocation());

                pstmt.execute();
                pstmt.close();
            }catch (Exception e){
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.out.println(e +" Failed to update Private waystone!");
                System.exit(0);
            }
        }



    }

     private int countPrivateWs(User user){
        int num = 0;
        Statement stmt;
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT COUNT(location) AS recordCount" +
                    " FROM private_waystones " +
                    " WHERE owner = '" + user.getUuid() +"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                num = rs.getInt("recordCount");
            }

            stmt.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to count the number of private waystones.");
            System.exit(0);
        }

        return num;
    }
    private int countPublicWs(User user){
        int num = 0;
        Statement stmt;
        try{
            stmt = getCon().createStatement();
            String sql = "SELECT COUNT(location) AS recordCount" +
                    " FROM public_waystones " +
                    " WHERE owner = '" + user.getUuid() +"'";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                num = rs.getInt("recordCount");
            }

            stmt.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to count the number of public waystones.");
            System.exit(0);
        }

        return num;
    }

    public void remWs(PrivateWaystone ws){
        Statement stmt;

        try{ //get the values from the broken waystone
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM private_waystones WHERE location = '"+ ws.getLocation() +"'";
            ResultSet rs = stmt.executeQuery(sql);
            String owner = null;
            String loc = null;

            //Store the values so that I can update the user after removing a waystone.
            while(rs.next()){
                owner = rs.getString("owner");
                loc = rs.getString("location");

                //Delete the waystone
                sql = "DELETE FROM private_waystones WHERE location = '"+ loc +"'";
                stmt.executeUpdate(sql);
//                System.out.println("Waystone removed.");
            }

            sql = "SELECT * FROM public_waystones WHERE location = '"+ ws.getLocation() +"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                owner = rs.getString("owner");
                loc = rs.getString("location");

                //Delete the waystone
                sql = "DELETE FROM public_waystones WHERE location = '"+ loc +"'";
                stmt.executeUpdate(sql);
//                System.out.println("Waystone removed.");
            }



            //update the owner of the waystone with the new number of waystones
            User user = getUserFromDB(owner);
            updateUser(user);

            stmt.close();

        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to delete wasytone.");
            System.exit(0);
        }
    }


}
