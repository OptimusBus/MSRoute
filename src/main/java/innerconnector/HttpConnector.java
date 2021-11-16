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
		Response r = makeRequest("/roadnetwork/shortestPath", Method.GET, param,  null);
		System.out.println("Get Shortest path " + r.getStatus());
		return r;
	}
	
	/**
	 * Request the node from the MSRoadNetwork service
	 * @param id the nodeId of the requested Node object
	 * @return the Response of the  service
	 */
	public static Response getNodeById(String id) {
		return makeRequest("/roadnetwork/"+id, Method.GET, null, null);
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
		return makeRequest("/roadnetwork/nearestNode", Method.GET, param, null);
	}
	
	/**
	 * Request a street from MSRoadNetwork service form is id
	 * @param id the identifier of the requested street object
	 * @return the Response of the service
	 */
	public static Response getStreetbyId(String id) {
		return makeRequest("/roadnetwork/street/"+id, Method.GET, null, null);
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
		return makeRequest("/roadnetwork/street", Method.GET, param, null);
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
		Response r = makeRequest("/roadnetwork/shortestStreet", Method.GET, param,  null);
		System.out.println("Get shortest street "+r.getStatus());
		return r;
	}
	
	/**
	 * Request a Vehicle Object form the MSVehicle service
	 * @param id the vehicleId of the requested Vehicle object
	 * @return the Response of the Service
	 */
	public static Response getVehicle(String id) {
		Response r = makeRequest("/vehicles/"+id, Method.GET, null, null);
		System.out.println("Get vehicle :" + r.getStatus());
		return r;
	}
	
	/**
	 * Request all booking (with Status different from closed or cancelled)
	 * @return the response of the service
	 */
	public static Response getAllWaitingBookings() {
		Response r = makeRequest("/bookings/status/waiting", Method.GET, null, null);
		System.out.println("Get all waiting booking :" + r.getStatus());
		return r;
	}
	
	public static Response getAllOnBoardBookings(String vehicleId) {
		Response r = makeRequest("/bookings/status/onboard/"+vehicleId, Method.GET, null, null);
		System.out.println("Get all onboard booking :" + r.getStatus());
		return r;
	}
	/**
	 * Reqeust all active vehicle form Vehicle service
	 * @return the response of the serivice
	 */
	public static Response getAllVehicle() {
		Response r = makeRequest("/vehicles/all", Method.GET, null, null);
		System.out.println("Get all vehicle :" + r.getStatus());
		return r;
	}
	
	/**
	 * Request all the standing point from RoadNetwork service
	 * @return the Response of the service
	 */
	public static Response getStandingPoints() {
		Response r = makeRequest("/roadnetwork/standingpoint", Method.GET, null, null);
		System.out.println("Get standing :" + r.getStatus());
		return r;
	}
	
	/**
	 * Request all the standing point from RoadNetwork service
	 * @return the Response of the service
	 */
	public static Response getPickupPoints() {
		Response r = makeRequest("/roadnetwork/pickuppoint", Method.GET, null, null);
		System.out.println("Get pickup :" + r.getStatus());
		return r;
	}
	
	private static final String baseAddress = "http://gateway-optimusbus.router.default.svc.cluster.local/optimusbus";
	public static enum Method {GET, POST, PUT, DELETE}
	
	
}
