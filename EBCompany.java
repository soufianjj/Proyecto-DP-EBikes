import java.util.*;

/**
 * Models the operation of an Electric Bike (EB) Company. 
 * It manages a fleet of {@link ElectricBike}s and a network of {@link BikeStation}s.
 * * @author David J. Barnes and Michael Kölling
 * @author DP classes
 * @version 2026.10.13
 */
public class EBCompany  
{
    public static final int MOVINGCOST = 5;
    
    private final String name;
    private final Set <ElectricBike> bikes;
    private final List <BikeStation> stations;
    /**
     * Constructor for objects of class EVCompany.
     * @param name The name of the company.
     */
    public EBCompany(String name)
    {
        this.name = name;
        this.bikes = new HashSet<>();
        this.stations = new ArrayList<>();
    }
    
    /**
     * @return the name of the company.
     */
    public String getName(){
        return name;
    }

    /**
     * Adds an {@link ElectricBike} to the company's fleet.
     * @param bike The electric bike to add.
     * @throws NullPointerException if bike is null.
     */
    public void addElectricBike(ElectricBike bike)
    {       
        if(bike == null){
            throw new NullPointerException("bike must not be null");
        }
        bikes.add(bike);
    }

    
    /**
     * Adds a {@link BikeStation} to the company's network.
     * If a station with the same id already exists, it is not added again.
     * @param station The bike station to add.
     * @throws NullPointerException if station is null.
     */
    public void addBikeStation(BikeStation station)
    {       
        if(station == null){
            throw new NullPointerException("station must not be null");
        }
        if(getBikeStation(station.getId()) == null){
            stations.add(station);
        }
    }
    
    
    /**
     * Retrieves a {@link BikeStation} by its unique ID.
     * @param id The ID of the station to find.
     * @return The {@link BikeStation} with the matching ID, or {@code null} if not found.
     */
    public BikeStation getBikeStation(String id)
    {
        for(BikeStation station : stations){
            if(station.getId().equals(id)){
                return station;
            }
        }
        return null;
    }

    /**
     * Retrieves a {@link BikeStation} by its {@link Location}.
     * @param location The {@link Location} of the station to find.
     * @return The {@link BikeStation} at the matching location, or {@code null} if not found.
     */
    public BikeStation getBikeStation(Location location)
    {
        for(BikeStation station : stations){
            if(station.getLocation() != null && station.getLocation().equals(location)){
                return station;
            }
        }
        return null;
    }
    
    /**
     * @return An unmodifiable view of the bike stations owned by the company.
     */
    public List<BikeStation> getBikeStation()
    {
        return Collections.unmodifiableList(stations);
    }
 
    /**
    @return An unmodifiable view of the electric bikes of the company's fleet.
    */
    public Set<ElectricBike> getElectricBikes()
    {
        return Collections.unmodifiableSet(bikes);
    }
    /**
     * @return The total number of managed {@link BikeStation}s.
     */
    public int getNumberOfStations(){
        return stations.size();
    }
    
    /**
     * Clears all managed bikes and stations, resetting the company to an empty state.
     */
    public void reset(){
        bikes.clear();
        stations.clear();
    }
    
}