package eu.ovmc.waystones.events;

import eu.ovmc.waystones.menusystem.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;

public class MenuHandler implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){

        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu) {
            e.setCancelled(true); //stops player from moving the item.

            if(e.getCurrentItem() != null && e.getClickedInventory().getType().toString().equals("CHEST")){
                //Handle the menu withing the menu class.
                Menu menu = (Menu) holder;
                menu.handleMenu(e);
            }
        }

    }


}
