package eu.ovmc.waystones.menusystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0 ;
    protected int startingPage;
    protected int prevIndexPubWs;
    protected int maxPublicWs = 28;
    protected int indexPubWs = 0;
    protected ArrayList<Integer> blankSlots;
    protected ArrayList<Integer> publicWsSlots;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, int startingPage, int prevIndexPubWs) {
        super(playerMenuUtility);
        this.startingPage = startingPage;
        this.prevIndexPubWs = prevIndexPubWs;

        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);

        publicWsSlots = new ArrayList<>();
        Collections.addAll(publicWsSlots, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    public void addMenuBorder(){

        inventory.setItem(49, makeItem(Material.RECOVERY_COMPASS, "Death location"));

        for(Integer blankSlot : blankSlots) {
            ItemStack blackPanel = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta bpMeta = blackPanel.getItemMeta();
            TextComponent bpName = Component.text(" ");
            bpMeta.displayName(bpName);
            blackPanel.setItemMeta(bpMeta);
            inventory.setItem(blankSlot, blackPanel);
        }
    }

    public void addMenuPageButtons(int pubWsSize){
        System.out.println("pubWsSize: "+ pubWsSize +" " + (pubWsSize - prevIndexPubWs -1 ) + " > "+ (maxPublicWs * (page+1)) );
        if(pubWsSize - prevIndexPubWs - 1 > maxPublicWs * (page+1)){
            inventory.setItem(50, makeItem(Material.ARROW, "Next Page"));
        }

        inventory.setItem(48, makeItem(Material.BARRIER, "Back"));


    }




}
