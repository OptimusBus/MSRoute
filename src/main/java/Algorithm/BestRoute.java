package Algorithm;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.bson.Document;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import innerconnector.HttpConnector;
import kmeans.Centroid;
import kmeans.EuclideanDistance;
import kmeans.KMeans;
import kmeans.Record;
import model.*;
import service.Branch;

@SuppressWarnings("deprecation")
public class BestRoute{
	
	public static void main(String[] args) {
		BestRoute m = new BestRoute();
		try {
			m.loadFiles();
			m.algo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BestRoute() {}
	
	public void loadFiles() throws Exception{
		String bok = "bookings.json";
		String veh = "vehicles.json";
		String pckp = "pckp.json";
		String stdp = "stdp.json";
		
		JSONParser jp = new JSONParser();
		FileReader r = new FileReader(bok);
		JSONArray jab = (JSONArray)jp.parse(r);
		r.close();
		
		r = new FileReader(veh);
		JSONArray jav = (JSONArray)jp.parse(r);
		r.close();
		
		r = new FileReader(pckp);
		JSONArray jap = (JSONArray)jp.parse(r);
		r.close();
		
		r = new FileReader(stdp);
		JSONArray jas = (JSONArray)jp.parse(r);
		r.close();
		
		for (Object j : jap) {
			String s = j.toString();
			Document d = Document.parse(s);
			Node n = Node.decodeIntersection(d);
			pickups.add(n);
		}
		for (Object j : jas) {
			String s = j.toString();
			Document d = Document.parse(s);
			Node n = Node.decodeIntersection(d);
			standings.add(n);
		}
		for (Object j : jav) {
			String s = j.toString();
			Document d = Document.parse(s);
			Vehicle v = Vehicle.decodeVehicle(d);
			vehicles.add(v);
		}
		for (Object j : jab) {
			String s = j.toString();
			Document d = Document.parse(s);
			String dep = d.get("departure").toString();
			String dest = d.get("destination").toString();
			Node depn = new Node();
			Node destn = new Node();
			for(Node n : pickups) {
				if(n.getNodeId().equals(dep))depn = n;
				if(n.getNodeId().equals(dest))destn = n;
			}
			Booking b = Booking.decodeBooking(d, depn, destn);
			bookings.add(b);
		}
	}
	
	/*
	 * https://api.mapbox.com/directions/v5/mapbox/driving/-122.39636,37.79129;-122.39732,37.79283;-122.39606,37.79349?annotations=maxspeed&overview=full&geometries=geojson&access_token=pk.eyJ1Ijoic2Vuc2VsZXNzbWl0ZSIsImEiOiJja3VmbTY0MjcxZXM1MnFtdHYwdW8zZnlmIn0.ou1Jnfl5Yrx60E9aQHNfsg
	 */
	
	public void algo() {
		Branch branch = new Branch();
		ArrayList<Node> waitNodes = getWaitingDeparture();
		ArrayList<Record> records = new ArrayList<>();
		for (Node n : waitNodes) {
			HashMap<String, Double> map = new HashMap<>();
			for(Vehicle v : vehicles) {
				Node vn = branch.getNearestNode(v.getLocation().getLatitude(), v.getLocation().getLongitude());
				int start = Integer.parseInt(vn.getNodeId());
				int dest = Integer.parseInt(n.getNodeId());
				map.put(v.getVehicleId(), -branch.getShortestRoute(start, dest).getLenght());
			}
			Record r = new Record(n.getNodeId(), map);
			records.add(r);
		}
		
		for (Record r  : records) {
			System.out.println("Node: "+r.getDescription());
			for(String s : r.getFeatures().keySet()) {
				System.out.println("\t"+s+": "+r.getFeatures().get(s));
			}
		}
		Map<Centroid, List<Record>> clusters = KMeans.fit(records, vehicles.size(), new EuclideanDistance(), 50);
		KMeans.printCluster(clusters);
		KMeans.saveCluster(clusters, "clusters.json");
	}
	
	
	public ArrayList<Node> getWaitingDeparture(){
		ArrayList<Node> waitpick = new ArrayList<Node>();
		for(Booking b : bookings) {
			if(b.getStatus().equals(Booking.Status.WAITING)){
				waitpick.add(b.getDeparture());
			}
		}
		return waitpick;
	}
	
	public ArrayList<Node> getWaitingDestination(){
		ArrayList<Node> waitpick = new ArrayList<Node>();
		for(Booking b : bookings) {
			if(b.getStatus().equals(Booking.Status.WAITING)){
				waitpick.add(b.getDestination());
			}
		}
		return waitpick;
	}
	
	public ArrayList<Node> getOnBoardDestination(){
		ArrayList<Node> onpick = new ArrayList<Node>();
		for(Booking b : bookings) {
			if(b.getStatus().equals(Booking.Status.ONBOARD)){
				onpick.add(b.getDestination());
			}
		}
		return onpick;
	}
	
	private ArrayList<Node> pickups = new ArrayList<Node>();
	private ArrayList<Node> standings = new ArrayList<Node>();
	private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
	private ArrayList<Booking> bookings = new ArrayList<Booking>();
	private static final double MAX_DISTANCE = 999999999999.0;
}
