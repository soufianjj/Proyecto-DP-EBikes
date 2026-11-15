import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test implementation of the {@link Location} class.
 * Provides unit tests for methods like {@code distance()} and {@code nextLocation()}.
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 * @version 2026.10.13 DP classes (adaptado a Java 8+)
 */
public class LocationTest
{
    private Location center;

    /**
     * Default constructor for test class LocationTest.
     */
    public LocationTest()
    {
    }

    /**
     * Sets up the test fixture.
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        center = new Location(10, 10);
    }

    /**
     * Tears down the test fixture.
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        center = null;
    }

    /**
     * Test the {@code distance} method of the {@link Location} class.
     * Checks distances from a central location to surrounding points.
     */
    @Test
    public void testDistance()
    {
        assertEquals(5, new Location(0, 0).distance(new Location(1, 5)));
        assertEquals(4, new Location(6, 6).distance(new Location(5, 2)));

        assertEquals(0, center.distance(new Location(10, 10)));
        assertEquals(3, center.distance(new Location(13, 10)));
        assertEquals(3, center.distance(new Location(10, 7)));
        assertEquals(center.distance(new Location(2, 15)),
                     new Location(2, 15).distance(center));
        assertEquals(8, center.distance(new Location(2, 15)));
    }

    /**
     * Test the {@code nextLocation} method when the destination is adjacent
     * (one step away in any direction).
     */
    @Test
    public void testAdjacentLocations()
    {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Location adjacent = new Location(10 + dx, 10 + dy);
                assertEquals(adjacent, center.nextLocation(adjacent));
            }
        }
    }

    /**
     * Test the {@code nextLocation} method when the destination is not adjacent
     * (more than one step away).
     */
    @Test
    public void testNonAdjacentLocations()
    {
        // (0,0) -> (1,5): one diagonal step, then straight on y
        Location p0 = new Location(0, 0);
        Location p1 = new Location(1, 5);
        Location step = p0.nextLocation(p1);
        assertEquals(new Location(1, 1), step);
        step = step.nextLocation(p1);
        assertEquals(new Location(1, 2), step);

        // (6,6) -> (5,2)
        Location p2 = new Location(6, 6);
        Location p3 = new Location(5, 2);
        step = p2.nextLocation(p3);
        assertEquals(new Location(5, 5), step);
        step = step.nextLocation(p3);
        assertEquals(new Location(5, 4), step);

        // (3,2) -> (6,0): x grows, y decreases
        Location p5 = new Location(3, 2);
        Location p6 = new Location(6, 0);
        step = p5.nextLocation(p6);
        assertEquals(new Location(4, 1), step);
        step = step.nextLocation(p6);
        assertEquals(new Location(5, 0), step);
        step = step.nextLocation(p6);
        assertEquals(p6, step);
    }

    /**
     * Test that the whole path from (0,0) to (3,4) is the expected one.
     */
    @Test
    public void testFullPath()
    {
        Location current = new Location(0, 0);
        Location target = new Location(3, 4);
        Location[] expected = {new Location(1, 1), new Location(2, 2),
                               new Location(3, 3), new Location(3, 4)};
        for (int i = 0; i < expected.length; i++) {
            current = current.nextLocation(target);
            assertEquals(expected[i], current);
        }
    }

    /**
     * Test that moving from the destination itself returns the destination.
     */
    @Test
    public void testNextLocationAtDestination()
    {
        assertEquals(center, center.nextLocation(new Location(10, 10)));
    }

    /**
     * Test the {@code equals} method.
     */
    @Test
    public void testEquals()
    {
        assertEquals(new Location(3, 4), new Location(3, 4));
        assertNotEquals(new Location(3, 4), new Location(4, 3));
        assertNotEquals(new Location(3, 4), null);
        assertNotEquals(new Location(3, 4), "3-4");
    }

    /**
     * Test that coordinates out of the 0-19 range are rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCoordinate()
    {
        new Location(-1, 5);
    }

    /**
     * Test that coordinates above 19 are rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCoordinateTooBig()
    {
        new Location(0, 20);
    }

    /**
     * Test that the limit coordinates are accepted.
     */
    @Test
    public void testLimitCoordinates()
    {
        assertEquals("0-0", new Location(0, 0).toString());
        assertEquals("19-19", new Location(19, 19).toString());
    }
}