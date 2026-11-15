import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class EBCompanyTest.
 * Tests the management of bikes and stations of an {@link EBCompany}.
 *
 * @author  (your name)
 * @version 2026.11.15
 */
public class EBCompanyTest
{
    private EBCompany company;
    private BikeStation station0;
    private BikeStation station1;
    private ElectricBike bike0;
    private ElectricBike bike1;

    /**
     * Default constructor for test class EBCompanyTest
     */
    public EBCompanyTest()
    {
    }

    /**
     * Sets up the test fixture: a company, two stations and two bikes.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        company = new EBCompany("EBBike Test");
        station0 = new BikeStation("CC00", new Location(10, 5));
        station1 = new BikeStation("CC01", new Location(10, 11));
        bike0 = new ElectricBike(company, new Location(1, 1), new Location(2, 2), "EBCC0", 30);
        bike1 = new ElectricBike(company, new Location(3, 3), new Location(4, 4), "EBCC1", 30);
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        company = null;
        station0 = null;
        station1 = null;
        bike0 = null;
        bike1 = null;
    }

    /**
     * Test the initial state of a company.
     */
    @Test
    public void testCreation()
    {
        assertEquals("EBBike Test", company.getName());
        assertTrue(company.getElectricBikes().isEmpty());
        assertTrue(company.getBikeStation().isEmpty());
        assertEquals(0, company.getNumberOfStations());
    }

    /**
     * Test adding bikes to the fleet.
     */
    @Test
    public void testAddElectricBike()
    {
        company.addElectricBike(bike0);
        company.addElectricBike(bike1);

        assertEquals(2, company.getElectricBikes().size());
        assertTrue(company.getElectricBikes().contains(bike0));
        assertTrue(company.getElectricBikes().contains(bike1));
    }

    /**
     * Test that the same bike is not stored twice and that null is rejected.
     */
    @Test
    public void testAddElectricBikeInvalid()
    {
        company.addElectricBike(bike0);
        company.addElectricBike(bike0);

        assertEquals(1, company.getElectricBikes().size());
        assertThrows(NullPointerException.class, () -> company.addElectricBike(null));
    }

    /**
     * Test adding stations to the network.
     */
    @Test
    public void testAddBikeStation()
    {
        company.addBikeStation(station0);
        company.addBikeStation(station1);

        assertEquals(2, company.getNumberOfStations());
        assertSame(station0, company.getBikeStation().get(0));
        assertSame(station1, company.getBikeStation().get(1));
    }

    /**
     * Test that a station with an existing id is not added again
     * and that null is rejected.
     */
    @Test
    public void testAddBikeStationInvalid()
    {
        company.addBikeStation(station0);
        company.addBikeStation(new BikeStation("CC00", new Location(0, 0)));

        assertEquals(1, company.getNumberOfStations());
        assertSame(station0, company.getBikeStation("CC00"));
        assertThrows(NullPointerException.class, () -> company.addBikeStation(null));
    }

    /**
     * Test the search of a station by id.
     */
    @Test
    public void testGetBikeStationById()
    {
        company.addBikeStation(station0);
        company.addBikeStation(station1);

        assertSame(station1, company.getBikeStation("CC01"));
        assertNull(company.getBikeStation("CC99"));
        assertNull(company.getBikeStation((String) null));
    }

    /**
     * Test the search of a station by location.
     */
    @Test
    public void testGetBikeStationByLocation()
    {
        company.addBikeStation(station0);
        company.addBikeStation(station1);

        assertSame(station0, company.getBikeStation(new Location(10, 5)));
        assertSame(station1, company.getBikeStation(new Location(10, 11)));
        assertNull(company.getBikeStation(new Location(0, 0)));
        assertNull(company.getBikeStation((Location) null));
    }

    /**
     * Test that the collections returned cannot be modified from outside.
     */
    @Test
    public void testCollectionsAreUnmodifiable()
    {
        company.addElectricBike(bike0);
        company.addBikeStation(station0);

        assertThrows(UnsupportedOperationException.class,
                     () -> company.getBikeStation().clear());
        assertThrows(UnsupportedOperationException.class,
                     () -> company.getElectricBikes().clear());
        assertEquals(1, company.getNumberOfStations());
        assertEquals(1, company.getElectricBikes().size());
    }


}