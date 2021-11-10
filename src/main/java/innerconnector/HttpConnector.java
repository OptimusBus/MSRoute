package innerconnector;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

import innerconnector.HttpConnector.Method;

public class HttpConnector {

	public HttpConnector() {}
	
	
	
	private static Response makeRequest(String baseAddress, String url, Method m, Map<String,String> queryParam, String params) {	
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
	
	public static Response getShortestPath(Integer source, Integer dest) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("source", source.toString());
		param.put("dest", dest.toString());
		Response r = makeRequest(roadNetworkAddr,"shortestPath", Method.GET, param,  null);
		System.out.println(r.getStatus());
		return r;
	}
	public static Response getNodeById(String id) {
		return makeRequest(roadNetworkAddr,""+id, Method.GET, null, null);
	}
	public static Response getNodeByCoordinate(Double lat, Double lon) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("latitude", lat.toString());
		param.put("longitude", lon.toString());
		return makeRequest(roadNetworkAddr,"nearestNode", Method.GET, param, null);
	}
	public static Response getStreetbyId(String id) {
		return makeRequest(roadNetworkAddr,"street/"+id, Method.GET, null, null);
	}
	public static Response getStreet(Integer start, Integer dest) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("start", start.toString());
		param.put("dest", dest.toString());
		return makeRequest(roadNetworkAddr,"street", Method.GET, param, null);
	}
	public static Response getVehicle(String id) {
		return makeRequest(vehicleAddr, "vehicles/"+id, Method.GET, null, null);
	}
	public static Response getActiveBookings() {
		return null;
	}
	public static Response getBookingsByVehicle(String vehicleId) {
		return null;
	}
	
	private static final String roadNetworkAddr="http://127.0.0.1:8080/MSRoadNetwork/RoadNetworkApplication-1.0/roadNetwork/";//Address of RoadNetworkService
	private static final String bookingAddr = "http://127.0.0.1:8080/MSRoadNetwork/RoadNetworkApplication-1.0/roadNetwork/street?start=16&dest=115239";
	private static final String vehicleAddr = "";
	public static enum Method {GET, POST, PUT, DELETE}
	
}
