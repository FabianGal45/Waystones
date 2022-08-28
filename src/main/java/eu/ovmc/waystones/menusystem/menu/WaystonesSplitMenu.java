package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
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


        if(e.getCurrentItem().getType().equals(Material.EMERALD_BLOCK)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected = privateWaystones.get(index);

            System.out.println(WaystonesPlugin.getPlugin().getDescription().getVersion());

            //Teleports player above the selected waystone
            Location loc = selected.getParsedLocation().add(0.5,1.0,0.5);
            player.teleport(loc);

        }
        else if(e.getCurrentItem().getType().equals(Material.NETHERITE_BLOCK)){
            player.sendMessage("You Clicked Netherite block!");
        }
        else if(e.getCurrentItem().getType().equals(Material.LIME_CONCRETE_POWDER)){
            player.sendMessage("You are already at this location");
        }
        else if(e.getCurrentItem().getType().equals(Material.CRACKED_STONE_BRICKS)){
            //Grab the index from the NBT data of the block
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = Objects.requireNonNull(itemMeta.getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER));
            PrivateWaystone selected = privateWaystones.get(index);

            player.sendMessage(Component.text("This waystone has been damaged! ("+selected.getParsedLocation().getBlockX()+", "+selected.getParsedLocation().getBlockY()+", "+ selected.getParsedLocation().getBlockZ()+")",
                            TextColor.fromHexString("#802f45")));
        }
        else if(e.getCurrentItem().getType().equals(Material.BARRIER)){
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
        else if(e.getCurrentItem().getType().equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
            System.out.println("Next page was selected");
            if (!((indexPrivWs + 1) >= privateWaystones.size())){
                page = page + 1;
                super.open();
            }else{
                //check if there are more public waystones than the slots then open a simple paginated menu.
                player.sendMessage("You are on the last page.");
            }
//            plugin.openGUI(player);
        }



    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();

        if(privateWaystones != null && !privateWaystones.isEmpty()) {
            for(int i = 0; i < getMaxPrivateWs(); i++) {
                indexPrivWs = getMaxPrivateWs() * page + i;
                if(indexPrivWs >= privateWaystones.size()){
                    indexPrivWs = i-1;
                    break; //If the index has reached the number of players.
                }
                PrivateWaystone ws = privateWaystones.get(indexPrivWs);
                if (ws != null){
                    ItemStack privateWs;

                    Block blockTop = ws.getParsedLocation().getBlock();
                    Block blockUnder = ws.getParsedLocation().subtract(0.0,1.0,0.0).getBlock();

                    boolean validPrivWs = blockTop.getType().equals(Material.LODESTONE) && blockUnder.getType().equals(Material.EMERALD_BLOCK);

                    //if this is the waystone he clicked on make it lime green
                    if(ws.getLocation().equals(playerMenuUtility.getClickedOnWs().getLocation())){
                        //Creates the LimeConcretePowder block item
                        privateWs = new ItemStack(Material.LIME_CONCRETE_POWDER);
                    }
                    else if(!validPrivWs){//if waystone not valid mark it with a cracked stone
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
                    if(ws.getParsedLocation().getWorld().getName().equals("world")){
                        worldName = "World";
                    } else if(ws.getParsedLocation().getWorld().getName().equals("world_nether")) {
                        worldName = "Nether";
                    }
                    else if(ws.getParsedLocation().getWorld().getName().equals("world_the_end")){
                        worldName = "End";
                    }
                    else{
                        worldName = "Unknown";
                    }


                    loreArray.add(Component.text(worldName +": "+ ws.getParsedLocation().getBlockX()+", "+ ws.getParsedLocation().getBlockY()+", "+ws.getParsedLocation().getBlockZ()));
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

        addMenuPageButtons(privateWaystones.size());
    }
}
