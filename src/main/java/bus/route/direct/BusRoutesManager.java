package bus.route.direct;


import java.io.*;
import java.util.*;

/**
 * @author rvlasov
 *
 */
public class BusRoutesManager {
	
	/**
	 * stations to routes map
	 * 
	 * Inner hash map stores "route ID-->index".
	 * Index here means the "moment" when the bus arrives at this bus station for the given route, i.e.
	 * the number of the bus station on the given route
	 */
	private HashMap<Integer, HashMap<Integer, Integer>>  m_stationsToRoutesMultiMap;
	
	
	/**
    * Load bus routes from the given file
    * 
    * @param fileName
    */
	public void loadRoutes(String fileName) throws IOException, IllegalArgumentException {
		if (fileName == null || fileName.length() == 0)
			throw new IllegalArgumentException();
		
		try (final FileInputStream fis = new FileInputStream(new File(fileName));
			 final Scanner in = new Scanner(new BufferedInputStream(fis)) ) {
			// Build "station ID"-->"<route ID-->index in this route> map" map while loading the data
			m_stationsToRoutesMultiMap = new HashMap<Integer, HashMap<Integer, Integer>>();
			
			int n = in.nextInt();
			in.nextLine(); // move cursor to the next line
			for (int i = 0; i < n; i++) {
				String[] strNumbers = in.nextLine().split(" ");
                
				// read route ID
				int routeId = Integer.valueOf(strNumbers[0]);
                
				// read the route
                int stationId;
                HashMap<Integer, Integer> routesOfStation = null;
                for (int j = 1; j < strNumbers.length; j++) {
                	// read station ID
                	stationId = Integer.valueOf(strNumbers[j]);
                	
                	// add the current route to the station's hash map of route-index pairs associated with the station
                	routesOfStation = m_stationsToRoutesMultiMap.get(stationId);
                	if (routesOfStation == null) {
                		routesOfStation = new HashMap<Integer, Integer>();
                		m_stationsToRoutesMultiMap.put(stationId, routesOfStation);
                	}
                	routesOfStation.put(routeId, j);
                }
			}
		}
	}

	/**
    * Returns @true if there is a route having a direct connection from
    * the specified start bus station to the specified end bus station
    * 
    * @param startId - ID of the start bus station
    * @param endId - ID of the end bus station 
    */
	public boolean isDirectRoute(int startId, int endId) {
		if (startId == endId)
			return true;
		
		final HashMap<Integer, Integer> routesOfStart = m_stationsToRoutesMultiMap.get(startId);
		final HashMap<Integer, Integer> routesOfEnd = m_stationsToRoutesMultiMap.get(endId);
		
		if (routesOfStart == null || routesOfEnd == null)
			return false; // station ID(s) not found	
		
		// Select the smaller map to iterate over, so that computations are faster
		HashMap<Integer, Integer> smallerMap = routesOfStart;
		HashMap<Integer, Integer> biggerMap = routesOfEnd;
		int direction = 1; // Should the bus encounter station corresponding to the smaller map before (1)
		                   // or after (-1) the station corresponding to the bigger map?
		if (routesOfStart.size() > routesOfEnd.size()) {
			smallerMap = routesOfEnd;
			biggerMap = routesOfStart;
			direction = -1;
		}
		
		// Iterate over smaller map and check if any route from it is present in the bigger map
		Integer idxBigger;
		for (Map.Entry<Integer, Integer> e: smallerMap.entrySet()) { // Time complexity is O(|smallerSet|)
			int routeIdSmaller = e.getKey();
			idxBigger =	biggerMap.get(routeIdSmaller);
			if (idxBigger == null)
				continue;
			// At this point we know that biggerMap has the route from the smaller map.
			// Just need to check if the stations are encountered by the bus in the right order
			int idxSmaller = e.getValue();
			if (idxBigger - idxSmaller > 0 && direction > 0 ||
				idxBigger - idxSmaller < 0 && direction < 0) {
				// Bus route found!
				return true;
			}
		}
		
		return false;
	}
}
