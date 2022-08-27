package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PaginatedSplitMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.xml.stream.events.Namespace;
import java.util.ArrayList;
import java.util.List;

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
            player.sendMessage("You clicked Emerald");

            NamespacedKey namespacedKey = new NamespacedKey(WaystonesPlugin.getPlugin(), "index");
            int index = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(namespacedKey,PersistentDataType.INTEGER);
            PrivateWaystone selected = privateWaystones.get(index);

            //Teleports player above the selected waystone
            Location loc = selected.getParsedLocation().add(0.5,1.0,0.5);
            player.teleport(loc);

        }
        else if(e.getCurrentItem().getType().equals(Material.NETHERITE_BLOCK)){
            player.sendMessage("You Clicked Netherite block!");
        }
        else if(e.getCurrentItem().getType().equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
            System.out.println("Next page was selected");
//            plugin.openGUI(player);
        }
        else if(e.getCurrentItem().getType().equals(Material.BARRIER)){
//            ArrayList<SplitMenu> GUIs = plugin.getArrGUIs();

        }



    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        ArrayList<PrivateWaystone> privateWaystones = playerMenuUtility.getPrivateWaystones();

        if(privateWaystones != null && !privateWaystones.isEmpty()) {
            for(int i = 0; i < getMaxPrivateWs(); i++) {
                indexPrivWs = getMaxPrivateWs() * page + i;
                if(indexPrivWs >= privateWaystones.size()) break; //If the index has reached the number of players.
                PrivateWaystone ws = privateWaystones.get(indexPrivWs);
                if ( ws != null){
                    //Creates the Emerald block item
                    ItemStack privateWs = new ItemStack(Material.EMERALD_BLOCK);
                    ItemMeta ptivateWsMeta = privateWs.getItemMeta();

                    //Sets the name
                    ptivateWsMeta.displayName(Component.text("#"+indexPrivWs+" ")
                            .append(Component.text("Private Waystone").decoration(TextDecoration.ITALIC, false)));

                    //Creates the lore of the item
                    List<Component> loreArray = new ArrayList<>();
                    loreArray.add(Component.text("Location: "+ ws.getParsedLocation().getBlockX()+", "+ ws.getParsedLocation().getBlockY()+", "+ws.getParsedLocation().getBlockZ()));
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
    }
}
