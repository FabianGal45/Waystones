package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, Object> playerList = new HashMap<>();

    public HashMap<Player, Object> getChatMap(){//Get the map when needed
       return playerList;
   }

    public void changeWsName(Player player, EditMenuUtility editMenuUtility){
        playerList.put(player, editMenuUtility);
    }

    public void handleChatInput(AsyncPlayerChatEvent e){
        if(playerList.get(e.getPlayer()) instanceof EditMenuUtility){ //If the reason you are taking the input is to edit then...
            System.out.println("ChatInputHandler - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
            e.setCancelled(true);


            //Rename the waystone
            EditMenuUtility emu = (EditMenuUtility) playerList.get(e.getPlayer());
            SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

            //Set the name of the waystone with the input from player
            emu.getSelected().setName(e.getMessage());

            //Update the name in the database
            jdbc.updateWaystone(emu.getSelected());

            e.getPlayer().sendMessage("The name has ben set to: "+emu.getSelected().getName());
            playerList.remove(e.getPlayer());

            //Reopen the last menu
            emu.returnToMenu();

        }


    }

}
