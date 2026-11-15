import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The test class EBikeDemoTest.
 * Tests the console output of the SIMPLE scenario by capturing System.out.
 * Tests written for SIMPLE are skipped if the DEMO constant has another value.
 *
 * @author  (your name)
 * @version 2026.11.15
 */
public class EBikeDemoTest
{
    private PrintStream originalOut;
    private ByteArrayOutputStream captured;

    /**
     * Default constructor for test class EBikeDemoTest
     */
    public EBikeDemoTest()
    {
    }

    /**
     * Sets up the test fixture: redirects the console to a buffer.
     *
     * Called before every test case method.
     */
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    /**
     * Tears down the test fixture: restores the console.
     *
     * Called after every test case method.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
    }

    /**
     * @return The text printed so far, split in lines.
     */
    private String[] output()
    {
        return captured.toString(StandardCharsets.UTF_8).split("\\R");
    }

    /**
     * Creates a demo and skips the test if the scenario is not SIMPLE.
     * @return The new demo.
     */
    private EBikeDemo createSimpleDemo()
    {
        EBikeDemo demo = new EBikeDemo();
        assumeTrue(countStartingWith(output(), "(ElectricBike: ") == 2,
                   "This test is written for DemoType.SIMPLE");
        return demo;
    }

    /**
     * @param lines The lines to search in.
     * @param prefix The prefix to look for.
     * @return The number of lines that start with the prefix.
     */
    private int countStartingWith(String[] lines, String prefix)
    {
        int count = 0;
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param lines The lines to search in.
     * @param id The id of the bike.
     * @return The number of step lines with the state of the given bike.
     */
    private int countStateLines(String[] lines, String id)
    {
        int count = 0;
        for (String line : lines) {
            if (line.startsWith("(step: ") && line.contains("ElectricBike: " + id + ", ")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Test the initial information: company, bikes, stations and docks.
     */
    @Test
    public void testInitialInfo()
    {
        createSimpleDemo();
        String[] lines = output();

        assertEquals("( Compañía EBBike Cáceres )", lines[0]);
        assertEquals(2, countStartingWith(lines, "(ElectricBike: "));
        assertEquals(4, countStartingWith(lines, "(BikeStation: "));
        assertEquals(16, countStartingWith(lines, "(Dock: "));
        assertTrue(Arrays.asList(lines).contains(
            "(ElectricBike: EBCC0, 15wh, 15wh, 0, 0.0€, 0, 10-13, 10-11, 1-1)"));
        assertTrue(Arrays.asList(lines).contains(
            "(ElectricBike: EBCC1, 30wh, 30wh, 0, 0.0€, 0, 8-4, 10-5, 19-19)"));
    }

    /**
     * Test that the initial bikes are listed by increasing id.
     */
    @Test
    public void testInitialBikesOrder()
    {
        createSimpleDemo();
        List<String> lines = Arrays.asList(output());

        int bikes = lines.indexOf("( Electric Bikes )") + 2;
        assertTrue(lines.get(bikes).startsWith("(ElectricBike: EBCC0,"));
        assertTrue(lines.get(bikes + 1).startsWith("(ElectricBike: EBCC1,"));
    }

    /**
     * Test that stations are listed by id and that the docks of each
     * station are sorted by speed (descending).
     */
    @Test
    public void testInitialStationsAndDocksOrder()
    {
        createSimpleDemo();
        List<String> lines = Arrays.asList(output());

        int first = lines.indexOf("(BikeStation: CC00, 0, 10-5)");
        assertTrue(first >= 0);
        assertEquals("(Dock: CC00_003, 80wh, 0.8€, 0, 0.0€)", lines.get(first + 1));
        assertEquals("(Dock: CC00_002, 60wh, 0.6€, 0, 0.0€)", lines.get(first + 2));
        assertEquals("(Dock: CC00_001, 40wh, 0.4€, 0, 0.0€)", lines.get(first + 3));
        assertEquals("(Dock: CC00_000, 20wh, 0.2€, 0, 0.0€)", lines.get(first + 4));
        assertEquals("(BikeStation: CC01, 0, 10-11)", lines.get(first + 5));
    }

    /**
     * Test that a single step prints the state of every bike and nothing else.
     */
    @Test
    public void testStep()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.step(0);
        List<String> lines = Arrays.asList(output());

        assertTrue(lines.contains(
            "(step: 0 - ElectricBike: EBCC0, 15wh, 10wh, 0, 0.0€, 0, 10-12, 10-11, 1-1)"));
        assertTrue(lines.contains(
            "(step: 0 - ElectricBike: EBCC1, 30wh, 25wh, 0, 0.0€, 0, 9-5, 10-5, 19-19)"));
        assertEquals(2, countStartingWith(output(), "(step: 0 - "));
        assertEquals(0, countStartingWith(output(), "(step: 1 - "));
    }

    /**
     * Test that the simulation prints one state line per bike and step.
     */
    @Test
    public void testRunPrintsAllSteps()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.run();
        String[] lines = output();

        assertTrue(Arrays.asList(lines).contains("( Simulation start )"));
        assertEquals(EBikeDemo.MAXSTEPS, countStateLines(lines, "EBCC0"));
        assertEquals(EBikeDemo.MAXSTEPS, countStateLines(lines, "EBCC1"));
        assertEquals(0, countStartingWith(lines, "(step: " + EBikeDemo.MAXSTEPS + " - "));
    }

    /**
     * Test the recharge and arrival events of the SIMPLE scenario.
     */
    @Test
    public void testSimulationEvents()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.run();
        List<String> lines = Arrays.asList(output());

        assertTrue(lines.contains(
            "(step: 1 - ElectricBike: EBCC0 recharges: 10wh at dock: CC01_003 with cost: 8.0€ ********)"));
        assertTrue(lines.contains(
            "(step: 1 - ElectricBike: EBCC1 recharges: 10wh at dock: CC00_003 with cost: 8.0€ ********)"));
        assertTrue(lines.contains(
            "(step: 7 - ElectricBike: EBCC1 recharges: 30wh at dock: CC01_003 with cost: 24.0€ ********)"));
        assertTrue(lines.contains(
            "(step: 12 - ElectricBike: EBCC1 recharges: 25wh at dock: CC02_003 with cost: 20.0€ ********)"));
        assertTrue(lines.contains(
            "(step: 17 - ElectricBike: EBCC1 at target destination ********)"));
    }

    /**
     * Test some states of the simulation against the output template.
     */
    @Test
    public void testSimulationStates()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.run();
        List<String> lines = Arrays.asList(output());

        assertTrue(lines.contains(
            "(step: 1 - ElectricBike: EBCC1, 30wh, 30wh, 1, 8.0€, 0, 10-5, 10-11, 19-19)"));
        assertTrue(lines.contains(
            "(step: 7 - ElectricBike: EBCC1, 30wh, 30wh, 2, 32.0€, 0, 10-11, 14-16, 19-19)"));
        assertTrue(lines.contains(
            "(step: 12 - ElectricBike: EBCC1, 30wh, 30wh, 3, 52.0€, 0, 14-16, 19-19)"));
        assertTrue(lines.contains(
            "(step: 49 - ElectricBike: EBCC0, 15wh, 15wh, 1, 8.0€, 48, 10-11, 1-1)"));
        assertTrue(lines.contains(
            "(step: 49 - ElectricBike: EBCC1, 30wh, 5wh, 3, 52.0€, 32, 19-19, 19-19)"));
    }

    /**
     * Test the final information: bikes by arrival step (the one that
     * never arrives goes last) and stations by number of recharges.
     */
    @Test
    public void testFinalInfo()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.run();
        String[] lines = output();
        List<String> all = Arrays.asList(lines);

        // Bikes: EBCC1 arrived at step 17, EBCC0 never arrived
        int bikes = all.lastIndexOf("( Electric Bikes )") + 2;
        assertEquals("(ElectricBike: EBCC1, 30wh, 5wh, 3, 52.0€, 32, 19-19, 19-19)", lines[bikes]);
        assertEquals("(Dock: CC00_003)", lines[bikes + 1]);
        assertEquals("(Dock: CC01_003)", lines[bikes + 2]);
        assertEquals("(Dock: CC02_003)", lines[bikes + 3]);
        assertEquals("(ElectricBike: EBCC0, 15wh, 15wh, 1, 8.0€, 48, 10-11, 1-1)", lines[bikes + 4]);
        assertEquals("(Dock: CC01_003)", lines[bikes + 5]);

        // Stations: by recharges (descending), then by id
        int stations = all.lastIndexOf("( Bike Stations )") + 2;
        List<String> stationLines = new ArrayList<>();
        for (int i = stations; i < lines.length; i++) {
            if (lines[i].startsWith("(BikeStation: ")) {
                stationLines.add(lines[i]);
            }
        }
        assertEquals(Arrays.asList("(BikeStation: CC01, 2, 10-11)",
                                   "(BikeStation: CC00, 1, 10-5)",
                                   "(BikeStation: CC02, 1, 14-16)",
                                   "(BikeStation: CC03, 0, 8-4)"), stationLines);

        // First station: its dock with the bikes recharged, in chronological order
        assertEquals("(Dock: CC01_003, 80wh, 0.8€, 2, 32.0€)", lines[stations + 1]);
        assertEquals("(ElectricBike: EBCC0, 15wh, 15wh, 1, 8.0€, 48, 10-11, 1-1)", lines[stations + 2]);
        assertEquals("(ElectricBike: EBCC1, 30wh, 5wh, 3, 52.0€, 32, 19-19, 19-19)", lines[stations + 3]);
        assertEquals("(Dock: CC01_002, 60wh, 0.6€, 0, 0.0€)", lines[stations + 4]);
    }

    /**
     * Test that reset leaves the demo in its initial state, without
     * duplicating bikes, stations or docks.
     */
    @Test
    public void testReset()
    {
        EBikeDemo demo = createSimpleDemo();
        demo.run();
        captured.reset();

        demo.reset();
        String[] lines = output();

        assertEquals(2, countStartingWith(lines, "(ElectricBike: "));
        assertEquals(4, countStartingWith(lines, "(BikeStation: "));
        assertEquals(16, countStartingWith(lines, "(Dock: "));
        assertTrue(Arrays.asList(lines).contains("(BikeStation: CC01, 0, 10-11)"));
    }

    /**
     * Test that the main method runs the whole simulation.
     */
    @Test
    public void testMain()
    {
        EBikeDemo.main();

        assertTrue(Arrays.asList(output()).contains("( Final information )"));
    }
}