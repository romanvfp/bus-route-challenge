package bus.route.direct;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author rvlasov
 *
 */
public class BusRoutesManagerTest {
	private BusRoutesManager busRoutesMan = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		busRoutesMan = new BusRoutesManager();
		busRoutesMan.loadRoutes("src/test/resources/data.txt");
	}

	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_exists_returnsTrue() {
		assertTrue(busRoutesMan.isDirectRoute(3, 6));
	}
	
	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_stationsExistButWrongOrder_returnsFalse() {
		assertFalse(busRoutesMan.isDirectRoute(6, 3));
	}
	
	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_firstStationDoesntExist_returnsFalse() {
		assertFalse(busRoutesMan.isDirectRoute(10, 6));
	}
	
	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_secondStationDoesntExist_returnsFalse() {
		assertFalse(busRoutesMan.isDirectRoute(3, 10));
	}
	
	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_theSameStation_returnsTrue() {
		assertTrue(busRoutesMan.isDirectRoute(3, 3));
	}
	
	/**
	 * Test method for {@link bus.route.direct.BusRoutesManager#isDirectRoute(int, int)}.
	 */
	@Test
	public void isDirectRoute_stationsInDifferentRoutes_returnsFalse() {
		assertFalse(busRoutesMan.isDirectRoute(2, 5));
	}
}
