package eu.ovmc.waystones.menusystem.menues.main;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PaginatedMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.items.ItemType;
import eu.ovmc.waystones.menusystem.items.MIPublicWaystone;
import eu.ovmc.waystones.menusystem.items.MenuItem;
import eu.ovmc.waystones.menusystem.menues.interactive.PublicWaystoneEditMenu;
import eu.ovmc.waystones.menusystem.menues.interactive.PublicWaystoneRateEditMenu;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PublicWsMenu extends PaginatedMenu {
    private final int MAX_PUBLIC_WS = 28;
    private int indexPubWs = 0;
    private ArrayList<Integer> publicWsSlots;
    private int carriedIndexPubWs;

    public PublicWsMenu(PlayerMenuUtility playerMenuUtility, int page, int carriedIndexPubWs) {
        super(playerMenuUtility, page);

        this.carriedIndexPubWs = carriedIndexPubWs;

        publicWsSlots = new ArrayList<>();
        Collections.addAll(publicWsSlots, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public Component getMenuName() {
        return Component.text("Public Waystones");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        commonMenuHandlers(e);
        Player player = (Player) e.getWhoClicked();

        ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
        NamespacedKey itemTypeKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "item_type");
        ItemType currentItemType = ItemType.valueOf(itemMeta.getPersistentDataContainer().get(itemTypeKey,PersistentDataType.STRING));

        if(currentItemType == ItemType.PAGE_BACK){
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            if(page == 1){
                if(oppenedByAdmin == null){
                    new WaystonesSplitMenu(playerMenuUtility, 0).open();
                }else{
                    new WaystonesSplitMenu(playerMenuUtility, 0).openAs(oppenedByAdmin);
                }
            }
            else{
                if(oppenedByAdmin == null){
                    page = page - 1;
                    super.open();
                }else{
                    page = page - 1;
                    super.openAs(oppenedByAdmin);
                }
            }
        }
        else if(currentItemType == ItemType.PAGE_FORWARD){
            //Make it more precise player can click on any arrow including personal inventory.
            //System.out.println("Next page was selected");
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);
            page = page + 1;
            if (oppenedByAdmin == null) {
                super.open();
            }else{
                super.openAs(oppenedByAdmin);
            }
        }

    }

//    private void openEditMenu(Player player, PublicWaystone selected) {
//        if(player.getUniqueId().toString().equals(selected.getOwner()) || player.hasPermission("waystones.admin")){
//            new PublicWaystoneEditMenu(playerMenuUtility, selected).open();
//        }else{
//            if(!WaystonesPlugin.getPlugin().getJdbc().hasPlayerRated(player,selected)){
//                new PublicWaystoneRateEditMenu(playerMenuUtility, selected).open();
//            }else{
//                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
//            }
//        }
//    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();

        for(int i = 0; i < MAX_PUBLIC_WS; i++) {
            indexPubWs = (MAX_PUBLIC_WS * page) + i - (carriedIndexPubWs + 1); // (28*1)+0+(0+1)=29 | (28*1)+1+(0+1)=30 | (28*1)+2+(0+1)=31
//            System.out.println("indexPubWs: "+ indexPubWs + " = ("+ MAX_PUBLIC_WS +" * "+ page + ") + "+ i + " - ("+carriedIndexPubWs + "+ 1)  | Page: "+ page + " PublicWaystones Size: "+ publicWaystones.size());

            if(indexPubWs >= publicWaystones.size()){
//                System.out.println("STOP!");
                indexPubWs--;
                break;
            }

            PublicWaystone ws = publicWaystones.get(indexPubWs);
            if (ws != null) {
                MIPublicWaystone publicWs = new MIPublicWaystone(Material.NETHERITE_BLOCK, indexPubWs, ws, playerMenuUtility);
                Block blockTop = ws.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = ws.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();
                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.NETHERITE_BLOCK));

                //Changes the Menu item type based on if it is damaged or the selected waystone
                if(playerMenuUtility.getClickedOnWs() != null) {
                    if (ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())) {
                        publicWs = new MIPublicWaystone(Material.BLACK_CONCRETE, ItemType.OPENED_PUBLIC_WAYSTONE, indexPubWs, ws, playerMenuUtility);
                    } else if (damagedWs) {
                        publicWs = new MIPublicWaystone(Material.CRACKED_STONE_BRICKS, ItemType.OPENED_PUBLIC_WAYSTONE, indexPubWs, ws, playerMenuUtility);
                    }
                }

                //Add the item to the inventory
                int pos = publicWsSlots.get(i);
                inventory.setItem(pos, publicWs.getDisplayItem());
            }
        }

        addMenuPageButtons(publicWaystones.size());
        addCompass(playerMenuUtility);
    }

    private void addMenuPageButtons(int pubWsSize){
//        System.out.println("pubWsSize: "+ pubWsSize +" " + (pubWsSize - prevIndexPubWs -1 ) + " > "+ (maxPublicWs * (page+1)) );
        if(pubWsSize - 1 > MAX_PUBLIC_WS * page){
            MenuItem nextPage = new MenuItem(Material.ARROW, ItemType.PAGE_FORWARD, "Next Page");
            inventory.setItem(50, nextPage.getDisplayItem());
        }

        MenuItem backPage = new MenuItem(Material.BARRIER, ItemType.PAGE_BACK, "Back");
        inventory.setItem(48, backPage.getDisplayItem());


    }
}
