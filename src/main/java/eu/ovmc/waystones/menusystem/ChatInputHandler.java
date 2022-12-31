package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, EditMenu> TEXT_INPUT_MAP = new HashMap<>();
    private static final HashMap<Player, EditMenu> CHAT_CLICK_MAP = new HashMap<>();

    public void addPlayerToTextMap(Player player, EditMenu editMenu){
        TEXT_INPUT_MAP.put(player, editMenu);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                TEXT_INPUT_MAP.remove(player);
                System.out.println("TEXT_INPUT_MAP timed out!");
            }
        },1200);//run after 60 seconds
    }

    public void addPlayerToChatClickMap(Player player, EditMenu editMenu){
        CHAT_CLICK_MAP.put(player, editMenu);


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                CHAT_CLICK_MAP.remove(player);
                System.out.println("CHAT_CLICK_MAP timed out!");
            }
        },1200);//run after 60 seconds
    }

    public void handleChatInput(AsyncPlayerChatEvent e){
        //Rename the waystone
        PrivateWaystone selected = TEXT_INPUT_MAP.get(e.getPlayer()).getSelected();
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        //Set the name of the waystone with the input from player
        selected.setName(e.getMessage());

        //If the waystone is still in the database e.g. player selects to change name but then destroys waystone.
        if(jdbc.getWaystone(selected.getLocation()) != null){
            //Update the name in the database
            jdbc.updateWaystone(selected);

            e.getPlayer().sendMessage(Component.text("Name set to: ", NamedTextColor.GRAY)
                    .append(Component.text(selected.getName(), NamedTextColor.WHITE)));

            openPreviousEditMenu(TEXT_INPUT_MAP.get(e.getPlayer()));
            e.setCancelled(true);
        }
        TEXT_INPUT_MAP.remove(e.getPlayer());
    }

    public void handleRemoveWs(Player player){
        PrivateWaystone selected = CHAT_CLICK_MAP.get(player).getSelected();
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

        jdbc.remWs(selected);
        CHAT_CLICK_MAP.remove(player);
        TEXT_INPUT_MAP.remove(player);//remove from here as a precaution. You can't edit something that doesn't exist
        player.sendMessage(Component.text("Waystone had been removed.", NamedTextColor.GREEN));
    }

    private void openPreviousEditMenu(EditMenu editMenu){

        if(Bukkit.isPrimaryThread()){
            System.out.println("PRIMARY THREAD!!");
            editMenu.open();
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
                    editMenu.open();

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

    public void removePlayerFromTextMap(Player player){
        openPreviousEditMenu(TEXT_INPUT_MAP.get(player));
        TEXT_INPUT_MAP.remove(player);
    }

    public void removePlayerFromChatClickMap(Player player){
        openPreviousEditMenu(CHAT_CLICK_MAP.get(player));
        CHAT_CLICK_MAP.remove(player);
    }

    public HashMap<Player, EditMenu> getTextInputMap(){//Get the map when needed
        return TEXT_INPUT_MAP;
    }

    public HashMap<Player, EditMenu> getChatClickMap(){
        return CHAT_CLICK_MAP;
    }


}
