package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;
/**
 * Implements the Route model for the service
 * The route is characterized by the id of the assigned Vehicle,
 * the route size in number of Node,
 * the List of Node of the route and the lenght of the route
 *@class Route
 */
@XmlRootElement
public class Route {
	
	public Route(){}
	
	/**
	 * Generate a Route object
	 * @param vehicleId the id of the assigned Vehicle
	 * @param route the List of Node of the route
	 * @param lenght the lenght of the route
	 */
	public Route(String vehicleId, Map<Integer, Node> route, double lenght){
		this.route = route;
		this.size = route.size();
		this.vehicleId = vehicleId;
		this.lenght = lenght;
	}
	
	public Route(String vehicleId, Map<Integer, Node> route){
		this.route = route;
		this.size = route.size();
		this.vehicleId = vehicleId;
		this.lenght = 0;
	}
	
	/**
	 * Return all the Node of type PICKUPPOINT inside the route
	 * @return a List<Node>
	 */
	/*
	public List<Node> getPickUps() {
		ArrayList<Node> picks = new ArrayList<Node>();
		for(Node n : this.route) {
			if(n.getType() == Node.Type.PICKUPPOINT);
			picks.add(n);
		}
		return picks;
	}*/
	
	/**
	 * Return all the Node of type STANDINGPOINT inside the route
	 * @return a List<Node>
	 */
	/*public List<Node> getStandings() {
		ArrayList<Node> stand = new ArrayList<Node>();
		for(Node n : this.route) {
			if(n.getType() == Node.Type.STANDINGPOINT);
			stand.add(n);
		}
		return stand;
	}*/
	
	/**
	 * Get the size of the Route as a number of Node
	 * @return the number of Node inside the route
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Get the List of Node inside the Route
	 * @return a List<Node>
	 */
	public Map<Integer, Node> getRoute() {
		return this.route;
	}

	/**
	 * Set the List of Node for the route
	 * @param route the new List of Node
	 */
	public void setRoute(Map<Integer, Node> route) {
		this.route = route;
		this.size = this.route.size();
	}
	
	/**
	 * Get the vehicleId of the vehicle assigned to the route
	 * @return the id of the vehicle
	 */
	public String getVehicleId() {
		return vehicleId;
	}

	/**
	 * Set the id of the vehicle assigned to the route
	 */
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	
	/**
	 * Get the lenght of the route
	 * @return the lenght of the route
	 */
	public double getLenght() {
		return this.lenght;
	}
	
	/**
	 * Encode a Route object in to a bson.Document object
	 * @param r the Route to be converted
	 * @return a bson.Document
	 */
	public static Document encodeRoute(Route r) {
		Document d = new Document();
		d.append("vehicleId", r.getVehicleId());
		d.append("size", r.getSize());
		d.append("lenght", r.getLenght());
		int i = 0;
		for(Integer n : r.getRoute().keySet()) {
			d.append(String.valueOf(i), Node.encodeNode(r.getRoute().get(n)));
			i++;
		}
		return d;
	}
	
	/**
	 * Decode a bson.Document in to a Route object
	 * @param d the Document to be converted
	 * @return a Route object
	 */
	public static Route decodeRoute(Document d) {
		if(d == null)return null;
		if(d.size()==0)return null;
		String veId = d.getString("vehicleId");
		int size = 0;
		if(d.getInteger("size")!=null)size = d.getInteger("size");
		Map<Integer, Node> path = new HashMap<>();
		for(int i = 0; i < size; i++) {
			path.put(i, (Node.decodeNode((Document)d.get(String.valueOf(i)))));
		}
		double l = 0;
		return new Route(veId, path, l);
	}
	
	/**
	 * Print Route
	 */
	public void printRoute() {
		Document d = Route.encodeRoute(this);
		System.out.println(d.toJson());
	}

	private Map<Integer, Node> route;
	private String vehicleId;
	private int size;
	private double lenght;
	
	
}
