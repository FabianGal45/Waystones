package eu.ovmc.waystones;

import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import eu.ovmc.waystones.menusystem.SplitMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.events.MenuHandler;
import eu.ovmc.waystones.events.WaystoneBreak;
import eu.ovmc.waystones.events.WaystoneInteract;
import eu.ovmc.waystones.events.WaystonePlace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public final class WaystonesPlugin extends JavaPlugin implements Listener {
    private static WaystonesPlugin plugin;
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

/*
    ArrayList<PrivateWaystone> privateWaystones;
    ArrayList<SplitMenu> arrGUIs;
*/


    @Override
    public void onEnable() {
        //Configuration File
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        plugin = this;

        //SQLiteJDBC - Connect and create the tables.
        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.createTables();

        //Events
        getServer().getPluginManager().registerEvents(new MenuHandler(), this);
        getServer().getPluginManager().registerEvents(new WaystonePlace(), this);
        getServer().getPluginManager().registerEvents(new WaystoneInteract(), this);
        getServer().getPluginManager().registerEvents(new WaystoneBreak(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }


    public static PlayerMenuUtility getPlayerMenuUtility(Player player){
        PlayerMenuUtility playerMenuUtility;

        //if player is already registered in the map then return it's playerMenuUtility var.
        if(playerMenuUtilityMap.containsKey(player)){
            return playerMenuUtilityMap.get(player);
        }
        else {//Register the player and return it's newly generated playerMenuUtility var.
            playerMenuUtility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player,playerMenuUtility);

            return playerMenuUtility;
        }
    }

    public static WaystonesPlugin getPlugin(){
        return plugin;
    }



/*////////
    public void openGUI(Player player){
        //Select the waystones that get in the splitMenu object
        ArrayList<PrivateWaystone> selection = new ArrayList<>();
        SplitMenu sm;
        arrGUIs = new ArrayList<>();

        if(privateWaystones.size() >= 7){
            sm = new SplitMenu();
            arrGUIs.add(sm);

            for(int i =0; i<7; i++){
                selection.add(privateWaystones.get(0));
                privateWaystones.remove(0);
            }
            sm.setPrivateWaystones(selection);
            sm.openMainMenu(player);
        }else if(privateWaystones.size()<7){
            for(int i =0; i<privateWaystones.size(); i++){
                selection.add(privateWaystones.get(0));
                privateWaystones.remove(0);
            }
            sm = new SplitMenu();
            arrGUIs.add(sm);

            sm.setPrivateWaystones(selection);
            sm.openMainMenu(player);
        }



        //Debug
        System.out.println("ArrGUIs: "+arrGUIs);
    }

    public SplitMenu getLastOppenedMenu(){
        return arrGUIs.get(arrGUIs.size()-1);
    }

    public void setPrivateWaystones(ArrayList<PrivateWaystone> privateWaystones) {
        this.privateWaystones = privateWaystones;
    }

    public ArrayList<PrivateWaystone> getPrivateWaystones() {
        return privateWaystones;
    }

    public ArrayList<SplitMenu> getArrGUIs() {
        return arrGUIs;
    }
//////////*/

    @Override
    public void onDisable() {}
}
