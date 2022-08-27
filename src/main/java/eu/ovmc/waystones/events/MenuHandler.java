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


/* ######################## Cancelled
        Player player = (Player) e.getWhoClicked();

        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title()); //converts the title Component into a string

//        InventoryHolder holder = e.getClickedInventory().getHolder();

        System.out.println("Slot: "+e.getSlot() + " Clicked Inventory: "+ e.getClickedInventory().getType() + " Holder: "+ holder);



        if(title.equalsIgnoreCase("Main GUI")){
            e.setCancelled(true);//stops player from moving the item.

            if(e.getCurrentItem() != null && e.getClickedInventory().getType().toString().equals("CHEST")){
                if(e.getCurrentItem().getType().equals(Material.EMERALD_BLOCK)){
                    player.sendMessage("You clicked Emerald");
                    ArrayList<PrivateWaystone> privWss = plugin.getLastOppenedMenu().getPrivateWaystones();

                    PrivateWaystone selected = privWss.get(e.getSlot()-10);
                    System.out.println(">>>>> "+selected.getLocation()+ " slot " + e.getSlot());

                    //Teleports player above the selected waystone
                    Location loc = selected.getParsedLocation().add(0.5,1.0,0.5);
                    player.teleport(loc);
                    if(e.getSlot()>53){
                        System.out.println("Personal inventory");
                    }
                    else{
                        System.out.println("Menu");
                    }

                }
                else if(e.getCurrentItem().getType().equals(Material.NETHERITE_BLOCK)){
                    player.sendMessage("You Clicked Netherite block!");
                }
                else if(e.getCurrentItem().getType().equals(Material.ARROW)){//Make it more precise player can click on any arrow including personal inventory.
                    System.out.println("Next page was selected");
                    plugin.openGUI(player);
                }
                else if(e.getCurrentItem().getType().equals(Material.BARRIER)){
                    ArrayList<SplitMenu> GUIs = plugin.getArrGUIs();



                }
            }
        }
##############################*/


    }


}
