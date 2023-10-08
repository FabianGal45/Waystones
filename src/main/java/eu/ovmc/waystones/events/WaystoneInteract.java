package eu.ovmc.waystones.events;

import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.menues.main.WaystonesSplitMenu;
import eu.ovmc.waystones.waystones.PublicWaystone;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.WaystonesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WaystoneInteract implements Listener {

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent e){
        Action action = e.getAction();
        Player player = e.getPlayer();
        PlayerMenuUtility playerMenuUtility = WaystonesPlugin.getPlayerMenuUtility(player);
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemMeta mainHandItemMeta = itemInMainHand.getItemMeta();
        CompassMeta compassMeta = null;
        boolean linkedCompass = false;
        long compassCooldownMillis = WaystonesPlugin.getPlugin().getConfig().getInt("CompassCooldownSeconds")* 1000L;


        //Cancels the action of the left hand. Without this the following code will trigger twice.  https://www.spigotmc.org/threads/playerinteractevent-fires-twice-for-right-clicking.301622/
        if(e.getHand().equals(EquipmentSlot.OFF_HAND)) {
            e.setCancelled(true);
            return;
        }

        //if item in hand is a compass set the meta
        if(mainHandItemMeta instanceof CompassMeta){
            compassMeta = (CompassMeta) mainHandItemMeta;
        }

        // if the player clicked on a block https://www.spigotmc.org/threads/errors-with-playerinteractevent-and-nameable.390258/
        if (e.getClickedBlock() != null){
            //If right-clicked a LODESTONE while not sneaking open the menu
            if(e.getClickedBlock().getType().equals(Material.LODESTONE) && action.equals(Action.RIGHT_CLICK_BLOCK) && !e.getPlayer().isSneaking()){
//            System.out.println("WaystonesInteract - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
                linkedCompass = true;
                Block blockUnder = e.getClickedBlock().getLocation().subtract(0.0,1.0,0.0).getBlock();

                //if block under is EMERALD_BLOCK or NETHERITE_BLOCK
                if(blockUnder.getType().equals(Material.EMERALD_BLOCK) || blockUnder.getType().equals(Material.NETHERITE_BLOCK)) {
                    SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
                    String loc = e.getClickedBlock().getLocation().toString();
                    PrivateWaystone ws = jdbc.getWaystone(loc);


                    //if waystone exists in the database
                    if(ws != null){
                        if(compassMeta != null){ //if the player has a compass set it up and skip opening the menu
                            //Set the Compass name
                            compassMeta.displayName(Component.text(ws.getName()));

                            //Set the lore
                            List<Component> loreList = new ArrayList<>();
                            Location location = TeleportHandler.getParsedLocation(ws.getLocation());
                            String shortWorldName;
                            if(location.getWorld().getName().equals("world")){
                                shortWorldName = "World";
                            } else if(location.getWorld().getName().equals("world_nether")) {
                                shortWorldName = "Nether";
                            }
                            else if(location.getWorld().getName().equals("world_the_end")){
                                shortWorldName = "End";
                            }
                            else{
                                shortWorldName = "Unknown";
                            }

                            Component locText = Component.text(shortWorldName +": ", NamedTextColor.DARK_PURPLE)
                                    .append(Component.text(location.getBlockX()+", "+ location.getBlockY()+", "+location.getBlockZ(), NamedTextColor.LIGHT_PURPLE));

                            loreList.add(locText);
                            loreList.add(Component.text("Owner: ", NamedTextColor.DARK_PURPLE)
                                    .append(Component.text(ws.getUser().getUserName(),NamedTextColor.LIGHT_PURPLE)));
                            compassMeta.lore(loreList);
                            itemInMainHand.setItemMeta(compassMeta);
                        }
                        else{//continue opening the menu
                            e.setCancelled(true);
                            playerMenuUtility.updatePrivateWaystones();
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
                        }
                    }else{
                        player.sendMessage(ChatColor.RED + "This waystone is inactive. Replace the Lodestone to activate.");
                    }
                }
            }
        }

        if(compassMeta != null && !linkedCompass){ //if the compass was not linked with a waystone earlier then tp
            if(compassMeta.hasLodestone()){
                PrivateWaystone ws = WaystonesPlugin.getPlugin().getJdbc().getWaystone(compassMeta.getLodestone().getBlock().getLocation().toString());
                if(ws != null){// if the lodestone is a waystone
                    //if the player is not in the cooldown list OR the current time - the time of the last run action is higher than the set cooldown then TP
                    if(!cooldown.containsKey(player.getUniqueId()) || System.currentTimeMillis() - cooldown.get(player.getUniqueId()) >= compassCooldownMillis){
                        cooldown.put(player.getUniqueId(),System.currentTimeMillis());

                        Location tpLocation = TeleportHandler.getParsedLocation(ws.getTpLocation());
                        TeleportHandler.safeTeleport(player, playerMenuUtility, tpLocation);// safe teleport to the location
                    }
                    else{
                        long millis = compassCooldownMillis - (System.currentTimeMillis() - cooldown.get(player.getUniqueId()));
                        int seconds = (int) (millis/1000)+1;
                        player.sendMessage(Component.text("Your compass is on cooldown for: ", NamedTextColor.DARK_RED)
                                .append(Component.text(seconds, NamedTextColor.RED)
                                        .append(Component.text(" sec.", NamedTextColor.DARK_RED))));
                    }
                }
            }
        }


    }

}
