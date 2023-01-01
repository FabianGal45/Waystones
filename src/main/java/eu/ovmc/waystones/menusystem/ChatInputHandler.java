package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, EditMenu> RENAME_MAP = new HashMap<>();
    private static final HashMap<Player, EditMenu> REMOVE_MAP = new HashMap<>();
    private static final HashMap<Player, EditMenu> COST_MAP = new HashMap<>();
    SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

    public void addToRenameMap(Player player, EditMenu editMenu){
        RENAME_MAP.put(player, editMenu);
        startCountdown(RENAME_MAP,player);
    }

    public void addToRemoveMap(Player player, EditMenu editMenu){
        REMOVE_MAP.put(player, editMenu);
        startCountdown(REMOVE_MAP, player);
    }

    public void addToCostMap(Player player ,EditMenu editMenu){
        COST_MAP.put(player, editMenu);
        startCountdown(COST_MAP,player);
    }

    private void startCountdown(HashMap<Player, EditMenu> hashMap, Player player){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                hashMap.remove(player);
            }
        },1200);//run after 60 seconds
    }

    public void handleRename(AsyncPlayerChatEvent e){
        //Rename the waystone
        PrivateWaystone selected = RENAME_MAP.get(e.getPlayer()).getSelected();

        //Set the name of the waystone with the input from player
        selected.setName(e.getMessage());

        //If the waystone is still in the database e.g. player selects to change name but then destroys waystone.
        if(jdbc.getWaystone(selected.getLocation()) != null){
            //Update the name in the database
            jdbc.updateWaystone(selected);

            e.getPlayer().sendMessage(Component.text("Name set to: ", NamedTextColor.GRAY)
                    .append(Component.text(selected.getName(), NamedTextColor.WHITE)));

            openPreviousEditMenu(RENAME_MAP.get(e.getPlayer()));
            e.setCancelled(true);
        }
        RENAME_MAP.remove(e.getPlayer());
    }

    public void handleRemove(Player player){
        PrivateWaystone selected = REMOVE_MAP.get(player).getSelected();

        jdbc.remWs(selected);
        REMOVE_MAP.remove(player);
        RENAME_MAP.remove(player);//remove from here as a precaution. You can't edit something that doesn't exist
        COST_MAP.remove(player);//Can't update something that doesn't exist.
        player.sendMessage(Component.text("Waystone had been removed.", NamedTextColor.GREEN));
    }

    public void handleCost(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        PrivateWaystone selected = COST_MAP.get(player).getSelected();

        if(selected instanceof PublicWaystone){
            System.out.println("Detected a change in price for a Public Waystone");
            PublicWaystone selectedPublicWs = (PublicWaystone) selected;

            if(isInt(e.getMessage())){
                selectedPublicWs.setCost(Integer.parseInt(e.getMessage()));
            }else{
                player.sendMessage(Component.text("That was not a number that can be used. Try again", NamedTextColor.DARK_RED));
            }
            openPreviousEditMenu(COST_MAP.get(player));
            e.setCancelled(true);
        }
        else {
            System.out.println("this waystone is not a public Waystone");
        }

        jdbc.updateWaystone(selected);
        COST_MAP.remove(player);
    }

    private boolean isInt(String s){
        try{
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex){
            return false;
        }
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

    public void removePlayerFromRenameMap(Player player){
        openPreviousEditMenu(RENAME_MAP.get(player));
        RENAME_MAP.remove(player);
    }

    public void removePlayerFromRemoveMap(Player player){
        openPreviousEditMenu(REMOVE_MAP.get(player));
        REMOVE_MAP.remove(player);
    }

    public void removePlayerFromCostMap(Player player){
        openPreviousEditMenu(COST_MAP.get(player));
        COST_MAP.remove(player);
    }

    public HashMap<Player, EditMenu> getRenameMap(){//Get the map when needed
        return RENAME_MAP;
    }

    public HashMap<Player, EditMenu> getRemoveMap(){
        return REMOVE_MAP;
    }

    public HashMap<Player, EditMenu> getCostMap(){
        return COST_MAP;
    }


}
