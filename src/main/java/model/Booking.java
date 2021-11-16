package model;

import java.time.Instant;

import javax.xml.bind.annotation.XmlRootElement;

import org.bson.Document;

@XmlRootElement
public class Booking {
	

	public Booking(Node departure, Node destination, String bookingId, String passengerId, String vehicleId, int code,
			boolean isConfirmed) {
		super();
		this.departure = departure;
		this.destination = destination;
		this.bookingId = bookingId;
		this.passengerId = passengerId;
		this.vehicleId = vehicleId;
		this.code = code;
		this.isConfirmed = isConfirmed;
	}
	
	public Booking(Node departure, Node destination, String bookingId, String passengerId, String vehicleId, int code,
			boolean isConfirmed, String timestamp) {
		super();
		this.departure = departure;
		this.destination = destination;
		this.bookingId = bookingId;
		this.passengerId = passengerId;
		this.vehicleId = vehicleId;
		this.code = code;
		this.isConfirmed = isConfirmed;
		this.timestamp = timestamp;
		this.status= Booking.Status.CREATED;
	}
	
	public Booking(Node departure, Node destination, String bookingId, String passengerId, String vehicleId, int code,
			boolean isConfirmed, String timestamp,Status status) {
		super();
		this.departure = departure;
		this.destination = destination;
		this.bookingId = bookingId;
		this.passengerId = passengerId;
		this.vehicleId = vehicleId;
		this.code = code;
		this.isConfirmed = isConfirmed;
		this.timestamp = timestamp;
		this.status= status;
	}
	
	
	public Booking() {}
	

	public Node getDeparture() {
		return departure;
	}
	public void setDeparture(Node departure) {
		this.departure = departure;
	}
	public Node getDestination() {
		return destination;
	}
	public void setDestination(Node destination) {
		this.destination = destination;
	}
	public String getBookingId() {
		return bookingId;
	}
	public String setBookingId(String bookingId) {
		return bookingId;
	}
	public String getPassengerId() {
		return passengerId;
	}
	public String setPassengerId(String passengerId) {
		
		return passengerId;
	}
	public String getVehicleId() {
		return vehicleId;
	}
	public String setVehicleId(String vehicleId) {
		
		return bookingId;
	}
	public int getCode() {
		return code;
	}
	public int setCode(int code) {
		return this.code = code;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public boolean isConfirmed() {
		return isConfirmed;
	}
	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}
	
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public static Booking decodeBooking(Document d) {
		if(d.size()==0) return null;
		String bid=d.getString("bookingId");
		String pid=d.getString("passengerId");
		String vid=d.getString("vehicleId");
		int code=d.getInteger("code", 0);
		boolean ic=d.getBoolean("isConfirmed",false);
		Node departure=Node.decodeNode((Document)d.get("departure"));
		Node destination=Node.decodeNode((Document)d.get("destination"));
		String timestamp=d.getString("timestamp");
		Status s;
		if(d.getString("status").equalsIgnoreCase("CREATED")) s=Booking.Status.CREATED;
		else if(d.getString("status").equalsIgnoreCase("ONBOARD")) s=Booking.Status.ONBOARD;
		else if(d.getString("status").equalsIgnoreCase("WAITING")) s=Booking.Status.WAITING;
		else if(d.getString("status").equalsIgnoreCase("CLOSED")) s=Booking.Status.CLOSED;
		else s=Booking.Status.CANCELED;
					
		return new Booking(departure, destination, bid, pid, vid, code, ic, timestamp, s);
	}
	
	public static Document encodeBooking(Booking b) {
		Document v=new Document("bookingId",b.getBookingId());
		v.append("passengerId", b.getPassengerId());
		v.append("departure", Node.encodeNode(b.getDeparture()));
		v.append("destination",Node.encodeNode(b.getDestination()));
		v.append("vehicleId", b.getVehicleId());
		v.append("code", b.getCode());
		v.append("timestamp", b.getTimestamp());
		v.append("isConfirmed", b.isConfirmed());
		v.append("status", b.getStatus().toString());
		return v;
	}
	
	public static Booking decodeBooking(Document d, Node dep, Node dest ) {
		if(d.size()==0) return null;
		String bid=d.getString("bookingId");
		String pid=d.getString("passengerId");
		String vid=d.getString("vehicleId");
		int code=d.getInteger("code", 0);
		boolean ic=d.getBoolean("isConfirmed",false);
		String timestamp=d.getString("timestamp");
		Status s;
		if(d.getString("status").equalsIgnoreCase("CREATED")) s=Booking.Status.CREATED;
		else if(d.getString("status").equalsIgnoreCase("ONBOARD")) s=Booking.Status.ONBOARD;
		else if(d.getString("status").equalsIgnoreCase("WAITING")) s=Booking.Status.WAITING;
		else if(d.getString("status").equalsIgnoreCase("CLOSED")) s=Booking.Status.CLOSED;
		else s=Booking.Status.CANCELED;
					
		return new Booking(dep, dest, bid, pid, vid, code, ic, timestamp, s);
	}
	
	public Node departure;
	public Node destination;
	private String bookingId;
	private String passengerId;
	private String vehicleId;
	private int code;
	private String timestamp=""+Instant.now().getEpochSecond();
	private boolean isConfirmed=false;
	public static enum Status { CREATED, ONBOARD, CANCELED, CLOSED, WAITING }
	private Status status;
	
	
}
