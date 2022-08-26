package eu.ovmc.waystones.menusystem.menu;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PaginatedSplitMenu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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

        PlayerMenuUtility playerMU = WaystonesPlugin.getPlayerMenuUtility(player);
        ArrayList<PrivateWaystone> privateWaystones = playerMU.getPrivateWaystones();
        for(PrivateWaystone privWs : privateWaystones){
            System.out.println(privWs.getLocation());

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

                    //Upates the meta with the provided one
                    privateWs.setItemMeta(ptivateWsMeta);

                    //Add the item to the inventory
                    inventory.addItem(privateWs);
                }
            }
        }
    }
}
