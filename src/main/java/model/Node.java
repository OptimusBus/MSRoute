package model;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;


@XmlRootElement

public class Node {
	public Node(String nodeId, Location location, Type type, double potentialDemand) {
		this.location = location;
		this.nodeId = nodeId;
		this.type = type;
		this.potentialDemand = potentialDemand;
	}
	
	public Node() {
		super();
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
	
	public static Node decodeNode(Document d) {
		if(d.size() == 0) return null;
		
		String id = d.getString("nodeId");
		Location l = Location.decodeLocation((Document) d.get("location"));
		double pd = d.getDouble("potentialDemand");
		Type t;
		if(d.getString("type").equalsIgnoreCase("PICKUPPOINT")) t = Node.Type.PICKUPPOINT;
		else if(d.getString("type").equalsIgnoreCase("STANDINGPOINT")) t = Node.Type.STANDINGPOINT;
		else t = Node.Type.TRANSITION;
		return new Node(id, l, t, pd);
	}
	
	
	public static Node decodeIntersection(Document d) {
		if(d.size()==0) return null;
		Location l=Location.decodeLocation((Document) d.get("coordinate"));
		String nid=d.getString("osmid");
		double pd=-1;
		return new Node(nid,l, Node.Type.TRANSITION, pd);
	}
	
	public static Document encodeNode(Node n) {
		Document d = new Document();
		d.append("nodeId", n.getNodeId());
		d.append("location", Location.encodeLocation(n.getLocation()));
		d.append("potentialDemand", n.getPotentialDemand());
		d.append("type", n.getType().toString());
		return d;
	}

	private Location location;
	private String nodeId;
	private double potentialDemand;
	public static enum Type { STANDINGPOINT, PICKUPPOINT, TRANSITION }
	private Type type;
}

