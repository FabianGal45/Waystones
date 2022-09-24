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

    private String uuid;
    private String userName;
    private int privateWs;
    private int publicWs;
    private int purchasedPrivateWs;

    public User(String uuid, String userName, int privateWs, int publicWs, int purchasedPrivateWs) {
        this.uuid = uuid;
        this.userName = userName;
        this.privateWs = privateWs;
        this.publicWs = publicWs;
        this.purchasedPrivateWs = purchasedPrivateWs;
    }

    public long getCostOfNextWs(){
        int boughtWaystones = purchasedPrivateWs;
        int priceMultiplier = WaystonesPlugin.getPlugin().getConfig().getInt("PriceMultiplier");

        int startPrice = WaystonesPlugin.getPlugin().getConfig().getInt("StartPrice");
        long total = 0;

        for(int i=0;i<=boughtWaystones; i++){
            if(i==0){
                total = startPrice;
            }else{
                total = total*priceMultiplier;
            }
        }
        return total;
    }

    public boolean canPlacePrivateWs(){
        boolean result = false;
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs");

        if(privateWs<freePrivateWs+purchasedPrivateWs){
            result = true;
        }

        return result;
    }

    public int getAllowedPrivWs(){
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs");

        //TODO: if user has permission to more private waystones the add the value

        return freePrivateWs+purchasedPrivateWs;
    }

    public int getAllowedPubWs(){
        return WaystonesPlugin.getPlugin().getConfig().getInt("FreePublicWs"); //TODO: + permission
    }

    public void addPurchase(int num){
        this.purchasedPrivateWs = purchasedPrivateWs + num;

        SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
        jdbc.updateUser(this);
    }

    public double getDiscount(PlayerMenuUtility playerMenuUtility) {
        double discount;
        int totalPoints = playerMenuUtility.getVotingPluginUser().getPoints();

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
        Player player = playerMenuUtility.getOwner();

        if(discount>0){
            cost = Math.round(cost * (1-discount));
        }

        Economy econ = WaystonesPlugin.getEcon();
        EconomyResponse r = econ.withdrawPlayer(player, cost);

        if(r.transactionSuccess()){
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.BLOCKS, 1, 2);
            purchasedPrivateWs++;

            SQLiteJDBC jdbc = WaystonesPlugin.getPlugin().getJdbc();
            jdbc.updateUser(this);

            playerMenuUtility.getVotingPluginUser().removePoints((int) (discount*100));

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

    public int getPurchasedPrivateWs() {
        return purchasedPrivateWs;
    }

    public void setPurchasedPrivateWs(int purchasedPrivateWs) {
        this.purchasedPrivateWs = purchasedPrivateWs;
    }
}
