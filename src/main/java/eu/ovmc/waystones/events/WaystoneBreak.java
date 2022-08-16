package eu.ovmc.waystones.events;
import eu.ovmc.waystones.PublicWaystone;
import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.PrivateWaystone;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WaystoneBreak implements Listener {
//    https://www.tutorialspoint.com/sqlite/sqlite_java.htm

    @EventHandler
    public void wsBroken(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        Block blockUnder = e.getBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();
        Block blockAbove = e.getBlock().getLocation().add(0.0, 1.0,0.0).getBlock();

        SQLiteJDBC jdbc = new SQLiteJDBC();

        if(block.getType().equals(Material.LODESTONE)){
            if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)){

                //if the waystone exists in the database
                PrivateWaystone ws = jdbc.getWaystone(block.getLocation().toString());
                if(ws != null){

                    if(ws instanceof PublicWaystone){
                        player.sendMessage("you broke a public waystone");
                    }else{
                        player.sendMessage("you broke a private waystone");
                    }
                    jdbc.remWs(ws);

                }
            }
        }

        if(block.getType().equals(Material.EMERALD_BLOCK) || block.getType().equals(Material.NETHERITE_BLOCK) && blockAbove.getType().equals(Material.LODESTONE)){

            //if the waystone exists in the database
            if(jdbc.getWaystone(blockAbove.getLocation().toString()) != null){
                player.sendMessage("You broke the base of a waystone. Disabling...");
                System.out.println("Location: "+ blockAbove.getLocation());
                PrivateWaystone ws = jdbc.getWaystone(blockAbove.getLocation().toString());
                jdbc.remWs(ws);
            }
        }

    }


}
