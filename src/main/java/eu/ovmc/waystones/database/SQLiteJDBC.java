package eu.ovmc.waystones.database;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;

public class SQLiteJDBC {
    private Connection con;
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

    public void createTables(){
        Statement stmt;
        try {
            stmt = getCon().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS users " + //Creates the users table
                    "(uuid varchar(255)," +
                    " user_name VARCHAR(255)," +
                    " private_ws INT(255)," +
                    " public_ws INT(255)," +
                    " purchased_private_ws INT(255)," +
                    " acquired_private_ws INT(255)," +
                    " acquired_public_ws INT(255)," +
                    " PRIMARY KEY(uuid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS private_waystones " + //Creates the private_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " name VARCHAR(255)," +
                    " tp_location VARCHAR(255)," +
                    " PRIMARY KEY(location)" +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(uuid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS public_waystones " +  //Creates the public_waystones table
                    "(location varchar(255)," +
                    " owner VARCHAR(255)," +
                    " name VARCHAR(255)," +
                    " tp_location VARCHAR(255)," +
                    " cost DOUBLE(40, 2)," +
                    " rating DOUBLE(40, 2)," +
                    " PRIMARY KEY(location)" +
                    " FOREIGN KEY (owner)" +
                    "  REFERENCES users(uuid))";
            stmt.executeUpdate(sql);
            sql = "CREATE TABLE IF NOT EXISTS ratings " +  //Creates the public_waystones table
                    "(public_waystone VARCHAR(255)," +
                    " player_uuid VARCHAR(255)," +
                    " player_name VARCHAR(255)," +
                    " rate INT(255)," +
                    " FOREIGN KEY (public_waystone)" +
                    "  REFERENCES public_waystones(location))";
            stmt.executeUpdate(sql);
            stmt.close();

            //Prints the table names to console to check if they exist
            DatabaseMetaData meta = getCon().getMetaData();
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
                System.out.println("Reg - TP loc: "+ws.getTpLocation());
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
            //Query to insert data into tutorials_data table
            String query = "INSERT INTO ratings (" +
                    "public_waystone,"+
                    "player_uuid," +
                    "player_name," +
                    "rate) VALUES(?, ?, ?, ?)";

            //Creating the preparedStatement object
            PreparedStatement pstmt = getCon().prepareStatement(query);
            String playerUUID = String.valueOf(player.getUniqueId());

            pstmt.setString(1, ws.getLocation());
            pstmt.setString(2, playerUUID);
            pstmt.setString(3, player.getName());
            pstmt.setInt(4, rate);
            pstmt.execute();
            pstmt.close();

            //Start calculating the final rate based on all entries.
            ArrayList<Integer> ratesList = getAllRatesForPubWs(ws);

            int a = 0, b = 0, c = 0, d = 0, e = 0, r = ratesList.size();
            for(Integer i :ratesList){
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
            ws.setRating(finalRate);
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
            stmt = getCon().createStatement();
            String sql = "SELECT * FROM ratings WHERE public_waystone = '"+ ws.getLocation() +"' and player_uuid = '"+ player.getUniqueId() +"';";
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
            String sql = "SELECT * FROM ratings WHERE public_waystone = '"+ ws.getLocation() +"';";
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
                user = new User(rs.getString("uuid"),
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
                user = new User(rs.getString("uuid"),
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
                ws = new PrivateWaystone(rs.getString("location"), rs.getString("owner"), rs.getString("name"), rs.getString("tp_location"));
            }

            sql = "SELECT * FROM public_waystones WHERE location = '"+ location +"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                ws = new PublicWaystone(rs.getString("location"), rs.getString("owner"), rs.getString("name"), rs.getString("tp_location"));
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
                ws = new PrivateWaystone(rs.getString("location"), rs.getString("owner"), rs.getString("name"),  rs.getString("tp_location"));
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
                ws = new PublicWaystone(rs.getString("location"),
                        rs.getString("owner"),
                        rs.getString("name"),
                        rs.getString("tp_location"),
                        rs.getDouble("rating"),
                        rs.getInt("cost"));
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
            System.out.println("User "+ user.getUuid() +" updated!");
            stmt.close();
        }catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.out.println(e +" Failed to update the private waystone number to the user.");
            System.exit(0);
        }


    }

    public void updateWaystone(PrivateWaystone ws){

        if(ws instanceof PublicWaystone){
            try{
                String sql = "UPDATE public_waystones" +
                        " SET name = ?, tp_location = ?, cost = ?, rating = ?" +
                        " WHERE location = ?;";


                PreparedStatement pstmt = getCon().prepareStatement(sql);
                pstmt.setString(1, ws.getName());
                pstmt.setString(2, ws.getLocation());
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
                System.out.println("Waystone removed.");
            }

            sql = "SELECT * FROM public_waystones WHERE location = '"+ ws.getLocation() +"'";
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                owner = rs.getString("owner");
                loc = rs.getString("location");

                //Delete the waystone
                sql = "DELETE FROM public_waystones WHERE location = '"+ loc +"'";
                stmt.executeUpdate(sql);
                System.out.println("Waystone removed.");
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
