package eu.ovmc.waystones.waystones;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.handlers.TeleportHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import org.bukkit.entity.Player;

public class PrivateWaystone extends TeleportHandler{
    private int id;
    private String location;
    private int userId;
    private String name;
    private String tpLocation;
    private int priority;
    private String customItem;

    //Constructors

    public PrivateWaystone(String location, int userId, String name, String tpLocation, int priority, String customItem) {
        this.location = location;
        this.userId = userId;
        this.name = name;
        this.tpLocation = tpLocation;
        this.priority = priority;
        this.customItem = customItem;
    }

    public PrivateWaystone(int id, String location, int userId, String name, String tpLocation, int priority, String customItem) {
        this.id = id;
        this.location = location;
        this.userId = userId;
        this.name = name;
        this.tpLocation = tpLocation;
        this.priority = priority;
        this.customItem = customItem;
    }

    //These polymorphisms methods are used to only specify the minimum amount of information necessary and from here the extra
    // information, like tpLocation, related to the waystone will be attached to the method located in the superclass TeleportHandler
    public void safeTeleportToWs(Player player, PlayerMenuUtility playerMenuUtility){
        safeTeleport(player, playerMenuUtility, getParsedLocation(tpLocation));
    }

    //Getters and setters

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public String getCustomItem() {
        return customItem;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setCustomItem(String customItem) {
        this.customItem = customItem;
    }

    public String getLocation() {
        return location;
    }

    public String getTpLocation() {
        return tpLocation;
    }

    public void setTpLocation(String tpLocation) {
        this.tpLocation = tpLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUserId() {
        return userId;
    }

    public User getUser(){
        return WaystonesPlugin.getPlugin().getJdbc().getUser(userId);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
