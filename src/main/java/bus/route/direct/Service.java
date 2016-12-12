package bus.route.direct;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


// Would do more tests in real projects, e.g. for:
// 1) queryToMap
// 2) getIntParam
// ...



/**
 * @author rvlasov
 *
 */
public class Service implements HttpHandler {
	private BusRoutesManager m_busRoutesMan = null;
	private HttpServer m_server = null;
	private static final int s_port = 8088;
	private static final String s_context = "/api/direct";
	
	/**
    * Prepares the bus routes data and starts the server
    * 
    * @param busRoutesFileName
    */
	public void start(String busRoutesFileName) {	
		m_busRoutesMan = new BusRoutesManager();
		
		// Prepare bus routes
		try {
			m_busRoutesMan.loadRoutes(busRoutesFileName);
		}
		catch (Exception e) {
			System.out.println("Error loading file '" + busRoutesFileName + "':");
			e.printStackTrace();
			return;
		}
		
		// Prepare and start server
		try {
			m_server = HttpServer.create(new InetSocketAddress(s_port), 0);
		}
		catch (Exception e) {
			System.out.println("Error creating http server at localhost:" + s_port + " :");
			e.printStackTrace();
			return;
		}		
		if (m_server.createContext(s_context, this) == null) {
			System.out.println("Can't context " + s_context);
			return;
		}
		m_server.start();
	}
	
	/**
    * @param httpExchange
    */
	@Override
    public void handle(HttpExchange httpExchange) throws IOException {        
        try {
        	// Get parameters
        	HashMap<String, String> paramsMap = queryToMap(httpExchange.getRequestURI().getQuery());
	        if (paramsMap.size() != 2)
	        	throw new IllegalArgumentException();

	        int depId = getIntParam(paramsMap, "dep_sid");
	        int arrId = getIntParam(paramsMap, "arr_sid");
	
	        // Prepare response	
	        // TODO: Use org.json.JSONObject for more complex json objects 
	        final StringBuilder sb = new StringBuilder();
	        sb.append("{\n");
	        sb.append("    \"dep_sid\": " + depId + ",\n");
	        sb.append("    \"dep_sid\": " + arrId + ",\n");
	        sb.append("    \"direct_bus_route\": " + m_busRoutesMan.isDirectRoute(depId, arrId) +"\n");
	        sb.append("}");
	        
	        // Write response
	        String response = sb.toString();
	        httpExchange.sendResponseHeaders(200, response.length());
	        try (OutputStream os = httpExchange.getResponseBody()) {
	            os.write(response.getBytes());
	        }
        }
        catch (Exception e) {
        	System.out.println("Bad request");
            e.printStackTrace();
            httpExchange.sendResponseHeaders(400, 0);
        }
    }
	
	
	/**
    * Returns the url parameters in a map
    * 
    * @param query
    * @return map
    */
	public static HashMap<String, String> queryToMap(String query){
		HashMap<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			}
			else {
				result.put(pair[0], "");
	        }
	    }
		
	    return result;
    }
	
	/**
    * Returns value of specified param in parameters map
    * 
    * @param paramsMap
    * @return paramName
    */
	public static int getIntParam(final Map<String, String> paramsMap, String paramName)
			throws IllegalArgumentException, NumberFormatException
	{
		String strVal = paramsMap.get(paramName);
		if (strVal == null || strVal.length() == 0)
			throw new IllegalArgumentException();
		
		Integer iVal = new Integer(strVal);
		return iVal.intValue();
	}
	
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1 || args[0].length() == 0) {
            System.out.println("Usage: pass <bus routes data file> as first argument");
            return;
        }
		
		Service service = new Service();
		service.start(args[0]);
	}
}
