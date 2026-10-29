import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Models an Electric Vehicle Charging Station.
 * A charging station contains multiple {@link Dock} units.
 * @author DP classes 
 * @version 2026.10.13
 */
public class BikeStation
{
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
     * //TODO complete the JavaDoc comments
     */
    public BikeStation(String id, Location location)
    {
        //TODO: Complete this code
    }
    
    /**
     * Retrieves the first free {@link Dock} found at the station.
     * @return A free {@link Dock}, or possibly throws an exception if none are found (depending on stream implementation details).
     * **Note: The implementation assumes at least one free dock exists if called.**
     */
    public Dock getFreeDock()
    {
       //TODO: Complete this code   
        return null;        
    }

    /**
     * @return The unique identifier of the charging station.
     */
    public String getId()
    {
        //TODO: Complete this code   
        return null;
    }
    
    
    /**
     * Adds a new {@link Dock} to the station.
     * @param dock The new Dock unit.
     */
    public void addDock(Dock dock)
    {
       //TODO: Complete this code
    }
    
    /**
     * Set the current location of the charging station.
     * @param location Where it is. Must not be null.
     * @throws NullPointerException If location is null.
     */
    public void setLocation(Location location)
    {
       //TODO: Complete this code   
    }

    //TODO complete the rest of the code

}
