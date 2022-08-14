package eu.ovmc.waystones.events;

import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.User;
import eu.ovmc.waystones.Waystone;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WaystonePlace implements Listener {
//    https://www.spigotmc.org/wiki/using-the-event-api/
    
    @EventHandler
    public void waystonePlaced(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.LODESTONE)){
            //Get the block underneath the lodestone
            Block blockUnder = e.getBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK)){
                Player player = e.getPlayer();
                Waystone ws = new Waystone(e.getBlock().getLocation().toString(), e.getPlayer().getUniqueId().toString());

                //get user data from users table
                SQLiteJDBC jdbc = new SQLiteJDBC();
                User user = jdbc.getUserFromDB(player.getUniqueId().toString());

                //if player does not exists
                if(user == null){
                    //Register new player
                    jdbc.regPlayer(player);
                    user = jdbc.getUserFromDB(player.getUniqueId().toString());//once registered, store the user object so that it doesn't satay null and crash when trying to update the user.
                }

                //register the waystone
                jdbc.regWaystone(ws, user);
                player.sendMessage("Waystone registered!");

            }
        }
    }

}
