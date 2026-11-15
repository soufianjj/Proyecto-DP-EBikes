import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The test class DockTest.
 * Tests the creation, validation and recharge operations of a {@link Dock}.
 *
 * @author  (your name)
 * @version 2026.11.15
 */
public class DockTest
{
    private Dock dock;
    private ElectricBike bike1;
    private ElectricBike bike2;

    /**
     * Default constructor for test class DockTest
     */
    public DockTest()
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
        EBCompany company = new EBCompany("Test");
        dock = new Dock("CC01_000", 80, 0.8f);
        bike1 = new ElectricBike(company, new Location(1, 1), new Location(2, 2), "EBCC0", 30);
        bike2 = new ElectricBike(company, new Location(3, 3), new Location(4, 4), "EBCC1", 30);
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        dock = null;
        bike1 = null;
        bike2 = null;
    }

    /**
     * Test that a dock is created with the right initial values.
     */
    @Test
    public void testCreation()
    {
        assertEquals("CC01_000", dock.getId());
        assertEquals(80, dock.getChargingSpeed());
        assertEquals(0.8f, dock.getChargingFee(), 0.001f);
        assertEquals(0.0f, dock.getAmountCollected(), 0.001f);
        assertEquals(0, dock.getNumerEVRecharged());
        assertTrue(dock.getBikesRecharged().isEmpty());
        assertTrue(dock.isFree());
    }

    /**
     * Test that invalid ids, speeds and fees are rejected.
     */
    @Test
    public void testInvalidCreation()
    {
        assertThrows(IllegalArgumentException.class, () -> new Dock("cc01_000", 80, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new Dock("CC01-000", 80, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new Dock("CC01_0", 80, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new Dock(null, 80, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new Dock("CC01_000", 0, 0.8f));
        assertThrows(IllegalArgumentException.class, () -> new Dock("CC01_000", 80, -0.1f));
    }

    /**
     * Test the recharge: it returns the cost, registers the bike and
     * increases the amount collected.
     */
    @Test
    public void testRecharge()
    {
        float cost = dock.recharge(bike1, 10);

        assertEquals(8.0f, cost, 0.001f);
        assertEquals(1, dock.getNumerEVRecharged());
        assertSame(bike1, dock.getBikesRecharged().get(0));
        assertEquals(8.0f, dock.getAmountCollected(), 0.001f);
    }

    /**
     * Test several recharges: the amount accumulates and the bikes keep
     * the chronological order.
     */
    @Test
    public void testRechargeSeveralBikes()
    {
        dock.recharge(bike1, 10);
        dock.recharge(bike2, 30);

        assertEquals(2, dock.getNumerEVRecharged());
        assertSame(bike1, dock.getBikesRecharged().get(0));
        assertSame(bike2, dock.getBikesRecharged().get(1));
        assertEquals(32.0f, dock.getAmountCollected(), 0.001f);
    }

    /**
     * Test that invalid recharges are rejected and change nothing.
     */
    @Test
    public void testRechargeInvalid()
    {
        assertThrows(NullPointerException.class, () -> dock.recharge(null, 10));
        assertThrows(IllegalArgumentException.class, () -> dock.recharge(bike1, 0));
        assertThrows(IllegalArgumentException.class, () -> dock.recharge(bike1, -5));
        assertEquals(0, dock.getNumerEVRecharged());
        assertEquals(0.0f, dock.getAmountCollected(), 0.001f);
    }

    /**
     * Test adding a bike to the list of recharged bikes.
     */
    @Test
    public void testAddBike()
    {
        dock.addBike(bike1);

        assertEquals(1, dock.getNumerEVRecharged());
        assertSame(bike1, dock.getBikesRecharged().get(0));
        assertThrows(NullPointerException.class, () -> dock.addBike(null));
    }

    /**
     * Test the update of the amount collected.
     */
    @Test
    public void testUpdateAmountCollected()
    {
        dock.updateAmountCollected(8.0f);
        dock.updateAmountCollected(2.5f);

        assertEquals(10.5f, dock.getAmountCollected(), 0.001f);
        assertThrows(IllegalArgumentException.class, () -> dock.updateAmountCollected(-1f));
        assertEquals(10.5f, dock.getAmountCollected(), 0.001f);
    }

    /**
     * Test that the list of recharged bikes cannot be modified from outside.
     */
    @Test
    public void testGetBikesRechargedIsUnmodifiable()
    {
        dock.recharge(bike1, 10);

        assertThrows(UnsupportedOperationException.class,
                     () -> dock.getBikesRecharged().clear());
        assertEquals(1, dock.getNumerEVRecharged());
    }

    /**
     * Test the string format required by the output template.
     */
    @Test
    public void testToString()
    {
        assertEquals("Dock: CC01_000, 80wh, 0.8€, 0, 0.0€", dock.toString());

        dock.recharge(bike1, 10);
        assertEquals("Dock: CC01_000, 80wh, 0.8€, 1, 8.0€", dock.toString());
    }
}