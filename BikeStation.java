import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Models an Electric Vehicle Charging Station.
 * A charging station contains multiple {@link Dock} units.
 * @author DP classes 
 * @version 2026.10.13
 */
public class BikeStation
{
    private static final String ID_PATTERN = "[A-Z]{2}\\d{2}";
    private static final Comparator <Dock> DOCK_COMPARATOR = Comparator.comparingDouble(Dock::getChargingSpeed).reversed()
        .thenComparingDouble(Dock::getChargingFee)
        .thenComparing(Dock::getId);

    private String id;
    private Location location;
    private List<Dock> docks;

    /**
     * Constructor for objects of class BikeStation.
     * Creates a station with a default identifier ("AA00"), no location yet
     * and an empty collection of docks.
     */
    public BikeStation(  )
    {
        this.id = "AA00";
        this.location = null;
        this.docks = new ArrayList<>();
    }

    /**
     * Constructor for objects of class BikeStation.
     * @param id The unique identifier of the station: 2 uppercase letters
     * (city) followed by 2 digits, e. g. "BB01".
     * @param location The {@link Location} of the station. Must not be null.
     * @throws IllegalArgumentException if the id does not have a valid format.
     * @throws NullPointerException if location is null.
     */
    public BikeStation(String id, Location location)
    {
        if (id == null || !id.matches(ID_PATTERN)){
            throw new IllegalArgumentException("Invalid station id (expected 2 letters + 2 digits, e.g. BB01):" + id);
        }
        if (location == null){
            throw new NullPointerException("location must not be null");
        }
        this.id = id;
        this.location = location;
        this.docks = new ArrayList <>();
    }

    /**
     * Retrieves the first free {@link Dock} found at the station.
     * Since the docks are kept sorted, this is the free dock with 
     * the best charging speed (then lowest rate, the lowest id).
     * @return A free {@link Dock}, or possibly throws an exception if none are found (depending on stream implementation details).
     * @throws java.util.NoSuchElementException if the station has no free dock.
     * **Note: The implementation assumes at least one free dock exists if called.**
     * **ASSUMPTION: Dock provides isFree().
     */
    public Dock getFreeDock()
    { 
        return docks.stream().filter(Dock::isFree).findFirst().orElseThrow();        
    }

    /**
     * @return The unique identifier of the charging station.
     */
    public String getId()
    {  
        return id;
    }

    /**
     * @return The {@link Location} of the charging station.
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Adds a new 
     */
    public List<Dock> getDocks()
    {
        return Collections.unmodifiableList(docks);
    }

    /**
     * Adds a new {@link Dock} to the station.
     * @param dock The new Dock unit.
     * @throws NullPointerException if dosck is null.
     */
    public void addDock(Dock dock)
    {
        if (dock == null){
            throw new NullPointerException("dock must not be null");
        }
        docks.add(dock);
        docks.sort(DOCK_COMPARATOR);
    }

    /**
     * Set the current location of the charging station.
     * @param location Where it is. Must not be null.
     * @throws NullPointerException If location is null.
     */
    public void setLocation(Location location)
    {
        if (location == null){
            throw new NullPointerException("location must not be null");
        }
        this.location = location;
    }

    /**
     * @return A string with the id, location and docks of the station.
     */

    @Override
    public String toString()
    {
        return "BikeStation{id='" + id + "', location=" + location + ", docks=" + 
        docks.stream().map(Dock::toString).collect(Collectors.joining(", ", "[", "]")) + "}";
    }

    /**
     * @return The total number of recharges performed in the docks of this station.
     */
    public int getBikesRechargedCount()
    {
        int total = 0;
        for (Dock dock : docks) {
            total += dock.getNumerEVRecharged();
        }
        return total;
    }
}

