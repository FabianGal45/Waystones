package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;

public class ChatListener implements Listener {

    @EventHandler
    public void AsyncPlayerChat(AsyncChatEvent e){

        ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();

        if(chatInputHandler.getChatMap().containsKey(e.getPlayer())){//If the player exists in the hash map, waiting to get an input
            System.out.println("This player is in the list. Handling...");
            chatInputHandler.handleChatInput(e);
        }
    }
}

