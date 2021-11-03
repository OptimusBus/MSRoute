package model;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class Location {
	public Location(double lati, double longi) {
		latitude = lati;
		longitude = longi;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitute) {
		this.latitude = latitute;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double[] getCoordinates() {
		coordinates[0] = latitude;
		coordinates[1] = longitude;
		return coordinates;
	}
	public static Location decodeLocation(Document d) {
		if(d.get("location") != null) d = (Document) d.get("location");
		if(d.getDouble("latitude") == null || d.getDouble("longitude") == null) return null;
		double lati = d.getDouble("latitude");
		double longi = d.getDouble("longitude");
		return new Location(lati, longi);
	}
	public static Document encodeLocation(Location l) {
		Document d = new Document();
		if(l.getLatitude() == -1 || l.getLongitude() == -1) {
			d.append("location", new Document());
		}else {
			d.append("location", new Document("longitude", l.getLongitude()).append("latitude", l.getLatitude()));
		}
		return d;
	}
	private double latitude;
	private double longitude;
	private double coordinates[];
}

