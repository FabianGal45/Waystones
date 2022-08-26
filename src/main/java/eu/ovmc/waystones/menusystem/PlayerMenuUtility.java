package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerMenuUtility {

    private Player owner;
    private ArrayList<PrivateWaystone> privateWaystones;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
        SQLiteJDBC jdbc = new SQLiteJDBC();
        this.privateWaystones = jdbc.getAllPrivateWaystones(owner.getUniqueId().toString());
    }

    public ArrayList<PrivateWaystone> getPrivateWaystones() {
        return privateWaystones;
    }

    public void setPrivateWaystones(ArrayList<PrivateWaystone> privateWaystones) {
        this.privateWaystones = privateWaystones;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
