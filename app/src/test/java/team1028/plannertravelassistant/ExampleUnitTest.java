package team1028.plannertravelassistant;

import team1028.plannertravelassistant.TrafficRouter;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testTrafficRouter() throws Exception {
        // TODO test JSON strings
        ArrayList<String> origins = new ArrayList<>();
        origins.add("Origin1");

        ArrayList<String> destinations = new ArrayList<>();
        destinations.add("Destination1");

        TrafficRouter router = new TrafficRouter(origins, destinations);

        String json = router.totalTravelTime("imperial", null, "driving", "pessimistic");
        assertNotNull(json); // TODO what does an error look like?
    }
}