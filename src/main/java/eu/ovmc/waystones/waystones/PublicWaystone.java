package eu.ovmc.waystones.waystones;

import org.jetbrains.annotations.NotNull;

public class PublicWaystone extends PrivateWaystone implements Comparable<PublicWaystone> {

    private double rating;
    private int rates;
    private int cost;
    private String category;

    public PublicWaystone(String location, int userId, String name, String tpLocation, int priority, String customItem, double rating, int rates, int cost, String category) {
        super(location, userId, name, tpLocation, priority, customItem);
        this.rating = rating;
        this.rates = rates;
        this.cost = cost;
        this.category = category;
    }

    public PublicWaystone(int id, String location, int userId, String name, String tpLocation, int priority, String customItem, double rating, int rates, int cost, String category) {
        super(id, location, userId, name, tpLocation, priority, customItem);
        this.rating = rating;
        this.rates = rates;
        this.cost = cost;
        this.category = category;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        System.out.println("rating set to: " +rating);
        this.rating = rating;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRates() {
        return rates;
    }

    public void setRates(int rates) {
        this.rates = rates;
    }

    @Override
    public int compareTo(@NotNull PublicWaystone o) {
        return Double.compare(o.rating, this.rating);
    }


}
