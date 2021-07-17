package Model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Location {
	public Location(double lati, double longi) {
		latitute = lati;
		longitude = longi;
	}
	public double getLatitute() {
		return latitute;
	}
	public void setLatitute(double latitute) {
		this.latitute = latitute;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double[] getCoordinates() {
		coordinates[0] = latitute;
		coordinates[1] = longitude;
		return coordinates;
	}
	private double latitute;
	private double longitude;
	private double coordinates[];
}

