package eu.ovmc.waystones.menusystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PaginatedSplitMenu extends Menu {

    protected int page;
    protected final int MAX_PRIVATE = 7;
    protected final int MAX_PUBLIC = 14;
    protected int indexPrivWs = 0;
    protected int indexPubWs = 0;
    protected ArrayList<Integer> blankSlots;
    protected ArrayList<Integer> publicWsSlots;


    public PaginatedSplitMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility);
        this.page = page;

        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);

        publicWsSlots = new ArrayList<>();
        Collections.addAll(publicWsSlots, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);

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

            loreArray.add(Component.text(worldName+": ",NamedTextColor.DARK_PURPLE)
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

    public void addMenuPageButtons(int pubWsSize){
        if(indexPrivWs + 1 >= MAX_PRIVATE * (page+1) || pubWsSize > MAX_PUBLIC * (page +1)){
            inventory.setItem(50, makeItem(Material.ARROW, "Next Page"));
        }
        if(page != 0){
            inventory.setItem(48, makeItem(Material.BARRIER, "Back"));
        }

    }



}
