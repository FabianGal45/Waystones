package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page;
    protected ArrayList<Integer> blankSlots;


    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility);
        this.page = page;

        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);
    }

    public void addMenuBorder(){
        for(Integer blankSlot : blankSlots) {
            ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta bpMeta = blackPanel.getItemMeta();
            TextComponent bpName = Component.text(" ");
            bpMeta.displayName(bpName);
            blackPanel.setItemMeta(bpMeta);
            inventory.setItem(blankSlot, blackPanel);
        }
    }


    @Override
    public Component getMenuName() {
        return null;
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {}

    @Override
    public void setMenuItems() {}

    public void commonMenuHandlers(InventoryClickEvent e){
        System.out.println("Common Handler running!");
        Material currentItem = e.getCurrentItem().getType();
        Player player = (Player) e.getWhoClicked();

        if (currentItem.equals(Material.RECOVERY_COMPASS)) {
            Inventory inventory1 = player.getInventory();

            if (inventory1.contains(Material.ECHO_SHARD)) {
                if (player.getLastDeathLocation() != null) {//if the player has died before
                    Location loc = WaystonesPlugin.getSafeLocation(player.getLastDeathLocation());
                    if (loc != null) {//If the place is safe to teleport
                        player.teleportAsync(loc);
                        player.playSound(loc, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1, 1);
                        inventory1.removeItem(new ItemStack(Material.ECHO_SHARD, 1));
                    }
                } else {
                    player.sendMessage(Component.text("You haven't died yet. ", NamedTextColor.DARK_RED)
                            .append(Component.text("Group hug with creepers?", NamedTextColor.RED)));
                }
            } else {
                player.sendMessage(Component.text("You need an ", NamedTextColor.DARK_RED)
                        .append(Component.text("Echo Shard ", NamedTextColor.RED)
                                .append(Component.text("to teleport.", NamedTextColor.DARK_RED))));
            }
        }
    }

    public void addCompass(PlayerMenuUtility playerMenuUtility){
        Player player = playerMenuUtility.getOwner();
        Location deathLocation = player.getLastDeathLocation();

        ItemStack compassItem = new ItemStack(Material.RECOVERY_COMPASS);
        ItemMeta compassMeta = compassItem.getItemMeta();
        compassMeta.displayName(Component.text("Death Location").decoration(TextDecoration.ITALIC, false));
        List<Component> loreArray = new ArrayList<>();
        if(deathLocation != null){
            String worldName;
            if(deathLocation.getWorld().getName().equals("world")){
                worldName = "World";
            } else if(deathLocation.getWorld().getName().equals("world_nether")) {
                worldName = "Nether";
            }
            else if(deathLocation.getWorld().getName().equals("world_the_end")){
                worldName = "End";
            }
            else{
                worldName = "Unknown";
            }

            loreArray.add(Component.text(worldName+": ", NamedTextColor.DARK_PURPLE)
                    .append(Component.text(deathLocation.getBlockX()+", "+deathLocation.getBlockY()+", "+ deathLocation.getBlockZ(),NamedTextColor.LIGHT_PURPLE)));
            loreArray.add(Component.text(""));
            loreArray.add(Component.text("Click: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("TP to Death Location", NamedTextColor.GRAY)));
            loreArray.add(Component.text("Cost: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Echo Shard x1", NamedTextColor.GRAY)));
        }
        else{
            loreArray.add(Component.text("You haven't died yet.", NamedTextColor.DARK_PURPLE));
            loreArray.add(Component.text(""));
            loreArray.add(Component.text("Click: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("TP to Death Location", NamedTextColor.GRAY)));
            loreArray.add(Component.text("Cost: ", NamedTextColor.DARK_GRAY)
                    .append(Component.text("Echo Shard x1", NamedTextColor.GRAY)));
        }
        compassMeta.lore(loreArray);
        compassItem.setItemMeta(compassMeta);

        inventory.setItem(49, compassItem);
    }


}
