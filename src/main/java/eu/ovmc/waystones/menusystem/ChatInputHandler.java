package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, PrivateWaystone> chatInputMap = new HashMap<>();
    PlayerMenuUtility playerMenuUtility;

    public void addPlayerToList(PlayerMenuUtility playerMenuUtility, Player player, PrivateWaystone selected){
        chatInputMap.put(player, selected);
        this.playerMenuUtility = playerMenuUtility;
    }

    public void handleChatInput(AsyncPlayerChatEvent e){
        //Rename the waystone
        PrivateWaystone selected = chatInputMap.get(e.getPlayer());
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        //Set the name of the waystone with the input from player
        selected.setName(e.getMessage());

        //If the waystone is still in the database e.g. player selects to change name but then destroys waystone.
        if(jdbc.getWaystone(selected.getLocation()) != null){
            //Update the name in the database
            jdbc.updateWaystone(selected);

//            e.getPlayer().sendMessage("The name has ben set to: "+selected.getName());

            e.getPlayer().sendMessage(Component.text("Name set to: ", NamedTextColor.GRAY)
                    .append(Component.text(selected.getName(), NamedTextColor.WHITE)));

            openPreviousEditMenu(e.getPlayer());
            e.setCancelled(true);
        }
        chatInputMap.remove(e.getPlayer());
    }

    private void openPreviousEditMenu(Player player){
        PrivateWaystone selected = chatInputMap.get(player);

        if(Bukkit.isPrimaryThread()){
            System.out.println("PRIMARY THREAD!!");
            new EditMenu(playerMenuUtility, selected).open();
        }
        else{
            System.out.println("NOT PRIMARY THREAD!!");
            //Running this synchronously & Opening back the edit menu
            //Places this thread on hold which is async, waits for the menu to open on the main thread, then continues back to this thread
            final CountDownLatch latch = new CountDownLatch(1);
            Bukkit.getScheduler().runTask(WaystonesPlugin.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    // Perform the synchronous operation

                    //Reopen the Edit menu
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

    public void removePlayerFromList(Player player){
        openPreviousEditMenu(player);
        chatInputMap.remove(player);
    }

    public HashMap<Player, PrivateWaystone> getChatInputMap(){//Get the map when needed
        return chatInputMap;
    }


}
