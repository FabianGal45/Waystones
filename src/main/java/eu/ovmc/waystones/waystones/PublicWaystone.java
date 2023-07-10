package eu.ovmc.waystones.waystones;

public class PublicWaystone extends PrivateWaystone {

    private double rating;
    private int cost;
    private String category;

    public PublicWaystone(String location, String owner, String name, String tpLocation, int priority, String customItem, double rating, int cost, String category) {
        super(location, owner, name, tpLocation, priority, customItem);
        this.rating = rating;
        this.cost = cost;
        this.category = category;
    }

    public PublicWaystone(int id, String location, String owner, String name, String tpLocation, int priority, String customItem, double rating, int cost, String category) {
        super(id, location, owner, name, tpLocation, priority, customItem);
        this.rating = rating;
        this.cost = cost;
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

}
