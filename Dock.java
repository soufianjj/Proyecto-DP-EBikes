import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Model a dock unit within a {@link BikeStation}.
 * It tracks its charging capabilities, fee, and the electric bikes it has recharged.
 * @author David J. Barnes and Michael Kölling
 * @author DP classes 
 * @version 2026.10.13
 */
public class Dock
{
    private static final String ID_PATTERN = "[A-Z]{2}\\d{2}_\\d{2,3}";
    private String id;
    private int chargingSpeed;
    private float chargingFee;
    private List<ElectricBike> bikesRecharged;
    private float amountCollected;
    private boolean free;

    /**
     * Constructor for objects of class Dock.
     * //TODO complete the JavaDoc comments
     */
    public Dock(String id, int speed, float fee)
    {
        if (id == null || !id.matches(ID_PATTERN)) {
            throw new IllegalArgumentException("Invalid dock id: " + id);
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Charging speed must be positive: " + speed);
        }
        if (fee < 0) {
            throw new IllegalArgumentException("Charging fee must not be negative: " + fee);
        }
        this.id = id;
        this.chargingSpeed = speed;
        this.chargingFee = fee;
        this.bikesRecharged = new ArrayList<>();
        this.amountCollected = 0;
        this.free = true;
    }

    /**
     * Returns a string representation of the dock, including its ID, speed, fee, and the number of EVs recharged.
     * @return A string representation of the dock.
     */
    public String toString()
    {
        return "(Dock: " + id + ", " + chargingSpeed + "wh, " + chargingFee + "€, "
        + getNumerEVRecharged() + ", " + amountCollected + "€)";
    }    

    /**
     * @return The total number of {@link ElectricBike}s that have been recharged by this dock.
     */
    public int getNumerEVRecharged(){
        return bikesRecharged.size();
    }

    /**
     * Simulates the charging process for an {@link ElectricBike}.
     * Increases the amount collected and registers the bike as recharged.
     * //TODO complete the JavaDoc comments
     */
    public float recharge(ElectricBike bike, int wh){
        if (bike == null) {
            throw new NullPointerException("bike must not be null");
        }
        if (wh <= 0) {
            throw new IllegalArgumentException("Energy must be positive: " + wh);
        }
        float cost = wh * chargingFee;
        addBike(bike);
        updateAmountCollected(cost);
        return cost;
    }

    /**
     * @return The unique identifier of the dock.
     */
    public String getId()
    {
        return id;
    }

    /**
     * @return The charging speed of the dock in wh.
     */
    public int getChargingSpeed()
    {
        return chargingSpeed;
    }

    /**
     * @return The charging fee of the dock in € per wh.
     */
    public float getChargingFee()
    {
        return chargingFee;
    }
    
    /**
     * @return The amount in € collected by this dock.
     */
    public float getAmountCollected()
    {
        return amountCollected;
    }

    /**
     * @return true if the dock is free, false if it is occupied.
     */
    public boolean isFree()
    {
        return free;
    }

    /**
     * @return An unmodifiable view of the bikes recharged here, in chronological order.
     */
    public List<ElectricBike> getBikesRecharged()
    {
        return Collections.unmodifiableList(bikesRecharged);
    }

    /**
     * Registers a bike as recharged by this dock.
     * @param bike The recharged bike. Must not be null.
     * @throws NullPointerException if bike is null.
     */
    public void addBike(ElectricBike bike)
    {
        if (bike == null) {
            throw new NullPointerException("bike must not be null");
        }
        bikesRecharged.add(bike);
    }

    /**
     * Adds an amount to the money collected by this dock.
     * @param amount The amount in € to add. Must not be negative.
     * @throws IllegalArgumentException if amount is negative.
     */
    public void updateAmountCollected(float amount)
    {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must not be negative: " + amount);
        }
        amountCollected += amount;
    }


}
