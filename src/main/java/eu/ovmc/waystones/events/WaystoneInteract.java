package eu.ovmc.waystones.events;

import eu.ovmc.waystones.SQLiteJDBC;
import eu.ovmc.waystones.Waystone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.swing.*;
import java.sql.Connection;

public class WaystoneInteract implements Listener {
    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();

        //Cancels the action of the left hand. Without this the following code will trigger twice.  https://www.spigotmc.org/threads/playerinteractevent-fires-twice-for-right-clicking.301622/
        if(e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        if(e.getClickedBlock().getType().equals(Material.LODESTONE) && action.equals(Action.RIGHT_CLICK_BLOCK)){
            Block blockUnder = e.getClickedBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            if(blockUnder.getType().equals(Material.EMERALD_BLOCK)) {
                Player player = e.getPlayer();
                player.sendMessage("You right clicked a private waystone!");

                //Get the location of the lodestone and grab the object from the database.
                SQLiteJDBC jdbc = new SQLiteJDBC();
                String loc = e.getClickedBlock().getLocation().toString();
                Waystone ws = jdbc.getWaystone(loc);

                System.out.println("Object loc: "+ ws.getParsedLocation().toString());

                //TODO: Open the GUI

            }
        }


    }

}
