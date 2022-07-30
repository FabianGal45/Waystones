package eu.ovmc.waystones.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class WaystonePlace implements Listener {
//    https://www.spigotmc.org/wiki/using-the-event-api/
    
    @EventHandler
    public void waystonePlaced(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.LODESTONE)){
            if(e.getBlockAgainst().getType().equals(Material.EMERALD_BLOCK)){
                Player player = e.getPlayer();
                player.sendMessage("That's a waystone!");
            }
        }
    }

}
