package eu.ovmc.waystones.database;

import eu.ovmc.waystones.WaystonesPlugin;
import eu.ovmc.waystones.menusystem.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class User {

    private int id;
    private String uuid;
    private String userName;
    private int privateWs;
    private int publicWs;
    private int purchasedPrivateWs;
    private int acquiredPrivateWs;
    private int acquiredPublicWs;

    public User(int id, String uuid, String userName, int privateWs, int publicWs, int purchasedPrivateWs, int acquiredPrivateWs, int acquiredPublicWs) {
        this.id = id;
        this.uuid = uuid;
        this.userName = userName;
        this.privateWs = privateWs;
        this.publicWs = publicWs;
        this.purchasedPrivateWs = purchasedPrivateWs;
        this.acquiredPrivateWs = acquiredPrivateWs;
        this.acquiredPublicWs = acquiredPublicWs;
    }

    public long getCostOfNextWs(){
        int priceMultiplier = WaystonesPlugin.getPlugin().getConfig().getInt("PriceMultiplier");
        int startPrice = WaystonesPlugin.getPlugin().getConfig().getInt("StartPrice");
        int maxPrice = WaystonesPlugin.getPlugin().getConfig().getInt("MaxPrice");
        long total = 0;

        for(int i=0;i<=purchasedPrivateWs; i++){
            if(i==0){
                total = startPrice;
            }else if(total>=maxPrice){
                break;
            }else{
                total = total*priceMultiplier;
            }
        }
        return total;
    }

    public boolean canPlacePrivateWs(){
        boolean result = false;
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs");

        if(privateWs<freePrivateWs+acquiredPrivateWs+purchasedPrivateWs){
            result = true;
        }

        return result;
    }

    public boolean canPlacePublicWs(){
        boolean canPlace = false;
        int freePublicWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePublicWs");

        if(publicWs<freePublicWs+acquiredPublicWs){
            canPlace = true;
        }

        return canPlace;
    }


    public int getId() {
        return id;
    }

    public int getAllowedPrivWs(){
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs"); //from confing.yml

        return freePrivateWs+acquiredPrivateWs+purchasedPrivateWs;
    }

    public int getAllowedPubWs(){
        return WaystonesPlugin.getPlugin().getConfig().getInt("FreePublicWs");
    }

    public void addPurchase(int num){
        this.purchasedPrivateWs = purchasedPrivateWs + num;

        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
        jdbc.updateUser(this);
    }

    public double getDiscount(PlayerMenuUtility playerMenuUtility) {
        double discount;
        int totalPoints;

        //Checks to see if the Voting plugin is not installed.
        if (!WaystonesPlugin.isIsVotingPluginInstalled()) {
            totalPoints = 0;
        }
        else{
            totalPoints = playerMenuUtility.getVotingPluginUser().getPoints();
        }

        int maxDiscount = WaystonesPlugin.getPlugin().getConfig().getInt("MaxDiscount");
        if(totalPoints>0){
            if(totalPoints>maxDiscount){
                discount = (double) maxDiscount/100;
            }
            else{
                discount = (double) totalPoints/100;
            }
        }
        else{
            discount = 0.0;
        }

        return discount;
    }


    public boolean purchaseWaystone(PlayerMenuUtility playerMenuUtility){
        boolean success;
        long cost = getCostOfNextWs();
        double discount = getDiscount(playerMenuUtility);
        Player player = playerMenuUtility.getPlayer();

        if(discount>0){
            cost = Math.round(cost * (1-discount));
        }

        Economy econ = WaystonesPlugin.getEcon();
        EconomyResponse r = econ.withdrawPlayer(player, cost);

        if(r.transactionSuccess()){
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 2);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, (float) 0.5, (float) 1.6);
            purchasedPrivateWs++;

            SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
            jdbc.updateUser(this);

            if(WaystonesPlugin.isIsVotingPluginInstalled()){
                playerMenuUtility.getVotingPluginUser().removePoints((int) (discount*100));
            }

            player.sendMessage(Component.text("You purchased a waystone for ", NamedTextColor.GREEN)
                    .append(Component.text( econ.format(cost), NamedTextColor.GREEN)));

            success = true;
        }
        else{
            player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1, (float) 0.1);
            player.sendMessage(Component.text("You don't have ", NamedTextColor.DARK_RED)
                    .append(Component.text( econ.format(cost), NamedTextColor.RED)));
            success = false;
        }

        return success;
    }

    public int getAcquiredPublicWs() {
        return acquiredPublicWs;
    }

    public void setAcquiredPublicWs(int acquiredPublicWs) {
        this.acquiredPublicWs = acquiredPublicWs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPrivateWs() {
        return privateWs;
    }

    public void setPrivateWs(int privateWs) {
        this.privateWs = privateWs;
    }

    public int getPublicWs() {
        return publicWs;
    }

    public void setPublicWs(int publicWs) {
        this.publicWs = publicWs;
    }

    public int getAcquiredPrivateWs() {
        return acquiredPrivateWs;
    }

    public void setAcquiredPrivateWs(int acquiredPrivateWs) {
        this.acquiredPrivateWs = acquiredPrivateWs;
    }

    public int getPurchasedPrivateWs() {
        return purchasedPrivateWs;
    }

    public void setPurchasedPrivateWs(int purchasedPrivateWs) {
        this.purchasedPrivateWs = purchasedPrivateWs;
    }
}
