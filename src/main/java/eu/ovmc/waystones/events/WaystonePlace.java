package eu.ovmc.waystones.events;

import eu.ovmc.waystones.SQLiteJDBC;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.Connection;

public class WaystonePlace implements Listener {
//    https://www.spigotmc.org/wiki/using-the-event-api/
    
    @EventHandler
    public void waystonePlaced(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.LODESTONE)){
            if(e.getBlockAgainst().getType().equals(Material.EMERALD_BLOCK)){
                System.out.println("Nieeeees!!!!!!");
                Connection con = new SQLiteJDBC().connectWDB();

            }
        }
    }

}
