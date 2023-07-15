package eu.ovmc.waystones.menusystem.menues.main;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PaginatedMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.items.*;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class WaystonesSplitMenu extends PaginatedMenu {

    private final int MAX_PRIVATE = 7;
    private final int MAX_PUBLIC = 14;
    private int indexPubWs = 0;
    private int indexPrvWs;
    private final ArrayList<Integer> PUBLIC_WS_SLOTS;


    public WaystonesSplitMenu(PlayerMenuUtility playerMenuUtility, int page) {
        super(playerMenuUtility, page);

        //define new border for the blank slots aside from the default one in PaginatedMenu
        blankSlots = new ArrayList<>();
        Collections.addAll(blankSlots, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53);

        PUBLIC_WS_SLOTS = new ArrayList<>();
        Collections.addAll(PUBLIC_WS_SLOTS, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
    }

    @Override
    public Component getMenuName() {
        if(playerMenuUtility.getClickedOnWs() != null){
            int userId = playerMenuUtility.getClickedOnWs().getUserId();
            User user = WaystonesPlugin.getPlugin().getJdbc().getUser(userId);
            return Component.text( user.getUserName()+ "'s Waystone"); //todo fix this with openAs
        }else{
            return Component.text("OPEN AS");
        }

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

            if (page == 0) {
                player.sendMessage("This is the first page.");
            } else {
                if(oppenedByAdmin == null){
                    page = page - 1;
                    super.open();
                }
                else{
                    page = page - 1;
                    super.openAs(oppenedByAdmin);
                }

            }
//            ArrayList<SplitMenu> GUIs = plugin.getArrGUIs();

        }
        else if(currentItemType == ItemType.PAGE_FORWARD){
//            System.out.println("Next page was selected");
            player.playSound(player.getLocation(), Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 1, 2);

            User user = playerMenuUtility.getUser();
//            System.out.println(">>>> "+ (user.getAllowedPrivWs())+ " < "+ (MAX_PRIVATE * (page + 1)));
            if(user.getAllowedPrivWs() < (MAX_PRIVATE * (page + 1))){ //get allowed < page max
                if(oppenedByAdmin == null){ //if this menu has no admin assigned that might have oppened it then run it for the player
                    page = page + 1;
                    new PublicWsMenu(playerMenuUtility, page, indexPubWs).open();
                } else{ //if there is an admin that has oppened this menu then continue openning for the admin
                    page = page + 1;
                    new PublicWsMenu(playerMenuUtility, page, indexPubWs).openAs(oppenedByAdmin);
                }
            }
            else{
                if(oppenedByAdmin == null){ //if this menu has no admin assigned that might have oppened it then run it for the player
                    page = page + 1;
                    super.open();
                } else{ //if there is an admin that has oppened this menu then continue openning for the admin
                    page = page + 1;
                    super.openAs(oppenedByAdmin);
                }
            }

        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();
        ArrayList<PublicWaystone> publicWaystones = playerMenuUtility.getPublicWaystones();


        //loop for each slot available to private waystones (7)
        for(int i = 0; i < MAX_PRIVATE; i++) {
            indexPrvWs = MAX_PRIVATE * page + i;//7*0+1= 1 | 7*0+2= 2 | ... | 7*1+1= 8 | 7*1+2= 9

            //If finished placing the existing waystones, before running out of space start placing the dyes
            if(indexPrvWs >= privateWaystones.size()){
                User user = playerMenuUtility.getUser();
                int pu = user.getAllowedPrivWs()-privateWaystones.size();//the amount of purchased and unused waystones

                //loop 7 times
                for(int j = 0; j< MAX_PRIVATE; j++){

                    //the position at which the dye to be placed
                    int dyePos = indexPrvWs -(MAX_PRIVATE * page)+10;// If there are 5 waystones in the first menu: 5-(7*0)+10 = 15

                    //if green dye have reached the pu AND the dyePos <= 16 (16 being the last available position in the GUI)
//                    System.out.println(">>>>>  "+ indexPrivWs +" - " + privateWaystones.size() +" < pu: "+pu);
                    if(indexPrvWs - privateWaystones.size() < pu && dyePos <= 16){
//                        Component.text("Available").color(TextColor.fromCSSHexString("#93cf98")).decoration(TextDecoration.ITALIC, false);
                        MenuItem limeDye = new MenuItem(Material.LIME_DYE, ItemType.BLANK, "Available");
                        inventory.setItem(dyePos ,limeDye.getDisplayItem());
                    }
                    else{
                        //if there is still space, add a grey dye
//                        System.out.println("indexPrivWs: "+ indexPrivWs + " size: "+ privateWaystones.size() + " PU: "+ pu + " allowed: "+user.getAllowedPrivWs());
                        if(indexPrvWs < MAX_PRIVATE*(page+1) && user.getAllowedPrivWs() == indexPrvWs){
                            MIPurchase grayDye = new MIPurchase(Material.GRAY_DYE, ItemType.PURCHASE, "Buy more");
                            grayDye.setLoreDescription(user, playerMenuUtility);
                            inventory.addItem(grayDye.getDisplayItem());
                            indexPrvWs--;
                        }
//                        System.out.println("INDEX>>> "+ indexPrivWs);
                        break;
                    }
                    indexPrvWs++;
                }
//                System.out.println("INDEX>>> "+ indexPrivWs);
                break; //If the index has reached the number of waystones.
            }

            PrivateWaystone ws = privateWaystones.get(indexPrvWs);
            if (ws != null){
                Block blockTop = TeleportHandler.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = TeleportHandler.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();
                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.EMERALD_BLOCK));

                MIPrivateWaystone prvWaystone = new MIPrivateWaystone(Material.EMERALD_BLOCK, indexPrvWs, ws, playerMenuUtility);

                //if this is the waystone he opened the menu from, make the item lime green
                if(playerMenuUtility.getClickedOnWs() != null){
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        prvWaystone = new MIPrivateWaystone(Material.LIME_CONCRETE, ItemType.OPENED_PRIVATE_WAYSTONE, indexPrvWs, ws, playerMenuUtility);
                    }
                    else if(damagedWs){//if waystone is damaged, mark it with a cracked stone
                        prvWaystone = new MIPrivateWaystone(Material.CRACKED_STONE_BRICKS, ItemType.BROKEN, indexPrvWs, ws, playerMenuUtility);
                    }
                }

                //Add the item to the inventory
                inventory.addItem(prvWaystone.getDisplayItem());
            }
        }

        //Public waystones
        for(int i = 0; i < MAX_PUBLIC; i++) {
            indexPubWs = MAX_PUBLIC * page + i;
//            System.out.println("INDEX>>> "+ indexPrivWs);
//            System.out.println("Index PUB: "+indexPubWs + " Page: "+ page);
            if(indexPubWs >= publicWaystones.size()){
                indexPubWs--;
                break;
            }
            PublicWaystone ws = publicWaystones.get(indexPubWs);
            if (ws != null) {
                Block blockTop = TeleportHandler.getParsedLocation(ws.getLocation()).getBlock();
                Block blockUnder = TeleportHandler.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();
                boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.NETHERITE_BLOCK));

                MIPublicWaystone publicWs = new MIPublicWaystone(Material.NETHERITE_BLOCK, indexPubWs, ws, playerMenuUtility);
                //if this is the waystone he clicked on make it Black Concrete
                if(playerMenuUtility.getClickedOnWs() != null){
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        publicWs = new MIPublicWaystone(Material.BLACK_CONCRETE, ItemType.OPENED_PUBLIC_WAYSTONE, indexPubWs, ws, playerMenuUtility);
                    }
                    else if(damagedWs){//if waystone is damaged, mark it with a cracked stone
                        publicWs = new MIPublicWaystone(Material.CRACKED_STONE_BRICKS, ItemType.BROKEN, indexPubWs, ws, playerMenuUtility);
                    }
                }

                //Add the item to the inventory
                int pos = PUBLIC_WS_SLOTS.get(i);
                inventory.setItem(pos, publicWs.getDisplayItem());
            }
        }

        //After all the waystones have been placed, add the compass and page buttons
        addCompass(playerMenuUtility);
        addMenuPageButtons(publicWaystones.size());
    }

    private void addMenuPageButtons(int pubWsSize){
        if(indexPrvWs + 1 >= MAX_PRIVATE * (page+1) || pubWsSize > MAX_PUBLIC * (page +1)){
            MenuItem nextPageItem = new MenuItem(Material.ARROW, ItemType.PAGE_FORWARD, "Next Page");
            inventory.setItem(50, nextPageItem.getDisplayItem());
        }
        if(page != 0){
            MenuItem backPageItem = new MenuItem(Material.BARRIER, ItemType.PAGE_BACK, "Back");
            inventory.setItem(48, backPageItem.getDisplayItem());
        }

    }

}
