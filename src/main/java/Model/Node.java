package Model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement

public class Node {
	public Node(String nodeId, Location location, Type type, double potentialDemand) {
		this.location = location;
		this.nodeId = nodeId;
		this.type = type;
		this.potentialDemand = potentialDemand;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public double getPotentialDemand() {
		return potentialDemand;
	}
	public void setPotentialDemand(double potentialDemand) {
		this.potentialDemand = potentialDemand;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}

	private Location location;
	private String nodeId;
	private double potentialDemand;
	private static enum Type { STANDINGPOINT, PICKUPPOINT, TRANSITION }
	private Type type;
}

