package eu.ovmc.waystones.events;
import eu.ovmc.waystones.waystones.PublicWaystone;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class WaystoneBreak implements Listener {
//    https://www.tutorialspoint.com/sqlite/sqlite_java.htm

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void wsBroken(BlockBreakEvent e){

        Block block = e.getBlock();
        Block blockAbove = e.getBlock().getLocation().add(0.0, 1.0,0.0).getBlock();

        if(block.getType().equals(Material.LODESTONE)){
            Player player = e.getPlayer();
            Block blockUnder = e.getBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();
            SQLiteJDBC jdbc = new SQLiteJDBC();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)){

                //if the waystone exists in the database
                PrivateWaystone ws = jdbc.getWaystone(block.getLocation().toString());
                if(ws != null){

                    player.playSound(ws.getParsedLocation(ws.getLocation()), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1, 2);
                    if(ws instanceof PublicWaystone){
                        player.sendMessage("you broke a public waystone");
                    }else{
                        player.sendMessage("you broke a private waystone");
                    }
                    jdbc.remWs(ws);

                }
            }
        }

        if((block.getType().equals(Material.EMERALD_BLOCK) || block.getType().equals(Material.NETHERITE_BLOCK)) && blockAbove.getType().equals(Material.LODESTONE)){
            Player player = e.getPlayer();
            SQLiteJDBC jdbc = new SQLiteJDBC();

            PrivateWaystone ws = jdbc.getWaystone(blockAbove.getLocation().toString());

            if(block.getType().equals(Material.NETHERITE_BLOCK) && !(ws instanceof PublicWaystone)) {
                System.out.println("Allowed since this was meant to be a private waystone");
            }
            else if(block.getType().equals(Material.EMERALD_BLOCK) && ws instanceof PublicWaystone){
                System.out.println("Allowed since this was meant to be a public waystone");
            }
            //if the waystone exists in the database
            else if(jdbc.getWaystone(blockAbove.getLocation().toString()) != null){
                player.sendMessage("You broke the base of a waystone. Disabling...");
                System.out.println("Location: "+ blockAbove.getLocation());
                jdbc.remWs(ws);
            }
        }

    }


}
