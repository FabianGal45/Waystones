package eu.ovmc.waystones.database;

import eu.ovmc.waystones.WaystonesPlugin;

public class User {

    private String uuid;
    private String userName;
    private int privateWs;
    private int publicWs;

    private int purchasedPrivateWs;



    public User() {
        this.uuid = null;
        this.userName = "";
        this.privateWs = 0;
        this.publicWs = 0;
    }

    public User(String uuid, String userName, int privateWs, int publicWs, int purchasedPrivateWs) {
        this.uuid = uuid;
        this.userName = userName;
        this.privateWs = privateWs;
        this.publicWs = publicWs;
        this.purchasedPrivateWs = purchasedPrivateWs;
    }

    public void test(){
        int boughtWaystones = 1;
        int priceMultiplier = 5;

        int startPrice = 32;
        int total = 0;

        for(int i=0;i<=boughtWaystones; i++){
            if(i==0){
                total = startPrice;
            }else{
                total = total*priceMultiplier;
            }
        }
        System.out.println(total);

    }

    public boolean canPlacePrivateWs(){
        boolean result = false;
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs");

        System.out.println("math: "+ privateWs + "<" + freePrivateWs + "+" +  purchasedPrivateWs);
        if(privateWs<freePrivateWs+purchasedPrivateWs){
            result = true;
        }

        return result;
    }

    public int getAllowedWs(){
        int freePrivateWs = WaystonesPlugin.getPlugin().getConfig().getInt("FreePrivateWs");
        return freePrivateWs+purchasedPrivateWs;
    }

    public void addPurchase(int num){
        this.purchasedPrivateWs = purchasedPrivateWs + num;

        SQLiteJDBC jdbc = new SQLiteJDBC();
        jdbc.updateUser(this);
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
