package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class Node
{
    private LngLat currentLngLat;
    private double previousCost;
    private double totalCost;
    private Node parent;
    private double angle;

    public Node(LngLat currentLngLat, double previousCost, double totalCost,Node parent, double angle)
    {
        this.currentLngLat = currentLngLat;
        this.previousCost = previousCost;
        this.totalCost = totalCost;
        this.parent = parent;
        this.angle = angle;
    }

    // Getter for currentLngLat
    public LngLat getCurrentLngLat() {
        return currentLngLat;
    }

    // Setter for currentLngLat
    public void setCurrentLngLat(LngLat currentLngLat) {
        this.currentLngLat = currentLngLat;
    }

    // Getter for previousCost
    public double getPreviousCost() {
        return previousCost;
    }

    // Setter for previousCost
    public void setPreviousCost(double previousCost) {
        this.previousCost = previousCost;
    }

    // Getter for totalCost
    public double getTotalCost() {
        return totalCost;
    }

    // Setter for totalCost
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    // Getter for parent
    public Node getParent() {
        return parent;
    }

    // Setter for parent
    public void setParent(Node parent) {
        this.parent = parent;
    }

    // Getter for angle
    public double getAngle() {
        return angle;
    }

    // Setter for angle
    public void setAngle(double angle) {
        this.angle = angle;
    }
}
