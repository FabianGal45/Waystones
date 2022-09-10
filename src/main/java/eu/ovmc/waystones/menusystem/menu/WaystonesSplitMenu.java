package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.menusystem.PaginatedSplitMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WaystonesSplitMenu extends PaginatedSplitMenu {


    public WaystonesSplitMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public Component getMenuName() {
        return Component.text("Your waystones");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        //TODO: Handle the menu here

        Player player = (Player) e.getWhoClicked();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();

        Material currentItem = e.getCurrentItem().getType();

        if(currentItem.equals(Material.EMERALD_BLOCK)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected = privateWaystones.get(index);

            System.out.println(WaystonesPlugin.getPlugin().getDescription().getVersion());

            //safety feature
            selected.safeTeleport(player);
            //TODO: use papers tasync teleportation

        }
        else if(currentItem.equals(Material.NETHERITE_BLOCK)){
            player.sendMessage("You Clicked Netherite block!");
        }
        else if(currentItem.equals(Material.LIME_CONCRETE_POWDER)){
            player.sendMessage("You are already at this location");
        }
        else if(currentItem.equals(Material.CRACKED_STONE_BRICKS)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected = privateWaystones.get(index);

            player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation(selected.getLocation()).getBlockX()+", "+selected.getParsedLocation(selected.getLocation()).getBlockY()+", "+ selected.getParsedLocation(selected.getLocation()).getBlockZ()+")",
                            TextColor.fromHexString("#802f45")));
        }
        else if(currentItem.equals(Material.GRAY_DYE)){
            User user = playerMenuUtility.getUser();
            int purchased = user.getPurchasedPrivateWs();
            int total = purchased +1;
            user.setPurchasedPrivateWs(total);

            SQLiteJDBC jdbc = new SQLiteJDBC();
            jdbc.updateUser(user);

            super.open();
        }
        else if(currentItem.equals(Material.BARRIER)){
            String itemName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta().displayName()));
            if (itemName.equals("Back")) {
                if (page == 0) {
                    player.sendMessage("This is the first page.");
                } else {
                    page = page - 1;
                    super.open();
                }
//            ArrayList<SplitMenu> GUIs = plugin.getArrGUIs();
            }
        }
        else if(currentItem.equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
            System.out.println("Next page was selected");
            //Todo: Check if withing the first page there is a gray dye. If it is then open the big menu


//            if ((indexPrivWs + 1) <= privateWaystones.size()){
                page = page + 1;
                super.open();
//            }else{
//                //check if there are more public waystones than the slots then open a simple paginated menu.
//                player.sendMessage("You are on the last page.");
//            }
//            plugin.openGUI(player);
        }
        else if (currentItem.equals(Material.RECOVERY_COMPASS) && e.getClick().isRightClick()) {
            User user = playerMenuUtility.getUser();
            int purchased = user.getPurchasedPrivateWs();
            int total = purchased +1;
            user.setPurchasedPrivateWs(total);

            SQLiteJDBC jdbc = new SQLiteJDBC();
            jdbc.updateUser(user);

            super.open();
        }
        else if (currentItem.equals(Material.RECOVERY_COMPASS) && e.getClick().isLeftClick()) {
            User user = playerMenuUtility.getUser();
            int purchased = user.getPurchasedPrivateWs();
            int total = purchased -1;
            user.setPurchasedPrivateWs(total);

            SQLiteJDBC jdbc = new SQLiteJDBC();
            jdbc.updateUser(user);

            super.open();
        }


    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();
        if(privateWaystones != null && !privateWaystones.isEmpty()) {
            //loop for each slot available to private waystones (7)
            for(int i = 0; i < getMaxPrivateWs(); i++) {
                indexPrivWs = getMaxPrivateWs() * page + i;
                System.out.println("Index1: "+ indexPrivWs +" = "+ getMaxPrivateWs()+" * "+ page+ " + " + i);

                //Todo Make it so that dyes get placed even if there are no waystones
                //TODO: take in a ccount the free waystones.

                System.out.println("ws: "+ privateWaystones.size());

                //If finished placing the existing waystones before running out of space start placing the dyes
                if(indexPrivWs >= privateWaystones.size()){
                    User user = playerMenuUtility.getUser();
                    //the amount of purchased and unused waystones
                    int pu = user.getAllowedWs()-privateWaystones.size();
                    System.out.println("PU: "+ pu);

                    //loop 7 times
                    for(int j=0;j<getMaxPrivateWs();j++){

                        //the position at which the dye to be placed
                        int pos = indexPrivWs-(getMaxPrivateWs()*page)+10;

                        //if green dye have reached the pu AND the pos <= 16
                        if( (indexPrivWs+1) - privateWaystones.size() <= pu && pos <= getMaxPrivateWs()+9){
                            ItemStack limeDye = new ItemStack(Material.LIME_DYE);
                            ItemMeta limeMeta = limeDye.getItemMeta();
                            limeMeta.displayName(Component.text("Available").color(TextColor.fromCSSHexString("#93cf98")).decoration(TextDecoration.ITALIC, false));
                            limeDye.setItemMeta(limeMeta);

                            inventory.setItem(pos ,limeDye);
                        }
                        else{
                            //if there is still space, add a grey dye
                            if(indexPrivWs<getMaxPrivateWs()*(page+1)){
                                ItemStack grayDye = new ItemStack(Material.GRAY_DYE);
                                ItemMeta grayMeta = grayDye.getItemMeta();
                                grayMeta.displayName(Component.text("Buy More"));
                                List<Component> loreArray = new ArrayList<>();
                                loreArray.add(Component.text("Cost: " + user.getPrice()));
                                grayMeta.lore(loreArray);
                                grayDye.setItemMeta(grayMeta);


                                inventory.addItem(grayDye);
                                indexPrivWs--;
                            }
                            break;
                        }
                        indexPrivWs++;
                    }

                    break; //If the index has reached the number of waystones.
                }

                PrivateWaystone ws = privateWaystones.get(indexPrivWs);
                if (ws != null){
                    ItemStack privateWs;

                    Block blockTop = ws.getParsedLocation(ws.getLocation()).getBlock();
                    Block blockUnder = ws.getParsedLocation(ws.getLocation()).subtract(0.0,1.0,0.0).getBlock();

                    boolean damagedWs = !(blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.EMERALD_BLOCK));

                    //if this is the waystone he clicked on make it lime green
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        //Creates the LimeConcretePowder block item
                        privateWs = new ItemStack(Material.LIME_CONCRETE);
                    }
                    else if(damagedWs){//if waystone is damaged, mark it with a cracked stone
                        privateWs = new ItemStack(Material.CRACKED_STONE_BRICKS);
                    }
                    else{
                        //Creates the Emerald block item
                        privateWs = new ItemStack(Material.EMERALD_BLOCK);
                    }


                    ItemMeta ptivateWsMeta = privateWs.getItemMeta();

                    //Sets the name
                    ptivateWsMeta.displayName(Component.text("#"+(indexPrivWs+1)+" ")
                            .append(Component.text("Private Waystone").decoration(TextDecoration.ITALIC, false)));

                    //Creates the lore of the item
                    List<Component> loreArray = new ArrayList<>();
                    String worldName;
                    if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world")){
                        worldName = "World";
                    } else if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world_nether")) {
                        worldName = "Nether";
                    }
                    else if(ws.getParsedLocation(ws.getLocation()).getWorld().getName().equals("world_the_end")){
                        worldName = "End";
                    }
                    else{
                        worldName = "Unknown";
                    }


                    loreArray.add(Component.text(worldName +": "+ ws.getParsedLocation(ws.getLocation()).getBlockX()+", "+ ws.getParsedLocation(ws.getLocation()).getBlockY()+", "+ws.getParsedLocation(ws.getLocation()).getBlockZ()));
                    ptivateWsMeta.lore(loreArray);

                    //Stores the index of the waystone from the waystones list into the NBT meta of that file so that it can be identified when clicked.
                    ptivateWsMeta.getPersistentDataContainer().set(new NamespacedKey(WaystonesPlugin.getPlugin(), "index"), PersistentDataType.INTEGER, indexPrivWs);

                    //Upates the meta with the provided one
                    privateWs.setItemMeta(ptivateWsMeta);

                    //Add the item to the inventory
                    inventory.addItem(privateWs);
                }
            }



        }

        addMenuPageButtons(indexPrivWs);
    }
}
