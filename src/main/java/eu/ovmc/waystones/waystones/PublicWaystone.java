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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int compareTo(@NotNull PublicWaystone o) {

        // if both this && other category == STAFF ||  if both this && other category == EVENT, sort them based on rating
        if(this.category != null && o.category != null){//if both this and other have a category
            if(this.category.equals(o.category)){ // if STAFF and STAFF || EVENT and EVENT || ..
                return Double.compare(o.rating, rating);//arrange based on rating
            }
        }

        // if this is STAFF, this goes to front
        if(this.category != null && this.category.equals("STAFF")){ //if STAFF
            return -1; // this goes to front
        }

        // if other is STAFF, this goes to back
        if(o.category != null && o.category.equals("STAFF")){ // if Other == STAFF
            return 1; // this goes to the back
        }


        // if this is EVENT, this goes to front
        if(this.category != null && this.category.equals("EVENT")){ //if EVENT
            return -1; // this goes to front
        }

        // if other is EVENT, this goes to back
        if(o.category != null && o.category.equals("EVENT")){ // if Other == EVENT
            return 1; // this goes to the back
        }

        //anything else arrange based on rating
        return Double.compare(o.rating, rating);
    }


}
