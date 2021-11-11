package service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import db.MongoConnector;
import innerconnector.HttpConnector;
import logger.StatusLogger;
import model.Booking;
import model.Location;
import model.Node;
import model.Route;
import model.Street;
import model.Vehicle;

public class Branch implements BranchLocal {

	@Override
	public Route getRoute(String vehicleId) {
		//Document d = mdb.getRouteByVehicleId(vehicleId);
		//if(d.size()<0)return null;
		//return Route.decodeRoute(d);
		return null;
	}

	@Override
	public boolean saveRoute(Route r) {
		Document d = Route.encodeRoute(r);
		//mdb.saveRoute(r);
		//if(mdb.getRouteByVehicleId(r.getVehicleId())== null)return false;
		return true;
	}

	@Override
	public boolean deleteRoute(String vehicleId) {
		//return mdb.removeRoute(vehicleId);
		return false;
	}
	
	@Override
	public Route bestRoute(String vehicleId) {
		return null;
	}
	

	@Override
	public Node getNearestNode(double lat, double lon) {
		Response r = HttpConnector.getNodeByCoordinate(lat, lon);
		if(r.getStatus() != 200)return null;
		String s = r.readEntity(String.class);
		Document d = Document.parse(s);
		return Node.decodeNode(d);
	}
	
	@Override
	public double getShortestStreet(int start, int dest) {
		Response r = HttpConnector.getShortestStreet(start, dest);
		if(r.getStatus()!=200)return 0;
		String s = r.readEntity(String.class);
		BsonArray list = BsonArray.parse(s);
		Iterator<BsonValue> i = list.iterator();
		ArrayList<Street> streets = new ArrayList<>();
		while(i.hasNext()) {
			Document d = Document.parse(i.next().toString());
			Street str = Street.decodeStreet(d);
			if(str == null)return 0;
			streets.add(str);
		}
		r.close();
		double length = 0;
		for(Street str : streets) {
			length += str.getLenght();
		}
		return length;
	}
	
	public List<Node> getShortestPath(int start, int dest){
		Response r = HttpConnector.getShortestPath(start, dest);
		if(r.getStatus() != 200)return null;
		String s = r.readEntity(String.class);
		BsonArray list = BsonArray.parse(s);
		Iterator<BsonValue> i = list.iterator();
		ArrayList<Node> path = new ArrayList<Node>();
		while(i.hasNext()) {
			Document d = Document.parse(i.next().toString());
			Node n = Node.decodeNode(d);
			if(n == null)return null;
			path.add(n);
		}
		r.close();
		return path;
	}
	
	private static final double MAXVALUE = 99999999999999999.0;
	//private MongoConnector mdb = new MongoConnector();

}
