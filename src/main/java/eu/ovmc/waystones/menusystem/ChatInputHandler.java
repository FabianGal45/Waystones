package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, PrivateWaystone> chatInputMap = new HashMap<>();

    PlayerMenuUtility playerMenuUtility;

    public HashMap<Player, PrivateWaystone> getChatInputMap(){//Get the map when needed
       return chatInputMap;
   }

    public void changeWsName(PlayerMenuUtility playerMenuUtility, Player player, PrivateWaystone selected){
        chatInputMap.put(player, selected);
        this.playerMenuUtility = playerMenuUtility;
    }

    public void handleChatInput(AsyncPlayerChatEvent e){
        //Rename the waystone
        PrivateWaystone selected = chatInputMap.get(e.getPlayer());
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        //Set the name of the waystone with the input from player
        selected.setName(e.getMessage());

        //Update the name in the database
        jdbc.updateWaystone(selected);

        e.getPlayer().sendMessage("The name has ben set to: "+selected.getName());
        chatInputMap.remove(e.getPlayer());


        //Running this synchronously
        final CountDownLatch latch = new CountDownLatch(1);

        Bukkit.getScheduler().runTask(WaystonesPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                // Perform the synchronous operation

                //Reopen the last menu
                new EditMenu(playerMenuUtility, selected).open();


                // When the operation is complete, count down the latch
                latch.countDown();
            }
        });

        try {
            // Wait for the latch to reach zero
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }



    }

}
