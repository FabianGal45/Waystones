package eu.ovmc.waystones.waystones;

import eu.ovmc.waystones.events.TeleportHandler;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import org.bukkit.entity.Player;

public class PrivateWaystone extends TeleportHandler{
    private String location;
    private String owner;
    private String name;
    private String tpLocation;

    //Constructors
    public PrivateWaystone() {
        location = null;
        owner = null;
        name = null;
        tpLocation = null;
    }

    public PrivateWaystone(String location, String owner, String name, String tpLocation) {
        this.location = location;
        this.owner = owner;
        this.name = name;
        this.tpLocation = tpLocation;
    }

    //These polymorphisms methods are used to only specify the minimum amount of information necessary and from here the extra
    // information, like tpLocation, related to the waystone will be attached to the method located in the superclass TeleportHandler
    public void safeTeleportToWs(Player player, PlayerMenuUtility playerMenuUtility){
        safeTeleport(player, playerMenuUtility, getParsedLocation(tpLocation));
    }

    //Getters and setters
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
