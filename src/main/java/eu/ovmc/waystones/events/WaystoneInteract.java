package eu.ovmc.waystones.events;

import eu.ovmc.waystones.SQLiteJDBC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.swing.*;
import java.sql.Connection;

public class WaystoneInteract implements Listener {
    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();
        if(action.equals(Action.RIGHT_CLICK_BLOCK)&&e.getClickedBlock().getType().equals(Material.LODESTONE)){
            Block blockUnder = e.getClickedBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK)) {
                Player player = e.getPlayer();
                player.sendMessage("Yes");

            }
        }


    }

}
