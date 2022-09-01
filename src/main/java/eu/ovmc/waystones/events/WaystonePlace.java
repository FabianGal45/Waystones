package eu.ovmc.waystones.events;

import eu.ovmc.waystones.waystones.PublicWaystone;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WaystonePlace implements Listener {
//    https://www.spigotmc.org/wiki/using-the-event-api/
    
    @EventHandler
    public void waystonePlaced(BlockPlaceEvent e){

        Player player = e.getPlayer();

        //Block player from placing a block when right clicking the waystone
        if(e.getBlockAgainst().getType().equals(Material.LODESTONE) && !e.getPlayer().isSneaking()){
            SQLiteJDBC jdbc = new SQLiteJDBC();
            if(jdbc.getWaystone(e.getBlockAgainst().getLocation().toString()) != null){
                e.setCancelled(true);
            }
        }

        if(e.getBlock().getType().equals(Material.LODESTONE)){
            Block blockUnder = e.getBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();
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

                //If waystone does not already exists in the database at this location register it. Otherwise just let it use the old data
                PrivateWaystone waystone = jdbc.getWaystone(e.getBlock().getLocation().toString());
                if(waystone == null){
                    //register the waystone
                    String tpLocation;
                    tpLocation = player.getLocation().toString();
                    if(blockUnder.getType().equals(Material.EMERALD_BLOCK)){
                        PrivateWaystone ws = new PrivateWaystone(e.getBlock().getLocation().toString(), e.getPlayer().getUniqueId().toString(), null, tpLocation);
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Private waystone registered!");

                    }
                    else if(blockUnder.getType().equals(Material.NETHERITE_BLOCK)){
                        PublicWaystone ws = new PublicWaystone(e.getBlock().getLocation().toString(), e.getPlayer().getUniqueId().toString(), null, tpLocation, 0.1, 1.0, "shop");
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Public waystone registered!");
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Something went wrong when registering the waystone.");//This message should never get triggered
                    }
                }
                else {
                    player.sendMessage("Waystone Restored.");
                }
            }
        }

        if((e.getBlock().getType().equals(Material.EMERALD_BLOCK) || e.getBlock().getType().equals(Material.NETHERITE_BLOCK))){
            Block blockAbove = e.getBlock().getLocation().add(0.0 , 1.0, 0.0).getBlock();
            SQLiteJDBC jdbc = new SQLiteJDBC();
            PrivateWaystone waystone = jdbc.getWaystone(blockAbove.getLocation().toString());

            if(blockAbove.getType().equals(Material.LODESTONE)){
                //get user data from users table

                User user = jdbc.getUserFromDB(player.getUniqueId().toString());

                //if player does not exists
                if(user == null){
                    //Register new player
                    jdbc.regPlayer(player);
                    user = jdbc.getUserFromDB(player.getUniqueId().toString());//once registered, store the user object so that it doesn't satay null and crash when trying to update the user.
                }

                //If waystone does not already exists in the database at this location register it. Otherwise just let it use the old data
                if(waystone == null){
                    //register the waystone
                    String tpLocation;
                    tpLocation = player.getLocation().toString();
                    if(e.getBlock().getType().equals(Material.EMERALD_BLOCK)){
                        PrivateWaystone ws = new PrivateWaystone(blockAbove.getLocation().toString(), e.getPlayer().getUniqueId().toString(), null, tpLocation);
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Private waystone registered!");

                    }
                    else if(e.getBlock().getType().equals(Material.NETHERITE_BLOCK)){
                        PublicWaystone ws = new PublicWaystone(blockAbove.getLocation().toString(), e.getPlayer().getUniqueId().toString(), null, tpLocation, 0.1, 1.0, "shop");
                        jdbc.regWaystone(ws, user);
                        player.sendMessage("Public waystone registered!");
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Something went wrong when registering the waystone.");//This message should never get triggered
                    }
                }
                else if((e.getBlock().getType().equals(Material.EMERALD_BLOCK) && !(waystone instanceof PublicWaystone)) || ((e.getBlock().getType().equals(Material.NETHERITE_BLOCK)) && waystone instanceof PublicWaystone)) {
                    player.sendMessage("Waystone Restored.");
                }
                else{
                    player.sendMessage("This is not like the original waystone!");
                }


            }
        }




    }

}
