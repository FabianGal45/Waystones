package eu.ovmc.waystones.events;

import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.menues.main.WaystonesSplitMenu;
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

public class WaystoneInteract implements Listener {

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();

        // Ignoring this event if player does not click on a block https://www.spigotmc.org/threads/errors-with-playerinteractevent-and-nameable.390258/
        if (e.getClickedBlock() == null) return;

        //If right-clicked a LODESTONE
        if(e.getClickedBlock().getType().equals(Material.LODESTONE) && action.equals(Action.RIGHT_CLICK_BLOCK) && !e.getPlayer().isSneaking()){
//            System.out.println("WaystonesInteract - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());

            Block blockUnder = e.getClickedBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

            //if block under is EMERALD_BLOCK or NETHERITE_BLOCK
            if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)) {
                SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
                String loc = e.getClickedBlock().getLocation().toString();
                PrivateWaystone ws = jdbc.getWaystone(loc);
                Player player = e.getPlayer();

                //Cancels the action of the left hand. Without this the following code will trigger twice.  https://www.spigotmc.org/threads/playerinteractevent-fires-twice-for-right-clicking.301622/
                if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {
                    e.setCancelled(true);
                    return;
                }

                //Check if the user exists if not register him.
                User user = jdbc.getUserFromUuid(player.getUniqueId().toString());
                if(user == null){
                    jdbc.regPlayer(player);
                    user = jdbc.getUserFromUuid(player.getUniqueId().toString()); //gets the updated user with the ID
                }

                //TESTING
//                User user = jdbc.getUserFromDB(player.getUniqueId().toString());
//                System.out.println("Can place: "+user.canPlacePrivateWs());

                //if waystone exists in the database
                if(ws != null){
                    e.setCancelled(true);
                    PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(player);
                    playerMenuUtility.setPrivateWaystones(jdbc.getAllPrivateWaystones(user.getId()));
                    playerMenuUtility.setPublicWaystones(jdbc.getAllPublicWaystones());
                    playerMenuUtility.setClickedOnWs(ws);

                    if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || (blockUnder.getType().equals(Material.NETHERITE_BLOCK) && ws instanceof PublicWaystone)){
//                        player.playSound(ws.getParsedLocation(ws.getLocation()), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
                        new WaystonesSplitMenu(playerMenuUtility, 0).open();
                    }
                    else{
                        if(ws instanceof PublicWaystone){
                            player.sendMessage("Chenge block under for Netherite Block");
                        }
                        else{
                            player.sendMessage("Change block under for Emerald Block");
                        }
                    }

                }else{
                  player.sendMessage(ChatColor.RED + "This waystone is inactive. Replace the Lodestone to activate.");
                }


            }
        }


    }

}
