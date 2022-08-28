package eu.ovmc.waystones.events;

import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.menu.WaystonesSplitMenu;
import eu.ovmc.waystones.waystones.PublicWaystone;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.WaystonesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;

public class WaystoneInteract implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();

        //Cancels the action of the left hand. Without this the following code will trigger twice.  https://www.spigotmc.org/threads/playerinteractevent-fires-twice-for-right-clicking.301622/
        if(e.getHand().equals(EquipmentSlot.OFF_HAND)) return;

        // Ignoring this event if player does not click on a block https://www.spigotmc.org/threads/errors-with-playerinteractevent-and-nameable.390258/
        if (e.getClickedBlock() == null) return;

        //If right clicked a LODESTONE
        if(e.getClickedBlock().getType().equals(Material.LODESTONE) && action.equals(Action.RIGHT_CLICK_BLOCK)){
            Block blockUnder = e.getClickedBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            //if block under is EMERALD_BLOCK or NETHERITE_BLOCK
            if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)) {
                SQLiteJDBC jdbc = new SQLiteJDBC();
                String loc = e.getClickedBlock().getLocation().toString();
                PrivateWaystone ws = jdbc.getWaystone(loc);
                Player player = e.getPlayer();

                //if waystone exists in the database
                if(ws != null){
                    if(ws instanceof PublicWaystone){
                        player.sendMessage("This is a public waystone.");
                    }
                    else{
                        player.sendMessage("This is a private waystone.");
                    }

                    ArrayList<PrivateWaystone> arrPrivateWaystones;
                    arrPrivateWaystones = jdbc.getAllPrivateWaystones(player.getUniqueId().toString());

                    PlayerMenuUtility pmu = WaystonesPlugin.getPlayerMenuUtility(player);
                    pmu.setPrivateWaystones(jdbc.getAllPrivateWaystones(player.getUniqueId().toString()));
                    pmu.setClickedOnWs(ws);
                    new WaystonesSplitMenu(pmu).open();

                }else{
                  player.sendMessage(ChatColor.RED + "This waystone does not exist in the database!");
                }


            }
        }


    }

}
