import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

/**
 * The test class BikeStationTest.
 * Tests the creation of stations and the management of their docks.
 *
 * @author  (your name)
 * @version 2026.11.15
 */
public class BikeStationTest
{
    private BikeStation station;
    private Dock slow;
    private Dock mid;
    private Dock fast;

    /**
     * Default constructor for test class BikeStationTest
     */
    public BikeStationTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        station = new BikeStation("CC00", new Location(10, 5));
        slow = new Dock("CC00_000", 20, 0.2f);
        mid = new Dock("CC00_001", 40, 0.4f);
        fast = new Dock("CC00_003", 80, 0.8f);
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        station = null;
        slow = null;
        mid = null;
        fast = null;
    }

    /**
     * Test the creation of a station.
     */
    @Test
    public void testCreation()
    {
        assertEquals("CC00", station.getId());
        assertEquals(new Location(10, 5), station.getLocation());
        assertTrue(station.getDocks().isEmpty());
        assertEquals(0, station.getBikesRechargedCount());
    }

    /**
     * Test that invalid ids and locations are rejected.
     */
    @Test
    public void testInvalidCreation()
    {
        Location loc = new Location(0, 0);
        assertThrows(IllegalArgumentException.class, () -> new BikeStation("cc00", loc));
        assertThrows(IllegalArgumentException.class, () -> new BikeStation("CC1", loc));
        assertThrows(IllegalArgumentException.class, () -> new BikeStation("CC001", loc));
        assertThrows(IllegalArgumentException.class, () -> new BikeStation(null, loc));
        assertThrows(NullPointerException.class, () -> new BikeStation("CC00", null));
    }

    /**
     * Test that docks are added and kept sorted by speed (descending).
     */
    @Test
    public void testAddDock()
    {
        station.addDock(slow);
        station.addDock(fast);
        station.addDock(mid);

        assertEquals(3, station.getDocks().size());
        assertSame(fast, station.getDocks().get(0));
        assertSame(mid, station.getDocks().get(1));
        assertSame(slow, station.getDocks().get(2));
    }

    /**
     * Test the tie-break by fee (ascending) when speeds are equal.
     */
    @Test
    public void testAddDockSameSpeedOrdersByFee()
    {
        Dock cheap = new Dock("CC00_002", 80, 0.5f);
        station.addDock(fast);
        station.addDock(cheap);

        assertSame(cheap, station.getDocks().get(0));
        assertSame(fast, station.getDocks().get(1));
    }

    /**
     * Test the tie-break by id (ascending) when speed and fee are equal.
     */
    @Test
    public void testAddDockSameSpeedAndFeeOrdersById()
    {
        Dock first = new Dock("CC00_004", 80, 0.8f);
        station.addDock(fast);
        station.addDock(first);

        assertSame(fast, station.getDocks().get(0));
        assertSame(first, station.getDocks().get(1));
    }

    /**
     * Test that a null dock is rejected and nothing is added.
     */
    @Test
    public void testAddNullDock()
    {
        assertThrows(NullPointerException.class, () -> station.addDock(null));
        assertTrue(station.getDocks().isEmpty());
    }

    /**
     * Test that the list returned by getDocks cannot be modified from outside.
     */
    @Test
    public void testGetDocksIsUnmodifiable()
    {
        station.addDock(fast);
        assertThrows(UnsupportedOperationException.class,
                     () -> station.getDocks().clear());
        assertEquals(1, station.getDocks().size());
    }

    /**
     * Test that the first free dock in sorted order is returned.
     */
    @Test
    public void testGetFreeDock()
    {
        station.addDock(slow);
        station.addDock(fast);
        assertSame(fast, station.getFreeDock());
    }

    /**
     * Test the behaviour when the station has no docks.
     * Adapt it if getFreeDock returns null instead of throwing.
     */
    @Test
    public void testGetFreeDockWithoutDocks()
    {
        assertThrows(NoSuchElementException.class, () -> station.getFreeDock());
    }

    /**
     * Test that the recharges of all docks are added up.
     */
    @Test
    public void testGetBikesRechargedCount()
    {
        EBCompany company = new EBCompany("Test");
        ElectricBike bike1 = new ElectricBike(company, new Location(1, 1),
                                              new Location(2, 2), "EBCC0", 30);
        ElectricBike bike2 = new ElectricBike(company, new Location(3, 3),
                                              new Location(4, 4), "EBCC1", 30);
        station.addDock(fast);
        station.addDock(slow);

        fast.recharge(bike1, 10);
        fast.recharge(bike2, 10);
        slow.recharge(bike1, 10);

        assertEquals(3, station.getBikesRechargedCount());
    }

    /**
     * Test the location setter and its validation.
     */
    @Test
    public void testSetLocation()
    {
        station.setLocation(new Location(3, 3));
        assertEquals(new Location(3, 3), station.getLocation());
        assertThrows(NullPointerException.class, () -> station.setLocation(null));
    }

    /**
     * Test the string format required by the output template.
     */
    @Test
    public void testToString()
    {
        assertEquals("BikeStation: CC00, 0, 10-5", station.toString());
    }
}