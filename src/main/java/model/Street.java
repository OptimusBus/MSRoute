package model;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class Street {
	
	public Street(String id, int from, int to, int speedlimit, String name, double weight, double lenght, double ffs) {
		this.linkid = id;
		this.speedlimit = speedlimit;
		this.name = name;
		this.weight = weight;
		this.lenght = lenght;
		this.ffs = ffs;
	}
	public String getId() {
		return linkid;
	}
	public void setId(String id) {
		this.linkid = id;
	}
	public int getSpeedlimit() {
		return speedlimit;
	}
	public void setSpeedlimit(int speedlimit) {
		this.speedlimit = speedlimit;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getFfs() {
		return ffs;
	}
	public void setFfs(double ffs) {
		this.ffs = ffs;
	}
	public String getLinkid() {
		return linkid;
	}
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
	public double getLenght() {
		return lenght;
	}
	public void setLenght(double lenght) {
		this.lenght = lenght;
	}
	public int getFrom() {
		return from;
	}
	public int getTo() {
		return to;
	}
	public static Street decodeStreet(Document d) {
		if(d.size() == 0) return null;
		
		String linkid= d.getString("linkid");
		int from = d.getInteger("from");
		int to = d.getInteger("to");
		int speedlimit=d.getInteger("speedlimit");
		String name=d.getString("name");
		double lenght=d.getDouble("lenght");
		double weight=d.getDouble("weight");
		double ffs=d.getDouble("ffs");
		
		return new Street(linkid,from,to,speedlimit,name,lenght,weight,ffs);
	}
	
	public static Document encodeStreet(Street n) {
		Document d= new Document();
		d.append("linkid", n.getLinkid());
		d.append("from", n.getFrom());
		d.append("to", n.getTo());
		d.append("speedlimit", n.getSpeedlimit());
		d.append("name", n.getName());
		d.append("lenght",n.getLenght());
		d.append("weight", n.getWeight());
		d.append("ffs", n.getFfs());
		
		return d;
	}

	private String linkid;
	private int from;
	private int to;
	private int speedlimit;
	private String name;
	private double lenght;
	private double weight;
	private double ffs;
	
	

}
