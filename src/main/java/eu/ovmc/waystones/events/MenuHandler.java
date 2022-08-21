package eu.ovmc.waystones.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;

public class MenuHandler implements Listener {

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

                }
                else if(e.getCurrentItem().getType().equals(Material.NETHERITE_BLOCK)){
                    player.sendMessage("You Clicked Netherite block!");
                }
            }
        }

    }


}
