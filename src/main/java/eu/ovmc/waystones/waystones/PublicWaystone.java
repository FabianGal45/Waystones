package eu.ovmc.waystones.waystones;

public class PublicWaystone extends PrivateWaystone {

    private double rating;
    private double cost;
    private String category;

    public PublicWaystone(String location, String owner, String name) {
        super(location, owner, name);
        rating = 0.0;
        cost = 0.0;
        category = null;
    }

    public PublicWaystone(String location, String owner, String name, double rating, double cost, String category) {
        super(location, owner, name);
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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
