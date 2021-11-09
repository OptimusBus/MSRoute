package service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.Response;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

import db.MongoConnector;
import innerconnector.HttpConnector;
import model.Booking;
import model.Location;
import model.Node;
import model.Route;
import model.Street;
import model.Vehicle;

public class Branch implements BranchLocal {

	@Override
	public Route getRoute(String vehicleId) {
		Document d = mdb.getRouteByVehicleId(vehicleId);
		if(d.size()<0)return null;
		return Route.decodeRoute(d);
	}

	@Override
	public boolean saveRoute(Route r) {
		Document d = Route.encodeRoute(r);
		mdb.saveRoute(r);
		if(mdb.getRouteByVehicleId(r.getVehicleId())== null)return false;
		return true;
	}

	@Override
	public boolean deleteRoute(String vehicleId) {
		return mdb.removeRoute(vehicleId);
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
	public Route getShortestRoute(int start, int dest) {
		Response r = HttpConnector.getShortestPath(start, dest);
		if(r.getStatus() != 200)return null;
		String s = r.readEntity(String.class);
		ArrayList<BasicDBObject> l = (ArrayList<BasicDBObject>) JSON.parse(s);
		Iterator<BasicDBObject> i = l.iterator();
		ArrayList<Node> path=new ArrayList<Node>();
		while(i.hasNext()) {
			Document d = new Document(i.next());
			Node n = Node.decodeNode(d);
			if(n!=null) {
				path.add(n);
			}else {
				path.add(Node.decodeIntersection(d));
			}
			
		}
		double lenght = 0.0;
		for(int c = 0; c < path.size()-1; c++) {
			int str = Integer.parseInt(path.get(c).getNodeId());
			int dst = Integer.parseInt(path.get(c+1).getNodeId());
			Response rs = HttpConnector.getStreet(str, dst);
			if(r.getStatus() != 200)return null;
			Document d = Document.parse(rs.readEntity(String.class));
			double len = Street.decodeStreet(d).getLenght();
			lenght += len;
		}
		return new Route("", path, lenght);
	}
	
	private MongoConnector mdb = new MongoConnector();


}
