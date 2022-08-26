package eu.ovmc.waystones.menusystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public abstract class PaginatedSplitMenu extends Menu {

    protected int page = 0;
    protected int maxPrivateWs = 7;
    protected int maxPublicWs = 14;
    protected int indexPrivWs = 0;
    protected int indexPubWs = 0;

    public PaginatedSplitMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public void addMenuBorder(){
        inventory.setItem(48, makeItem(Material.DARK_OAK_BUTTON, ChatColor.GREEN + "Left"));
        inventory.setItem(49, makeItem(Material.RECOVERY_COMPASS, "Death location"));
        inventory.setItem(50, makeItem(Material.ARROW, "Right"));

        ArrayList<Integer> blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 51, 52, 53);
        for(Integer blankSlot : blankSlots) {
            ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta bpMeta = blackPanel.getItemMeta();
            TextComponent bpName = Component.text(" ");
            bpMeta.displayName(bpName);
            blackPanel.setItemMeta(bpMeta);
            inventory.setItem(blankSlot, blackPanel);
        }
    }

    public int getMaxPrivateWs() {
        return maxPrivateWs;
    }

    public int getMaxPublicWs() {
        return maxPublicWs;
    }
}
