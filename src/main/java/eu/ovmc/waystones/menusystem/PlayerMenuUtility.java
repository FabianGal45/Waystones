package eu.ovmc.waystones.menusystem;

import com.bencodez.votingplugin.VotingPluginMain;
import com.bencodez.votingplugin.advancedcore.AdvancedCorePlugin;
import com.bencodez.votingplugin.advancedcore.api.user.AdvancedCoreUser;
import com.bencodez.votingplugin.user.VotingPluginUser;
import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.database.SQLiteJDBC;
import eu.ovmc.waystones.database.User;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import eu.ovmc.waystones.waystones.PublicWaystone;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//The owner of the waystone/menu
public class PlayerMenuUtility {

    private Player owner;
    private UUID ownerUUID;
    private boolean isAdmin;
    private ArrayList<PrivateWaystone> privateWaystones;
    private ArrayList<PublicWaystone> publicWaystones;
    private PrivateWaystone clickedOnWs;//This is the physical waystone a player clicked on
    private User user;
    private VotingPluginUser votingPluginUser;
    private List<Player> tpaList;
    private PrivateWaystone selected;

    public PlayerMenuUtility(OfflinePlayer offlineOwner) {
        this.owner = Bukkit.getPlayer(offlineOwner.getUniqueId());
        checkPlayerIsAdmin();
        this.ownerUUID = offlineOwner.getUniqueId();
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
        this.privateWaystones = jdbc.getAllPrivateWaystones(ownerUUID.toString());
        this.user = jdbc.getUserFromDB(ownerUUID.toString());
        votingPluginUser = new VotingPluginUser(VotingPluginMain.getPlugin(), new AdvancedCoreUser(AdvancedCorePlugin.getInstance(), offlineOwner.getUniqueId()));
    }

    private void checkPlayerIsAdmin(){
        if(owner != null){
            if(owner.hasPermission("waystones.admin")){
                isAdmin=true;
            }else{
                isAdmin=false;
            }
        }
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

    public ArrayList<PublicWaystone> getPublicWaystones() {
        return publicWaystones;
    }

    public void setPublicWaystones(ArrayList<PublicWaystone> publicWaystones) {
        this.publicWaystones = publicWaystones;
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

    public List<Player> getTpaList() {
        return tpaList;
    }

    public void setTpaList(List<Player> tpaList) {
        this.tpaList = tpaList;
    }

    public PrivateWaystone getSelected() {
        return selected;
    }

    public void setSelected(PrivateWaystone selected) {
        this.selected = selected;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
