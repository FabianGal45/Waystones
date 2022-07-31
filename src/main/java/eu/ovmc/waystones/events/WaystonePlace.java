package eu.ovmc.waystones.events;

import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.Waystone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WaystonePlace implements Listener {
//    https://www.spigotmc.org/wiki/using-the-event-api/
    
    @EventHandler
    public void waystonePlaced(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.LODESTONE)){
            //Get the block underneath the lodestone
            Block blockUnder = e.getBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK)){
                Player player = e.getPlayer();
                Waystone ws = new Waystone(e.getBlock().getLocation(), e.getPlayer().getUniqueId());

                SQLiteJDBC jdbc = new SQLiteJDBC();
                System.out.println(">1> Getting user data for player");
                ResultSet rs = jdbc.getDatafromUser(player);//get user data from users table
                try{
                    if(rs.next()){
                        System.out.println(">2> Player exists, calculating and adding 1 to the user data");

                        //Increase the number of private waystones
                        int original = rs.getInt("private_ws");
                        int newTotal = original +1;
                        jdbc.addPrivateWS(newTotal, player.getUniqueId().toString());
                    }
                    else {//if player does not exist in database, register him
                        System.out.println(">3> Player does not exist, creating default info");
                        jdbc.regPlayer(player);
                    }
                    System.out.println(">4> Registering Waystone");
                    jdbc.regWaystone(ws);//register the waystone

                }catch (Exception exception){
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                    System.out.println(exception +" Failed to retrieve user from users table.");
                    System.exit(0);
                }

                player.sendMessage("This is a waystone!");



            }
        }
    }

}
