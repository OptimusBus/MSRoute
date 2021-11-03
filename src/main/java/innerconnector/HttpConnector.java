package innerconnector;

import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;

public class HttpConnector {

	public HttpConnector() {}
	
	
	
	private static Response makeRequest(String baseAddress, String url, Method m, String params) {
		WebClient client = WebClient.create(baseAddress);
		client.accept("application/json");
		client.type("application/json");
		client.path(url);
		switch(m) {
			case GET:
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
	
	public static Response getShortestPath(int source, int dest) {
		return makeRequest(roadNetworkAddr,"shortestPath?source="+source+"&dest="+dest, Method.GET, null);
	}
	public static Response getNodeById(String id) {
		return makeRequest(roadNetworkAddr,""+id, Method.GET, null);
	}
	public static Response getNodeByCoordinate(double lat, double lon) {
		return makeRequest(roadNetworkAddr,"nearestNode?latitude="+lat+"&longitude="+lon, Method.GET, null);
	}
	public static Response getStreetbyId(String id) {
		return makeRequest(roadNetworkAddr,"street/"+id, Method.GET, null);
	}
	public static Response getStreet(int start, int dest) {
		return makeRequest(roadNetworkAddr,"street?start="+start+"&dest="+dest, Method.GET, null);
	}
	
	private static final String roadNetworkAddr="";//Address of RoadNetworkService
	private static final String bookingAddr = "";
	private static final String vehicleAddr = "";
	public static enum Method {GET, POST, PUT, DELETE}
	
}
