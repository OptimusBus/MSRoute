package innerconnector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import innerconnector.HttpConnector.Method;

public class HttpConnector {

	public HttpConnector() {}
	
	
	/**
	 * Generic request maker for Http request
	 * @param url the url of the request
	 * @param m the type of request to execute (GET, POST, DELETE, PUT)
	 * @param queryParam a map of query param - value for multiple param GET request
	 * @param params the params for POST and PUT request
	 * @return a Response or null
	 */
	private static Response makeRequest(String url, Method m, Map<String,String> queryParam, String params) {	
		WebClient client = WebClient.create(baseAddress);
		client.accept("application/json");
		client.type("application/json");
		client.path(url);
		switch(m) {
			case GET:
				if(queryParam != null) {
					for (Entry<String, String> entry : queryParam.entrySet()) {
				        client.query(entry.getKey(), entry.getValue());
				    }
				}
				return client.get();
			case POST:
				if(params != null) return client.post(params);
			case PUT:
				if(params != null) return client.put(params);
			case DELETE:
				if(params != null) return client.delete();
			default:
				return null;
		}
	}
	/**
	 * Request the shortest path form the MSRoadNetwork service
	 * @param source the id of the starting Node
	 * @param dest the id of the destination Node
	 * @return the Response of the service
	 */
	public static Response getShortestPath(Integer source, Integer dest) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("source", source.toString());
		param.put("dest", dest.toString());
		Response r = makeRequest("/roadNetwork/shortestPath", Method.GET, param,  null);
		System.out.println(r.getStatus());
		return r;
	}
	
	/**
	 * Request the node from the MSRoadNetwork service
	 * @param id the nodeId of the requested Node object
	 * @return the Response of the  service
	 */
	public static Response getNodeById(String id) {
		return makeRequest("/roadNetwork/"+id, Method.GET, null, null);
	}
	
	/**
	 * Request the node from the MSRoadNetwork service from is coordinate
	 * @param lat the latitude of the requested Node object
	 * @param lon the longitude of the requested Node object
	 * @return the Response of the  service
	 */
	public static Response getNodeByCoordinate(Double lat, Double lon) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("latitude", lat.toString());
		param.put("longitude", lon.toString());
		return makeRequest("/roadNetwork/nearestNode", Method.GET, param, null);
	}
	
	/**
	 * Request a street from MSRoadNetwork service form is id
	 * @param id the identifier of the requested street object
	 * @return the Response of the service
	 */
	public static Response getStreetbyId(String id) {
		return makeRequest("/roadNetwork/street/"+id, Method.GET, null, null);
	}
	
	/**
	 * Request a street from MSRoadNetwork service form is extremes
	 * @param start the identifier of the "from" Node of the Street
	 * @param dest the identifier of the "to" node of the Street
	 * @return the Response of the service
	 */
	public static Response getStreet(Integer start, Integer dest) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("start", start.toString());
		param.put("dest", dest.toString());
		return makeRequest("/roadNetwork/street", Method.GET, param, null);
	}
	
	/**
	 * Request the shortestPath from MSRoadNetwork as a list of Streets
	 * @param source is the Id of the starting Node
	 * @param dest is  the Id of the destination Node
	 * @return the Response of the service
	 */
	public static Response getShortestStreet(Integer source, Integer dest) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("source", source.toString());
		param.put("dest", dest.toString());
		Response r = makeRequest("/roadNetwork/shortestStreet", Method.GET, param,  null);
		System.out.println(r.getStatus());
		return r;
	}
	
	/**
	 * Request a Vehicle Object form the MSVehicle service
	 * @param id the vehicleId of the requested Vehicle object
	 * @return the Response of the Service
	 */
	public static Response getVehicle(String id) {
		return makeRequest("/vehicles/"+id, Method.GET, null, null);
	}
	
	/**
	 * Request all the waiting Booking from the MSBooking service
	 * @return the Response of the service
	 */
	public static Response getWaitingBookings() {
		return null;
	}
	
	/**
	 * Request all the  booking assigned to a Vehicle from MSBooking service
	 * @param vehicleId
	 * @return the Response of the service
	 */
	public static Response getBookingsByVehicle(String vehicleId) {
		return null;
	}
	
	private static final String baseAddress = "http://gateway-optimusbus.router.default.svc.cluster.local/optimusbus";
	public static enum Method {GET, POST, PUT, DELETE}
	
}
