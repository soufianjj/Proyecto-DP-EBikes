import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

/**
 * Model the common elements of an Electric bike (EB) that operates 
 * within the simulation, moving towards a target and potentially recharging.
 * * @author David J. Barnes and Michael Kölling
 * @author DP classes 
 * @version 2026.10.13
 */
public class ElectricBike 
{
    private static final int ID_LENGTH = 6;

    private final String idBike;
    private final EBCompany company;

    private Location location;
    private Location targetLocation;
    private Location rechargingLocation;

    private int idleCount;

    private final int batteryCapacity;
    private int batteryLevel;
    private int wsCharged;
    private int chargesCount;
    private double chargesCost;

    private final List<Dock> docks;

    /**
     * Constructor of class ElectricBike.
     * @param company The EB's operating company. Must not be null.
     * @param location The EB's starting {@link Location}. Must not be null.
     * @param targetLocation The EB's final destination {@link Location}. Must not be null.
     * @param id The identificator of the bike.
     * @param batteryCapacity The maximum capacity of the battery.
     * @throws NullPointerException If company, location, or targetLocation is null.
     * @throws IllegalArgumentException if the id does not have 6 characters.
     */
    public ElectricBike(EBCompany company, Location location, Location targetLocation, String id, int batteryCapacity)
    {
        if (company == null){
            throw new NullPointerException("company must not be null");
        }
        if (location == null){
            throw new NullPointerException("location must not be null");
        }
        if (targetLocation == null){
            throw new NullPointerException("targetLocation must not be null");
        }
        if (id == null || id.length() != ID_LENGTH){
            throw new IllegalArgumentException("The id must have " + ID_LENGTH + "characters: " + id);
        }
        this.company = company;
        this.location = location;
        this.targetLocation = targetLocation;
        this.idBike = id;
        this.batteryCapacity = batteryCapacity;
        this.batteryLevel = batteryCapacity;
        this.rechargingLocation = null;
        this.idleCount = 0;
        this.wsCharged = 0;
        this.chargesCount = 0;
        this.chargesCost = 0;
        this.docks = new ArrayList<>();
        calculateRoute();
    }

    /**
     * @return The id of the electric bike.
     */
    public String getIdBike()
    {
        return idBike;    
    }

    /**
     * Set the current location.
     * @param location Where it is. Must not be null.
     * @throws NullPointerException If location is null.
     */
    public void setLocation(Location location)
    {
        if(location == null){
            throw new NullPointerException("location must not be null");
        }
        this.location = location;
    }

    /**
     * Get the final target location.
     * @return Where this bike is ultimately headed.
     */
    public Location getTargetLocation()
    {
        return targetLocation;    
    }

    /**
     * Get the temporary recharging location.
     * @return The {@link Location} of the next {@link BikeStation} to visit, or null if no recharge is planned.
     */
    public Location getRechargingLocation()
    {
        return rechargingLocation;
    }

    /**
     * Set the required final target location.
     * @param location Where to go. Must not be null.
     * @throws NullPointerException If location is null.
     */
    public void setTargetLocation(Location location)
    {
        if(location == null){
            throw new NullPointerException("location must not be null");
        }
        this.targetLocation = location;
    }

    /**
     * Calculates the optimal route for the bike. 
     * If there isn't enough battery to reach the target, it attempts to find an intermediate 
     * {@link BikeStation} and sets it as the {@code rechargingLocation}.
     */
    public void calculateRoute()
    {
        rechargingLocation = null;
        if(!enoughBattery(distanceToTheTargetLocation())){
            calculateRechargingPosition();
        }
    }

    /**
     * Gets a string representation of the planned route, including the recharging stop if one exists.
     * @return A string showing the route: {@code currentLocation -> [rechargingLocation ->] targetLocation}.
     */
    public String getStringRoute()
    {
        StringBuilder route = new StringBuilder();
        route.append(location).append(" -> ");
        if(rechargingLocation != null){
            route.append(rechargingLocation).append(" -> ");
        }
        route.append(targetLocation);
        return route.toString();
    }

    /**
     * Checks if the current battery level is sufficient to cover a given distance.
     * @param distance the distance to cover.
     * @return true if the battery level is enough to cover the distance 
     * (each step costs {@link EBCompany#MOVINGCOST} wh), false otherwise.
     */
    public boolean enoughBattery(int distance)
    {
        return batteryLevel >= distance * EBCompany.MOVINGCOST;
    }

    /**
     * Determines the optimal intermediate {@link BikeStation} to visit for recharging
     * if the bike cannot reach the final target directly.
     * Sets {@code rechargingLocation} to the chosen station's location.
     * The chosen station is the reachable one (with the current battery, and
     * different from the current location) that minimizes the total distance
     * current location -> station -> target. If there is none, {@code rechargingLocation} is null.
     */
    public void calculateRechargingPosition()
    {
        Location best = null;
        int bestDistance = Integer.MAX_VALUE;
        for(BikeStation station : company.getBikeStation()){
            Location stationLocation = station.getLocation();
            if(stationLocation == null || stationLocation.equals(location)){
                continue;
            }
            int toStation = location.distance(stationLocation);
            if(enoughBattery(toStation)){
                int total = toStation + stationLocation.distance(targetLocation);
                if(total < bestDistance){
                    bestDistance = total;
                    best = stationLocation;
                }
            }
        }
        rechargingLocation = best;
    }

    /**
     * Get the Manhattan-like distance to the final target location from the current location.
     * @return The distance to the target location.
     */
    public int distanceToTheTargetLocation()
    {
        return location.distance(targetLocation);
    }

    /**
     * Simulates the recharging process when the bike arrives at a {@code rechargingLocation}.
     * The battery is charged to full capacity, the cost is calculated, and the route is recalculated.
     * The cost is the wh charged multiplied by the charging rate of the dock.
     * @param dock The {@link Dock} used to charge the bike. Must not be null.
     * @throws NullPointerException if dock is null.
     */
    public void recharge(Dock dock)
    {
        if(dock == null){
            throw new NullPointerException("dock must not be null");
        }
        int charged = batteryCapacity - batteryLevel;
        batteryLevel = batteryCapacity;
        wsCharged += charged;
        incrementCharges();
        incrementChargesCost(charged * dock.getChargingFee());
        if(!docks.contains(dock)){
            docks.add(dock); 
        }
        dock.addBike(this);
        calculateRoute();
    } 

    /**
     * Increments the count of recharges performed by this bike.
     */
    public void incrementCharges()
    {
        chargesCount ++;   
    }

    /**
     * Adds a cost amount to the total charges cost.
     * @param cost The cost of the recharge to add to the total.
     */
    public void incrementChargesCost(double cost)
    {
        chargesCost += cost;
    }   

    /**
     * Carries out a single step of the bike's actions.
     * Moves one step towards the target (recharging or final) or stays idle.
     * If the bike is at the recharging location, it recharges in a free dock
     * of that station (staying idle if there is none). If it is already at the 
     * final target, or it has no battery to move, it stays idle (idleCount is incremented).
     */
    public void act()
    {
        Location destination = (rechargingLocation != null) ? rechargingLocation : targetLocation;

        if(location.equals(destination) || (rechargingLocation == null && !enoughBattery(distanceToTheTargetLocation()))){
            idleCount++;          
            return;
        }

        setLocation(location.nextLocation(destination));
        reduceBatteryLevel();

        if(location.equals(destination)){
            BikeStation station = company.getBikeStation(rechargingLocation);
            if(station != null){
                try{
                    recharge(station.getFreeDock());
                } catch(NoSuchElementException e){

                }
            }
        }
    }

    /**
     * Reduces the battery level by the cost of one movement step (defined in {@link EBCompany#MOVINGCOST}).
     * Ensures the battery level does not go below zero.
     */
    public void reduceBatteryLevel(){
        batteryLevel = Math.max(0, batteryLevel - EBCompany.MOVINGCOST); 
    }

    /**
     * Returns a detailed string representation of the electric bike.
     * @return A string containing the bike's name, plate, battery info, charge counts, costs, idle count, and route.
     */
    @Override
    public String toString(){
        String route = location + ", " + (rechargingLocation 
        != null ? rechargingLocation + ", " : "") + targetLocation;
        return "ElectricBike: " + idBike + ", " + batteryCapacity + "wh, " 
        + batteryLevel + "wh, "+ chargesCount + ", " + chargesCost + "€, " 
        + idleCount + ", " + route;
    }

    /**
     * @return An unmodifiable view of the docks where the bike has charged.
     */
    public List<Dock> getDocks(){
        return Collections.unmodifiableList(docks);
    }

}
