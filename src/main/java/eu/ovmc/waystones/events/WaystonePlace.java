package eu.ovmc.waystones.events;

import eu.ovmc.waystones.waystones.PublicWaystone;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.ChatColor;
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
            Player player = e.getPlayer();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)){
                //get user data from users table
                SQLiteJDBC jdbc = new SQLiteJDBC();
                User user = jdbc.getUserFromDB(player.getUniqueId().toString());

                //if player does not exists
                if(user == null){
                    //Register new player
                    jdbc.regPlayer(player);
                    user = jdbc.getUserFromDB(player.getUniqueId().toString());//once registered, store the user object so that it doesn't satay null and crash when trying to update the user.
                }

                //If waystone does not already exists in the database at this location remove it.
                PrivateWaystone waystone = jdbc.getWaystone(e.getBlock().getLocation().toString());
                if(waystone == null){
                    //register the waystone
                    if(blockUnder.getType().equals(Material.EMERALD_BLOCK)){
                        PrivateWaystone ws = new PrivateWaystone(e.getBlock().getLocation().toString(), e.getPlayer().getUniqueId().toString(), null);
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Private waystone registered!");

                    }
                    else if(blockUnder.getType().equals(Material.NETHERITE_BLOCK)){
                        PublicWaystone ws = new PublicWaystone(e.getBlock().getLocation().toString(), e.getPlayer().getUniqueId().toString(), null, 0.1, 1.0, "shop");
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Public waystone registered!");
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Something went wrong when registering the waystone.");//This message should never get triggered
                    }
                }



            }






        }
    }

}
