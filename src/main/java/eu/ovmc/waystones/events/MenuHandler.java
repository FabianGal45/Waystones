package eu.ovmc.waystones.events;

import eu.ovmc.waystones.PrivateWaystone;
import eu.ovmc.waystones.Waystones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Objects;

public class MenuHandler implements Listener {

    Waystones plugin;

    public MenuHandler(Waystones plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();

        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title()); //converts the title Component into a string

        //Todo: if get current item is null then ignore or do nothing

        if(title.equalsIgnoreCase("Main GUI")){
            e.setCancelled(true);
            if(e.getCurrentItem() != null){
                if(e.getCurrentItem().getType().equals(Material.EMERALD_BLOCK)){
                    player.sendMessage("You clicked Emerald");
                    ArrayList<PrivateWaystone> privWss = plugin.getPrivWSs();
                    System.out.println(">>>>> "+privWss.get(e.getSlot()-10).getLocation()+ " slot " + e.getSlot());

                }
                else if(e.getCurrentItem().getType().equals(Material.NETHERITE_BLOCK)){
                    player.sendMessage("You Clicked Netherite block!");
                }
            }
        }

    }


}
