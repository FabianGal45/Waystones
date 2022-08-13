package eu.ovmc.waystones.events;
import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.Waystone;
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

        System.out.println("Broken: "+ block.getType() + " Under: "+ blockUnder.getType());

        if(block.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.EMERALD_BLOCK)){
            System.out.println("you broke a waystone");
            SQLiteJDBC jdbc = new SQLiteJDBC();
            System.out.println("Location: "+ block.getLocation());
            Waystone ws = jdbc.getWaystone(block.getLocation().toString());
            System.out.println("Loc2: "+ ws.getLocation());
            jdbc.remPrivateWs(ws);

        }

    }


}
