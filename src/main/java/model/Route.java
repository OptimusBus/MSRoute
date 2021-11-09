package model;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class Route {
	
	public Route(){}
	
	public Route(String vehicleId, List<Node> route, double lenght){
		this.route = route;
		this.size = route.size();
		this.vehicleId = vehicleId;
		this.lenght = 0;
	}
	
	public List<Node> getPickUps() {
		ArrayList<Node> picks = new ArrayList<Node>();
		for(Node n : this.route) {
			if(n.getType() == Node.Type.PICKUPPOINT);
			picks.add(n);
		}
		return picks;
	}
	
	public List<Node> getStandings() {
		ArrayList<Node> stand = new ArrayList<Node>();
		for(Node n : this.route) {
			if(n.getType() == Node.Type.STANDINGPOINT);
			stand.add(n);
		}
		return stand;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public List<Node> getRoute() {
		return this.route;
	}

	public void setRoute(List<Node> route) {
		this.route = route;
		this.size = this.route.size();
	}
	
	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	
	public static Document encodeRoute(Route r) {
		Document d = new Document();
		d.append("vehicleId", r.getVehicleId());
		d.append("size", r.getSize());
		d.append("lenght", r.getLenght());
		int i = 0;
		for(Node n : r.getRoute()) {
			d.append(String.valueOf(i), Node.encodeNode(n));
			i++;
		}
		return d;
	}
	
	public double getLenght() {
		return this.lenght;
	}

	public static Route decodeRoute(Document d) {
		if(d.size()<0)return null;
		String veId = d.getString("vehicleId");
		int size = d.getInteger("size");
		ArrayList<Node> route = new ArrayList<Node>();
		for(int i = 0; i < size; i++) {
			route.add(Node.decodeNode((Document)d.get(String.valueOf(i))));
		}
		double lenght = d.getDouble("lenght");
		return new Route(veId, route, lenght);
	}

	private List<Node> route;
	private String vehicleId;
	private int size;
	private double lenght;
	
	
}
