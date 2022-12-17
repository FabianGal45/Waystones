package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.ChatInputHandler;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AbstractChatEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {
    // @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void asyncPlayerChat(AsyncChatEvent e){
//
//        ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();
//
//        if(chatInputHandler.getChatMap().containsKey(e.getPlayer())){//If the player exists in the hash map, waiting to get an input
//            System.out.println("ChatListener - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
//            System.out.println("This player is in the list. Handling...");
//            chatInputHandler.handleChatInput(e);
//            e.setCancelled(true);
//        }
//    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncPlayerChatOld(AsyncPlayerChatEvent e){ //I have to use a deprecated method as the old plugins use it as the default and canceling a new event won't work

        ChatInputHandler chatInputHandler = WaystonesPlugin.getPlugin().getChatInputHandler();

        if(chatInputHandler.getChatMap().containsKey(e.getPlayer())){//If the player exists in the hash map, waiting to get an input
            System.out.println("ChatListener - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
            System.out.println("This player is in the list. Handling...");
            chatInputHandler.handleChatInput(e);
            e.setCancelled(true);
        }
    }



//    @EventHandler
//    public void abstractChatEvent(ChatRenderer e){
//        e.render()
//        System.out.println("AAAAA aaa AAA");
//    }
}

