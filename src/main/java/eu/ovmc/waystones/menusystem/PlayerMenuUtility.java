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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//The owner of the waystone/menu
public class PlayerMenuUtility {

    private Player player;
    private boolean isAdmin;
    private ArrayList<PrivateWaystone> privateWaystones;
    private ArrayList<PublicWaystone> publicWaystones;
    private PrivateWaystone clickedOnWs;//This is the physical waystone a player clicked on
    private User user;
    private VotingPluginUser votingPluginUser;
    private List<Player> tpaList;
    private PrivateWaystone selected;
    private Material tpCostMaterial;
    private Location nextTpLocation;

    public PlayerMenuUtility(OfflinePlayer offlinePlayer) {
        this.player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
        checkPlayerIsAdmin();
        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
        this.user = jdbc.getUserFromUuid(offlinePlayer.getUniqueId().toString());
        this.privateWaystones = jdbc.getAllPrivateWaystones(user.getId());
        if(WaystonesPlugin.isIsVotingPluginInstalled()){ //IF the plugin is installed then assign the VPU
            votingPluginUser = new VotingPluginUser(VotingPluginMain.getPlugin(), new AdvancedCoreUser(AdvancedCorePlugin.getInstance(), offlinePlayer.getUniqueId()));
        }
    }

    private void checkPlayerIsAdmin(){
        if(player != null){
            if(player.hasPermission("waystones.admin")){
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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


    public boolean isAdmin() {
        checkPlayerIsAdmin();
        return isAdmin;
    }

    public Material getTpCostMaterial() {
        return tpCostMaterial;
    }

    public void setTpCostMaterial(Material tpCostMaterial) {
        this.tpCostMaterial = tpCostMaterial;
    }

    public Location getNextTpLocation() {
        return nextTpLocation;
    }

    public void setNextTpLocation(Location nextTpLocation) {
        this.nextTpLocation = nextTpLocation;
    }
}
