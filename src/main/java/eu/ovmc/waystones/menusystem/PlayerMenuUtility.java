package eu.ovmc.waystones.menusystem;

import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.advancedcore.AdvancedCorePlugin;
import com.bencodez.votingplugin.advancedcore.api.user.AdvancedCoreUser;
import com.bencodez.votingplugin.user.VotingPluginUser;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerMenuUtility {

    private Player owner;
    private ArrayList<PrivateWaystone> privateWaystones;
    private PrivateWaystone clickedOnWs;
    private User user;
    VotingPluginUser votingPluginUser;

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
        SQLiteJDBC jdbc = new SQLiteJDBC();
        this.privateWaystones = jdbc.getAllPrivateWaystones(owner.getUniqueId().toString());
        this.user = jdbc.getUserFromDB(owner.getUniqueId().toString());
        votingPluginUser = new VotingPluginUser(VotingPluginMain.getPlugin(), new AdvancedCoreUser(AdvancedCorePlugin.getInstance(), owner));
    }

    public PrivateWaystone getClickedOnWs() {
        return clickedOnWs;
    }

    public void setClickedOnWs(PrivateWaystone clickedOnWs) {
        this.clickedOnWs = clickedOnWs;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VotingPluginUser getVotingPluginUser() {
        return votingPluginUser;
    }
}
