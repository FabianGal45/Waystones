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
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

//The owner of the waystone/menu
public class PlayerMenuUtility {

    private Player player;
    private boolean isAdmin;
    private ArrayList<PrivateWaystone> privateWaystones;
    private ArrayList<PublicWaystone> publicWaystones = new ArrayList<>();
    private Queue<PublicWaystone> pubWsPriorityQ = new PriorityQueue<>();
    private PrivateWaystone clickedOnWs;//This is the physical waystone a player clicked on
    private User user;
    private VotingPluginUser votingPluginUser;
    private List<Player> tpaList;
    private PrivateWaystone selected;
    private Material tpCostMaterial;
    private Location nextTpLocation;
    private SQLiteJDBC JDBC;

    public PlayerMenuUtility(OfflinePlayer offlinePlayer) {
        this.JDBC = WaystonesPlugin.getPlugin().getJdbc();
        this.player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
        this.user = JDBC.getUserFromUuid(offlinePlayer.getUniqueId().toString());
        checkPlayerIsAdmin();
        updatePrivateWaystones();
        updatePublicWaystones();
        checkVPU(offlinePlayer);
    }

    private void checkVPU(OfflinePlayer offlinePlayer){
        if(WaystonesPlugin.isIsVotingPluginInstalled()){ //IF the plugin is installed then assign the VPU
            votingPluginUser = new VotingPluginUser(VotingPluginMain.getPlugin(), new AdvancedCoreUser(AdvancedCorePlugin.getInstance(), offlinePlayer.getUniqueId()));
        }
    }
    public void updatePrivateWaystones(){
        this.privateWaystones = JDBC.getAllPrivateWaystones(user.getId());
    }
    public void updatePublicWaystones(){
        this.pubWsPriorityQ.clear();
        this.publicWaystones.clear();
        this.pubWsPriorityQ.addAll(JDBC.getAllPublicWaystones());
        while(!pubWsPriorityQ.isEmpty()){
            publicWaystones.add(pubWsPriorityQ.poll());
        }
    }

    public void test(){
        while(!pubWsPriorityQ.isEmpty()){
            PublicWaystone pubws = pubWsPriorityQ.poll();
            System.out.println("Value: "+ pubws.getRating() + " Name: "+ pubws.getName());
        }

        System.out.println("nnnnnn ////as/d/a/sd/a/s/da///// /// // // // ");

        for(PublicWaystone pubws:publicWaystones){
            System.out.println("Value: "+ pubws.getRating() + " Name: "+ pubws.getName());
        }

        updatePublicWaystones();
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

    public Queue<PublicWaystone> getPubWsPriorityQ() {
        return pubWsPriorityQ;
    }

    public void setPubWsPriorityQ(Queue<PublicWaystone> pubWsPriorityQ) {
        this.pubWsPriorityQ = pubWsPriorityQ;
    }
}
