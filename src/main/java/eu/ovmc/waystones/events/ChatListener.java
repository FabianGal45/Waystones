package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.handlers.ChatInputHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {
    // @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();

    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncPlayerChat(AsyncPlayerChatEvent e){ //I have to use a deprecated method as the old plugins use it as the default and canceling a new event won't work

        if(chatInputHandler.getRenameMap().containsKey(e.getPlayer())){//If the player exists in the hash map, waiting to get an input
            if(!e.getMessage().equalsIgnoreCase("cancel")){
                chatInputHandler.handleRename(e);
            }
            else{
                e.setCancelled(true);
                chatInputHandler.removePlayerFromRenameMap(e.getPlayer());
            }
        }
        else if(chatInputHandler.getCostMap().containsKey(e.getPlayer())){
            if(!e.getMessage().equalsIgnoreCase("cancel")){
                chatInputHandler.handleCost(e);
            }
            else{
                e.setCancelled(true);
                chatInputHandler.removePlayerFromCostMap(e.getPlayer());
            }
        }
    }


}

