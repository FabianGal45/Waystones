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
                System.out.println(">1> Getting user data for player");
                SQLiteJDBC jdbc = new SQLiteJDBC();
                User user = jdbc.getUserFromDB(player.getUniqueId().toString());
                try{
                    //if player does not exists
                    if(user == null){
                        //Register new player
                        System.out.println(">3> Registering new payer");
                        jdbc.regPlayer(player);
                        user = jdbc.getUserFromDB(player.getUniqueId().toString());//once registered, store the user object so that it doesn't satay null and crash when trying to update the user.
                    }
                    //register the waystone
                    System.out.println(">4> Registering Waystone");
                    jdbc.regWaystone(ws);

                    //Todo: should happen automatically when calling the method
                    //Update the number of waystones that the user has
                    System.out.println("User exists, +1 waystone");
                    jdbc.updateUser(user);


                }catch (Exception exception){
                    System.err.println(exception.getClass().getName() + ": " + exception.getMessage());
                    System.out.println(exception +" Failed to eat beans");
                    System.exit(0);
                }

                player.sendMessage("This is a waystone!");



            }
        }
    }

}
