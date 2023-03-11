package eu.ovmc.waystones.menusystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0 ;
    protected int prevIndexWs;
    protected ArrayList<Integer> blankSlots;


    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility);
        this.page = page;

        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);
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
        compassMeta.lore(loreArray);
        compassItem.setItemMeta(compassMeta);

        inventory.setItem(49, compassItem);
    }
}
