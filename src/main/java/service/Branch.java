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

	/**
	 * Return the assigned route for the vehicle
	 * @param vehicleId the id of the vehicle requesting the route
	 * @return	a Route object
	 */
	@Override
	public Route getRoute(String vehicleId) {
		Document d = mdb.getRouteByVehicleId(vehicleId);
		if(d.size()<0)return null;
		return Route.decodeRoute(d);
	}
	
	/**
	 * Save the route on database
	 * @param r the Route object to be saved
	 * @return the result of the operation (true if succeded or false if failed)
	 */
	@Override
	public boolean saveRoute(Route r) {
		mdb.saveRoute(r);
		if(mdb.getRouteByVehicleId(r.getVehicleId())== null)return false;
		return true;
	}
	
	/**
	 * Delete a route from the database
	 * @param vehicleId the id of the vehicle assigned to the desired route
	 * @return the result of the operation (true if succeded or false if failed)
	 */
	@Override
	public boolean deleteRoute(String vehicleId) {
		return mdb.removeRoute(vehicleId);
		return true;
	}
	
	/**
	 * Get the nearest node from given coordinates
	 * @param lat latitude
	 * @param lon longitude
	 * @return the closest Node object for given coordinates (null if there is a problem)
	 */
	@Override
	public Node getNearestNode(double lat, double lon) {
		Response r = HttpConnector.getNodeByCoordinate(lat, lon);
		if(r.getStatus() != 200)return null;
		String s = r.readEntity(String.class);
		Document d = Document.parse(s);
		return Node.decodeNode(d);
	}
	
	/**
	 * Return the value of the shortestPath from two node.
	 * The value is calculated interrogating the RoadNetwor service to get the path as a list
	 * of Street object. Then the weight of the path is calculated as sum of the single value of the parameter
	 * lenght of the Street object.
	 * @param start the id of the starting Node
	 * @param dest the id of the destination Node
	 * @return the value of the shortestpath (return 0 if there is a problem)
	 */
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
	
	/**
	 * Get the shortestPath for two node as a list of node
	 * @param start the id of the starting node
	 * @param dest the id of the destination node
	 * @return a the shortestPath as a list of Node
	 */
	@Override
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
	
	private MongoConnector mdb = new MongoConnector();

}
