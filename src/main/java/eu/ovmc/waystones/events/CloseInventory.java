package eu.ovmc.waystones.events;

import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.menusystem.menu.EditMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class CloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        InventoryHolder holder = e.getInventory().getHolder();

        System.out.println("A menu closed."+ holder);
        if(holder instanceof EditMenu){
            System.out.println("An Edit Menu was closed!!");
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        System.out.println("A menu Opened: " + holder);
    }
}
