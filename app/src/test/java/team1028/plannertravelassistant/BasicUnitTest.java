package team1028.plannertravelassistant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BasicUnitTest {
	private ArrayList<String> origins;
	private ArrayList<String> destinations;

	@Before
	public void setUp() throws Exception {
		origins = new ArrayList<>();
		destinations = new ArrayList<>();

		origins.add("manchester+,+CT"); // ensure no spaces in the string
		destinations.add("44.2717525+,+-75.7971609");
	}

	@After
	public void tearDown() {
		origins = null;
		destinations = null;
	}

    @Test
    public void testTrafficRouter() throws Exception {
        TrafficRouter router = new TrafficRouter(origins, destinations);

        String json = router.getTrafficJson("imperial", null, "driving", "pessimistic");
        assertNotNull(json); // TODO what does an error look like?
        if (!json.isEmpty()) {
//            System.out.println("returned json " + json);
            float duration = router.parseTravelDuration(json);
//            System.out.println(duration);
	        assertTrue(duration > 1000);
        }
    }

	@Test
	public void testMultiplePlaces() throws Exception {
		origins.add("Burlington+VT");
		destinations.add("Houston,+TX");

		TrafficRouter router = new TrafficRouter(origins, destinations);

		String json = router.getTrafficJson("imperial", null, "driving", "pessimistic");
		assertNotNull(json);

		if (!json.isEmpty()) {
			float duration = router.parseTravelDuration(json);

			assertTrue(duration > 0);
//			System.out.println("returned json " + json);
			assertTrue(duration > 1000);
		}
	}

	@Test
	public void testDifferentSettings() throws Exception {
		TrafficRouter router = new TrafficRouter(origins, destinations);

		String json1 = router.getTrafficJson("imperial", null, "driving", "optimistic");
		String json2 = router.getTrafficJson("metric", null, "walking", "pessimistic");

		// Failed test case may be the fault of Google servers
		assertTrue(!json1.isEmpty());
		assertTrue(!json2.isEmpty());

		float time1 = router.parseTravelDuration(json1);
		float time2 = router.parseTravelDuration(json2);

		System.out.println(json1);
		System.out.println(json2);

		assertTrue(time1 > 100);
		assertTrue(time2 > 100);

		// Walking should take longer
		assertTrue(time1 < time2);
	}
}