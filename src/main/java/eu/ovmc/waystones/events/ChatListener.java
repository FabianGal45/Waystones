package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;


public class ChatListener implements Listener {
    // @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();

    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncPlayerChat(AsyncPlayerChatEvent e){ //I have to use a deprecated method as the old plugins use it as the default and canceling a new event won't work

        if(chatInputHandler.getChatInputMap().containsKey(e.getPlayer())){//If the player exists in the hash map, waiting to get an input
            if(!e.getMessage().equalsIgnoreCase("cancel")){
                chatInputHandler.handleChatInput(e);
            }
            else{
                e.setCancelled(true);
                chatInputHandler.removePlayerFromList(e.getPlayer());
            }
        }
    }


}

