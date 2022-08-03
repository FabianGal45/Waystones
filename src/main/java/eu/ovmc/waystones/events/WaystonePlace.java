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
                Waystone ws = new Waystone(e.getBlock().getLocation(), e.getPlayer().getUniqueId());

                //get user data from users table
                System.out.println(">1> Getting user data for player");
                SQLiteJDBC jdbc = new SQLiteJDBC();
                User user = jdbc.getUserFromDB(player);
                try{
                    //if player exists
                    if(user != null){
                        //+1 the number of private waystones in users data
                        System.out.println("User exists, +1 waystone");
                        int original = user.getPrivateWs();
                        int newTotal = original +1;
                        user.setPrivateWs(newTotal);
                        jdbc.updateUser(user);
                    }
                    else{
                        //Register new player
                        System.out.println(">3> Registering new payer");
                        jdbc.regPlayer(player);
                    }

                    //register the waystone
                    System.out.println(">4> Registering Waystone");
                    jdbc.regWaystone(ws);


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
