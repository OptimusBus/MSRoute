package model;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;


@XmlRootElement
public class Vehicle {
	
	public Vehicle() {}
	
	public Vehicle(String id) {
		this.vehicleId = id;
	}
	
	public Vehicle(String id, String sp, boolean isActive) {
		this.vehicleId = id;
		this.standingPoint = sp;
		this.isActive = isActive;
	}
	
	
	
	public Vehicle(String vehicleId, Location location, String standingPoint, boolean isActive, int maxOccupancy,
			int currentOccupancy, String vehicleToSupport, State state, Route route) {
		this.vehicleId = vehicleId;
		this.location = location;
		this.standingPoint = standingPoint;
		this.isActive = isActive;
		this.maxOccupancy = maxOccupancy;
		this.currentOccupancy = currentOccupancy;
		this.vehicleToSupport = vehicleToSupport;
		this.state = state;
		this.route = route;
	}

	public String getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getStandingPoint() {
		return standingPoint;
	}
	public void setStandingPoint(String standingPoint) {
		this.standingPoint = standingPoint;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public int getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	public int getCurrentOccupancy() {
		return currentOccupancy;
	}
	public void setCurrentOccupancy(int currentOccupancy) {
		this.currentOccupancy = currentOccupancy;
	}
	public String getVehicleToSupport() {
		return vehicleToSupport;
	}
	public void setVehicleToSupport(String vehicleToSupport) {
		this.vehicleToSupport = vehicleToSupport;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
	}
	public void addPassenger() {
		if(currentOccupancy < maxOccupancy) {
			this.currentOccupancy++;
		}
	}
	public void removePassenger() {
		if(currentOccupancy > 0) {
			this.currentOccupancy--;
		}
	}
	
	public static Vehicle decodeVehicle(Document d) {
		if(d.size() == 0) return null;
		String id = d.getString("vehicleId");
		String sp = d.getString("standingPoint");
		String vts = d.getString("vehicleToSupport");
		boolean ia = d.getBoolean("isActive", false);
		int mo = d.getInteger("maxOccupancy", 7);
		int co = d.getInteger("currentOccupancy", 0);
		State s = State.FREE;
		if(d.getString("state").equalsIgnoreCase("PRINCIPALE")) s = State.PRINCIPALE;
		else if(d.getString("state").equalsIgnoreCase("SUPPORTO")) s = State.SUPPORTO;
		else if(d.getString("state").equalsIgnoreCase("FUORISERVIZIO")) s = State.FUORISERVIZIO;
		Location l = Location.decodeLocation((Document)d.get("location"));
		Route r = Route.decodeRoute((Document)d.get("route"));
		return new Vehicle(id, l, sp, ia, mo, co, vts, s, r);
	}
	
	public static Document encodeVehicle(Vehicle v) {
		Document ve = new Document("vehicleId", v.getVehicleId());
		ve.append("location", Location.encodeLocation(v.getLocation()));
        ve.append("standingPoint", v.getStandingPoint());
        ve.append("state", v.getState().toString());
        ve.append("isActive", v.isActive());
        ve.append("route", Route.encodeRoute(v.getRoute()));
        ve.append("maxOccupancy", v.getMaxOccupancy());
        ve.append("currentOccupancy", v.getCurrentOccupancy());
        ve.append("vehicleToSupport", v.getVehicleToSupport());
		return ve;
	}
	
	private String vehicleId;
	private Location location = new Location();
	private String standingPoint = "";
	public static enum State { FREE, PRINCIPALE, SUPPORTO, FUORISERVIZIO }
	private boolean isActive = false;
	private int maxOccupancy = 7;
	private int currentOccupancy = 0;
	private String vehicleToSupport = "";
	private State state = State.FREE;
	private Route route = new Route();
}
