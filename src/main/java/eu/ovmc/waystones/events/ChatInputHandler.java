package eu.ovmc.waystones.events;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.menu.WaystonesSplitMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ChatInputHandler {
    //This class will be handling all utility classes for all chat request-response needs

    private static final HashMap<Player, PlayerMenuUtility> RENAME_MAP = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> REMOVE_MAP = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> COST_MAP = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> TPA_LIST = new HashMap<>();
    private static final HashMap<Player, PlayerMenuUtility> TPA_ACCEPT_LIST = new HashMap<>(); //the player teleporting / The initiator
    private static final HashMap<Player, PlayerMenuUtility> UNSAFE_TP_LIST = new HashMap<>();
    SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();

    public void addToRenameMap(Player player, PlayerMenuUtility playerMenuUtility){
        RENAME_MAP.put(player, playerMenuUtility);
        startCountdown(RENAME_MAP,player,1200);
    }

    public void addToRemoveMap(Player player, PlayerMenuUtility playerMenuUtility){
        REMOVE_MAP.put(player, playerMenuUtility);
        startCountdown(REMOVE_MAP, player,1200);
    }

    public void addToCostMap(Player player ,PlayerMenuUtility playerMenuUtility){
        COST_MAP.put(player, playerMenuUtility);
        startCountdown(COST_MAP,player,1200);
    }

    public void addToTpaMap(Player player, PlayerMenuUtility playerMenuUtility){
        TPA_LIST.put(player, playerMenuUtility);
        startCountdown(TPA_LIST, player, 300);
    }

    public void addToUnsafeTPMap(Player player, PlayerMenuUtility playerMenuUtility){
        UNSAFE_TP_LIST.put(player, playerMenuUtility);
        startCountdown(UNSAFE_TP_LIST, player, 300);
    }

    private void startCountdown(HashMap<Player, PlayerMenuUtility> hashMap, Player player, int delay){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(WaystonesPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                hashMap.remove(player);
            }
        },delay);//run after 60 seconds
    }

    public void handleRename(AsyncPlayerChatEvent e){
        //Rename the waystone
        PrivateWaystone selected = (RENAME_MAP.get(e.getPlayer())).getSelected();

        //Set the name of the waystone with the input from player
        selected.setName(e.getMessage());

        //If the waystone is still in the database e.g. player selects to change name but then destroys waystone.
        if(jdbc.getWaystone(selected.getLocation()) != null){
            //Update the name in the database
            jdbc.updateWaystone(selected);

            e.getPlayer().sendMessage(Component.text("Name set to: ", NamedTextColor.GRAY)
                    .append(Component.text(selected.getName(), NamedTextColor.WHITE)));

//            openPreviousMenu(RENAME_MAP.get(e.getPlayer()));
            openSplitMenuOnThread(e.getPlayer(), RENAME_MAP.get(e.getPlayer()));
            e.setCancelled(true);
        }
        RENAME_MAP.remove(e.getPlayer());
    }

    public void handleRemove(Player player){
        PrivateWaystone selected = (REMOVE_MAP.get(player)).getSelected();

        jdbc.remWs(selected);
        REMOVE_MAP.remove(player);
        RENAME_MAP.remove(player);//remove from here as a precaution. You can't edit something that doesn't exist
        COST_MAP.remove(player);//Can't update something that doesn't exist.
        player.sendMessage(Component.text("Waystone had been removed.", NamedTextColor.GREEN));
    }

    public void handleCost(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        PrivateWaystone selected = (COST_MAP.get(player)).getSelected();

        if(selected instanceof PublicWaystone){
//            System.out.println("Detected a change in price for a Public Waystone");
            PublicWaystone selectedPublicWs = (PublicWaystone) selected;

            if(isInt(e.getMessage())){
                selectedPublicWs.setCost(Integer.parseInt(e.getMessage()));
                player.sendMessage(Component.text("Cost set to: ", NamedTextColor.GRAY)
                        .append(Component.text(e.getMessage(), NamedTextColor.WHITE)));
            }else{
                player.sendMessage(Component.text("That was not a number that can be used. Try again", NamedTextColor.DARK_RED));
            }
            openSplitMenuOnThread(e.getPlayer(), COST_MAP.get(player));
            e.setCancelled(true);
        }
        else {
            System.out.println("this waystone is not a public Waystone");
        }

        jdbc.updateWaystone(selected);
        COST_MAP.remove(player);
    }

    public void handleTpa(Player player){
        //This method will be triggered when the player that teleported accepts to teleport with nearby players and then the nearby players will be asked if they want to tp
        //players in the list will get a message asking if to accept the tpa
        List<Player> playerList = TPA_LIST.get(player).getTpaList();

        for(Player p: playerList){
            // Add all nearby players to the list where they can accept and be removed within 15 seconds
            TPA_ACCEPT_LIST.put(p, TPA_LIST.get(player));

            //Send the players a message if they want to teleport
            p.sendMessage(Component.text("Do you want to teleport with ", NamedTextColor.YELLOW)
                    .append(Component.text(player.getName(), NamedTextColor.GOLD))
                    .append(Component.text(" [✔] ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Accept")))
                            .clickEvent(ClickEvent.runCommand("/ws confirmTPA")))
                    .append(Component.text(" [X]", NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Cancel")))
                            .clickEvent(ClickEvent.runCommand("/ws cancelTPA"))));
        }

        //After 15 seconds remove all players from this request from the TPA_Accept list
        startCountdown(TPA_ACCEPT_LIST, player, 300);

        player.sendMessage(Component.text("A request has been sent to all nearby players.", NamedTextColor.YELLOW));
        TPA_LIST.remove(player);
    }

    public void handleTpaAccept(Player player){
        PrivateWaystone ws = TPA_ACCEPT_LIST.get(player).getSelected();
        ws.safeTeleport(player, TPA_ACCEPT_LIST.get(player));
        TPA_ACCEPT_LIST.remove(player);
    }

    public void handleUnsafeTPAccept(Player player){
        PrivateWaystone ws = UNSAFE_TP_LIST.get(player).getSelected();
        PrivateWaystone clickedOnWaystone = UNSAFE_TP_LIST.get(player).getClickedOnWs();
        System.out.println("WS: "+ ws.getLocation());
        //if the player is close to the waystone in case they want to kill then quickly tp back by accepting
        if(player.getLocation().distance(clickedOnWaystone.getParsedLocation(clickedOnWaystone.getLocation())) < 5){
            ws.unsafeTeleport(player);
        }
        UNSAFE_TP_LIST.remove(player);
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

    private void openPreviousMenu(Menu menu){

        if(Bukkit.isPrimaryThread()){
//            System.out.println("PRIMARY THREAD!!");
            menu.open();
        }
        else{
//            System.out.println("NOT PRIMARY THREAD!!");
            //Running this synchronously & Opening back the edit menu
            //Places this thread on hold which is async, waits for the menu to open on the main thread, then continues back to this thread
            final CountDownLatch latch = new CountDownLatch(1);
            Bukkit.getScheduler().runTask(WaystonesPlugin.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    // Perform the synchronous operation

                    //Reopen the Edit menu
                    menu.open();

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

    private void openSplitMenuOnThread(Player player, PlayerMenuUtility playerMenuUtility){
        if(Bukkit.isPrimaryThread()){
            openSplitMenu(player, playerMenuUtility);
        }
        else{
//            System.out.println("NOT PRIMARY THREAD!!");
            //Running this synchronously & Opening back the edit menu
            //Places this thread on hold which is async, waits for the menu to open on the main thread, then continues back to this thread
            final CountDownLatch latch = new CountDownLatch(1);
            Bukkit.getScheduler().runTask(WaystonesPlugin.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    // Perform the synchronous operation

                    //Reopen a Split menu
                    openSplitMenu(player, playerMenuUtility);

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

    private void openSplitMenu(Player player, PlayerMenuUtility playerMenuUtility){
        Location clickedOnLocation = playerMenuUtility.getClickedOnWs().getParsedLocation(playerMenuUtility.getClickedOnWs().getLocation());
        System.out.println("player: "+player.getLocation()+ " selected: " + clickedOnLocation + " Distance: "+ player.getLocation().distance(clickedOnLocation));
        if(player.getLocation().distance(clickedOnLocation)<=5){
            new WaystonesSplitMenu(playerMenuUtility, 0).open();
        }
    }

    public void removePlayerFromRenameMap(Player player){
        openSplitMenuOnThread(player, RENAME_MAP.get(player));
        RENAME_MAP.remove(player);
    }

    public void removePlayerFromRemoveMap(Player player){
        openSplitMenuOnThread(player, REMOVE_MAP.get(player));
        REMOVE_MAP.remove(player);
    }

    public void removePlayerFromCostMap(Player player){
        openSplitMenuOnThread(player, COST_MAP.get(player));
        COST_MAP.remove(player);
    }

    public void removePlayerFromTpaList(Player player){
        TPA_LIST.remove(player);
    }

    public void removePlayerFromTpaAcceptList(Player player){
        TPA_ACCEPT_LIST.remove(player);
    }
    public void removePlayerFromUnsafeTPList(Player player){
        UNSAFE_TP_LIST.remove(player);
    }

    public HashMap<Player, PlayerMenuUtility> getRenameMap(){//Get the map when needed
        return RENAME_MAP;
    }

    public HashMap<Player, PlayerMenuUtility> getRemoveMap(){
        return REMOVE_MAP;
    }

    public HashMap<Player, PlayerMenuUtility> getCostMap(){
        return COST_MAP;
    }

    public HashMap<Player, PlayerMenuUtility> getTpaMap() {
        return TPA_LIST;
    }

    public HashMap<Player, PlayerMenuUtility> getTpaAcceptMap(){
        return TPA_ACCEPT_LIST;
    }

    public HashMap<Player, PlayerMenuUtility> getUnsafeTPMap(){
        return UNSAFE_TP_LIST;
    }
}
