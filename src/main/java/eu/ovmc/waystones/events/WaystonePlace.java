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
                Connection con = jdbc.getCon();
                jdbc.regWaystone(con, ws);

                player.sendMessage("This is a waystone!");



            }
        }
    }

}
