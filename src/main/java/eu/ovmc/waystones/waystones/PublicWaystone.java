package eu.ovmc.waystones.waystones;

public class PublicWaystone extends PrivateWaystone {

    private double rating;
    private int cost;
//    private String category;

    public PublicWaystone(String location, String owner, String name, String tpLocation) {
        super(location, owner, name, tpLocation);
        rating = 0.0;
        cost = 0;
//        category = null;
    }

    public PublicWaystone(String location, String owner, String name, String tpLocation, double rating, int cost) {
        super(location, owner, name, tpLocation);
        this.rating = rating;
        this.cost = cost;
//        this.category = category;
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
