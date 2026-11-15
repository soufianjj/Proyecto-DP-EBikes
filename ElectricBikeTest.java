import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class ElectricBikeTest.
 * Tests creation, battery, route calculation, recharge and simulation steps
 * of an {@link ElectricBike}.
 *
 * @author  (your name)
 * @version 2026.11.15
 */
public class ElectricBikeTest
{
    private EBCompany company;
    private BikeStation station;
    private Dock fastDock;
    private Dock slowDock;

    /**
     * Default constructor for test class ElectricBikeTest
     */
    public ElectricBikeTest()
    {
    }

    /**
     * Sets up the test fixture: a company with one station at 10-11
     * that has two docks.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        company = new EBCompany("Test");
        station = new BikeStation("CC01", new Location(10, 11));
        fastDock = new Dock("CC01_003", 80, 0.8f);
        slowDock = new Dock("CC01_000", 20, 0.2f);
        station.addDock(slowDock);
        station.addDock(fastDock);
        company.addBikeStation(station);
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
        station = null;
        fastDock = null;
        slowDock = null;
    }

    /**
     * Test that a bike is created with the right initial values.
     */
    @Test
    public void testCreation()
    {
        ElectricBike bike = new ElectricBike(company, new Location(1, 1),
                                             new Location(2, 2), "EBCC0", 30);

        assertEquals("EBCC0", bike.getIdBike());
        assertEquals(new Location(2, 2), bike.getTargetLocation());
        assertNull(bike.getRechargingLocation());
        assertTrue(bike.getDocks().isEmpty());
        assertEquals(Integer.MAX_VALUE, bike.getArrivalStep());
        assertEquals("ElectricBike: EBCC0, 30wh, 30wh, 0, 0.0€, 0, 1-1, 2-2", bike.toString());
    }

    /**
     * Test that invalid arguments are rejected in the constructor.
     */
    @Test
    public void testInvalidCreation()
    {
        Location a = new Location(1, 1);
        Location b = new Location(2, 2);

        assertThrows(NullPointerException.class, () -> new ElectricBike(null, a, b, "EBCC0", 30));
        assertThrows(NullPointerException.class, () -> new ElectricBike(company, null, b, "EBCC0", 30));
        assertThrows(NullPointerException.class, () -> new ElectricBike(company, a, null, "EBCC0", 30));
        assertThrows(IllegalArgumentException.class, () -> new ElectricBike(company, a, b, "EBCC01", 30));
        assertThrows(IllegalArgumentException.class, () -> new ElectricBike(company, a, b, null, 30));
    }

    /**
     * Test the battery check: each step costs 5 wh.
     */
    @Test
    public void testEnoughBattery()
    {
        ElectricBike bike = new ElectricBike(company, new Location(1, 1),
                                             new Location(2, 2), "EBCC0", 30);

        assertTrue(bike.enoughBattery(0));
        assertTrue(bike.enoughBattery(6));   // 30 wh needed, 30 wh available
        assertFalse(bike.enoughBattery(7));  // 35 wh needed

        bike.reduceBatteryLevel();           // 25 wh left
        assertTrue(bike.enoughBattery(5));
        assertFalse(bike.enoughBattery(6));
    }

    /**
     * Test the distance to the target.
     */
    @Test
    public void testDistanceToTheTargetLocation()
    {
        ElectricBike bike = new ElectricBike(company, new Location(0, 0),
                                             new Location(1, 5), "EBCC0", 30);

        assertEquals(5, bike.distanceToTheTargetLocation());
    }

    /**
     * Test the route when the battery is enough to reach the target:
     * no recharge stop is planned.
     */
    @Test
    public void testCalculateRouteDirect()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 13),
                                             new Location(10, 9), "EBCC0", 30);
        bike.calculateRoute();

        assertNull(bike.getRechargingLocation());
        assertEquals("10-13 -> 10-9", bike.getStringRoute());
    }

    /**
     * Test the route when the battery is not enough: the bike goes
     * through the station.
     */
    @Test
    public void testCalculateRouteWithRecharge()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 13),
                                             new Location(1, 1), "EBCC0", 15);
        bike.calculateRoute();

        assertEquals(new Location(10, 11), bike.getRechargingLocation());
        assertEquals("10-13 -> 10-11 -> 1-1", bike.getStringRoute());
    }

    /**
     * Test that, among several reachable stations, the one with the lowest
     * total distance (bike -> station -> target) is chosen.
     */
    @Test
    public void testCalculateRouteChoosesBestStation()
    {
        company.addBikeStation(new BikeStation("CC00", new Location(10, 5)));
        // 45 wh: reaches both stations, but the target (distance 12) needs 60 wh.
        // Station 10-11: 2 + 10 = 12. Station 10-5: 8 + 9 = 17.
        ElectricBike bike = new ElectricBike(company, new Location(10, 13),
                                             new Location(1, 1), "EBCC0", 45);
        bike.calculateRoute();

        assertEquals(new Location(10, 11), bike.getRechargingLocation());
    }

    /**
     * Test that no station is chosen if none can be reached with the
     * current battery.
     */
    @Test
    public void testCalculateRouteNoReachableStation()
    {
        EBCompany farCompany = new EBCompany("Far");
        farCompany.addBikeStation(new BikeStation("CC00", new Location(10, 5)));
        // 15 wh only allow 3 steps, and the station is 8 steps away.
        ElectricBike bike = new ElectricBike(farCompany, new Location(10, 13),
                                             new Location(1, 1), "EBCC0", 15);
        bike.calculateRoute();

        assertNull(bike.getRechargingLocation());
    }

    /**
     * Test the recharge: battery full, cost computed, dock registered
     * and route recalculated.
     */
    @Test
    public void testRecharge()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 11),
                                             new Location(1, 1), "EBCC0", 30);
        bike.reduceBatteryLevel();
        bike.reduceBatteryLevel();           // 20 wh, 10 wh missing

        float cost = bike.recharge(fastDock);

        assertEquals(8.0f, cost, 0.001f);
        assertEquals("ElectricBike: EBCC0, 30wh, 30wh, 1, 8.0€, 0, 10-11, 1-1", bike.toString());
        assertEquals(1, bike.getDocks().size());
        assertSame(fastDock, bike.getDocks().get(0));
        assertEquals(1, fastDock.getNumerEVRecharged());
        assertEquals(8.0f, fastDock.getAmountCollected(), 0.001f);
    }

    /**
     * Test that charging twice in the same dock keeps the dock only once
     * in the list of docks of the bike.
     */
    @Test
    public void testRechargeSameDockTwice()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 11),
                                             new Location(1, 1), "EBCC0", 30);
        bike.reduceBatteryLevel();
        bike.recharge(fastDock);
        bike.reduceBatteryLevel();
        bike.recharge(fastDock);

        assertEquals(1, bike.getDocks().size());
        assertEquals(2, fastDock.getNumerEVRecharged());
    }

    /**
     * Test that recharging with a null dock is rejected.
     */
    @Test
    public void testRechargeNullDock()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 11),
                                             new Location(1, 1), "EBCC0", 30);

        assertThrows(NullPointerException.class, () -> bike.recharge(null));
    }

    /**
     * Test the simulation of the first steps of a bike that needs a
     * recharge (first scenario of the output template, bike EBCC0).
     */
    @Test
    public void testActGoesToStationAndRecharges()
    {
        ElectricBike bike = new ElectricBike(company, new Location(10, 13),
                                             new Location(1, 1), "EBCC0", 15);

        assertNull(bike.act(0));
        assertEquals("ElectricBike: EBCC0, 15wh, 10wh, 0, 0.0€, 0, 10-12, 10-11, 1-1",
                     bike.toString());

        String event = bike.act(1);
        assertEquals("ElectricBike: EBCC0 recharges: 10wh at dock: CC01_003 with cost: 8.0€ ********",
                     event);
        assertEquals("ElectricBike: EBCC0, 15wh, 15wh, 1, 8.0€, 0, 10-11, 1-1",
                     bike.toString());
        assertSame(fastDock, bike.getDocks().get(0));
    }

    /**
     * Test that the bike registers its arrival step and reports it.
     */
    @Test
    public void testActArrivesAtTarget()
    {
        ElectricBike bike = new ElectricBike(company, new Location(1, 1),
                                             new Location(1, 3), "EBCC0", 30);

        assertNull(bike.act(0));
        assertEquals(Integer.MAX_VALUE, bike.getArrivalStep());

        assertEquals("ElectricBike: EBCC0 at target destination ********", bike.act(1));
        assertEquals(1, bike.getArrivalStep());
    }

    /**
     * Test that a bike already at its target stays idle and counts the turns.
     */
    @Test
    public void testActIdleAtTarget()
    {
        ElectricBike bike = new ElectricBike(company, new Location(1, 1),
                                             new Location(1, 2), "EBCC0", 30);
        bike.act(0);   // arrives
        assertNull(bike.act(1));
        assertNull(bike.act(2));

        assertEquals("ElectricBike: EBCC0, 30wh, 25wh, 0, 0.0€, 2, 1-2, 1-2", bike.toString());
        assertEquals(0, bike.getArrivalStep());
    }

    /**
     * Test that a bike without enough battery and without any reachable
     * station stays where it is.
     */
    @Test
    public void testActNoBatteryStaysIdle()
    {
        EBCompany emptyCompany = new EBCompany("Empty");
        ElectricBike bike = new ElectricBike(emptyCompany, new Location(1, 1),
                                             new Location(10, 10), "EBCC0", 15);

        assertNull(bike.act(0));
        assertEquals("ElectricBike: EBCC0, 15wh, 15wh, 0, 0.0€, 1, 1-1, 10-10", bike.toString());
    }
}