package algorithm;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.Document;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import kmeans.Centroid;
import kmeans.EuclideanDistance;
import kmeans.KMeans;
import kmeans.Record;
import model.*;
import service.Branch;

/**
 * The class implementing the Routing algorithm
 * @class BestRoute
 */
public class BestRoute{
	
	/**
	 * Execute the algorithm locally (for testing purpose)
	 */
	public static void main(String[] args) {
		BestRoute m = new BestRoute();
		try {
			m.loadFiles();
			m.parallelAlgo();
			//m.algo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BestRoute() {}
	
	public BestRoute(ArrayList<Vehicle> vehicles, ArrayList<Booking> bookings, ArrayList<Node> pickups) {
		this.vehicles = vehicles;
		this.bookings = bookings;
		this.pickups = pickups;
	}
	
	public BsonArray getClusterResult() {
		return this.clusterResult;
	}
	
	/**
	 * Load example data from files (for testing purpose)
	 * @throws Exception
	 */
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
			Node n = Node.decodeIntersection(d, Node.Type.PICKUPPOINT);
			pickups.add(n);
		}
		for (Object j : jas) {
			String s = j.toString();
			Document d = Document.parse(s);
			Node n = Node.decodeIntersection(d, Node.Type.STANDINGPOINT);
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
	
	/**
	 * The algorithm execute a clusterization of the waiting booking to the available vehicle.
	 * Then he recalculate the Route for each vehicle adding the new Node checking the availability of seats
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
				System.out.println("Request for nodes: " + start + " " + dest);
				double r = branch.getShortestStreet(start, dest);
				map.put(v.getVehicleId(), - r);
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
		Map<Centroid, List<Record>> clusters = KMeans.fit(records, vehicles.size(), new EuclideanDistance(), 10);
		KMeans.printCluster(clusters);
		KMeans.saveCluster(clusters, "clusters.json");
	}
	
	/**
	 * The parallel version of the algorithm to determinate the shortest path for all vehicle
	 * The algorithm execute a clusterization of the waiting booking to the available vehicle.
	 * Then he recalculate the Route for each vehicle adding the new Node checking the availability of seats
	 * @throws InterruptedException
	 */
	public List<Route> parallelAlgo() throws InterruptedException {
		/*
		 * Request all resuces from Booking service e Vehicle service
		 */
		
		if(!this.getAllData())return null;
		
		List<Node> wn = getWaitingDeparture();
		ArrayList<Record> records = new ArrayList<>();
		ThreadGroup tg = new ThreadGroup("records");
		ArrayList<ParallelPath> threads = new ArrayList<>();
		/*
		 * Generate a sub set of element for each thread
		 */
		int size = wn.size();
		List<Node> sub1 = wn.subList(0, (size/2));
		List<Node> sub2 = wn.subList(size/2, size);
		size = sub1.size();
		List<Node> sub1_1 = sub1.subList(0, (size/2));
		List<Node> sub1_2 = sub1.subList(size/2, size);
		size = sub2.size();
		List<Node> sub2_1 = sub2.subList(0, (size/2));
		List<Node> sub2_2 = sub2.subList(size/2, size);
		
		/*
		 * Create 4 thread
		 */
		threads.add(new ParallelPath(tg, vehicles, sub1_1));
		threads.add(new ParallelPath(tg, vehicles, sub1_2));
		threads.add(new ParallelPath(tg, vehicles, sub2_1));
		threads.add(new ParallelPath(tg, vehicles, sub2_2));
		
		for(ParallelPath p : threads) {
			p.start();
			p.join();
			records.addAll(p.getRecords());
		}
		tg.destroy();
		
		// Print the recods for the KMeans algorithm
		for (Record r  : records) {
			System.out.println("Node: "+r.getDescription());
			for(String s : r.getFeatures().keySet()) {
				System.out.println("\t"+s+": "+r.getFeatures().get(s));
			}
		}
		//execute the clusterization
		Map<Centroid, List<Record>> clusters = KMeans.fit(records, vehicles.size(), new EuclideanDistance(), 20);
		KMeans.printCluster(clusters);
		/*
		 * With the result of the clusterization now the new route for the vehicle will be calculated
		 * First a map containing the vehicle and all the nodes assigned to it is constructed.
		 * The map (data) contains all the node assigned to the vehicle from the clusterization and all the node
		 * present in the old route of the vehicle that the vehicle hasn't visited
		 */
		this.clusterResult = BsonArray.parse(KMeans.returnCluster(clusters).toJSONString());
		Iterator<BsonValue> i = this.clusterResult.iterator();
		Map<Vehicle, List<Node>> data = new HashMap<>();
		while(i.hasNext()) {
			Document d = Document.parse(i.next().toString());
			Vehicle v = this.getVehicle(d.getString("vehicleId"));
			List<String> nodesId = (List<String>) d.get("departure");
			List<Node> nodes = new ArrayList<Node>();
			for(String id : nodesId) {
				nodes.add(this.getNode(id));
			}
			Route r = v.getRoute();
			if(r != null) {
				nodes.addAll(this.filterAlreadyVisited(r.getRoute(), v));
			}
			data.put(v, nodes);
		}
		/*
		 * Calcoliamo gli shortest path per i nodi assegnati al veicolo
		 * Per non ricalcolare quelli già presi per i nodi da clusterizzare li ho presi dai record creati per il cluster
		 * Quelli che facevano parte già della route del veicolo invece facciamo lo shortest path
		 * 
		 * Per allegerire la cosa avevo pensato di sgravare il server da calcolare lo shortest path totale e assegnare
		 * alla route del veicolo solo i waypoint ordinati per distanza e lasciare al client il compito di tracciare il
		 * path totale
		 * 
		 */
		Branch branch = new Branch();
		List<Route> routes = new ArrayList<>();
		for(Vehicle v : data.keySet()) {
			List<Node> nodes = data.get(v);
			Map<Node, Double> wayPoint = new HashMap<>();
			for(Node n : nodes) {
				for(Record r : records) {
					if(r.getDescription().equals(n.getNodeId())) {
						wayPoint.put(n, - r.getFeatures().get(v.getVehicleId()));
					}else {
						wayPoint.put(n, -1.0);
					}
				}
			}
			for(Node n : wayPoint.keySet()) {
				double leng = wayPoint.get(n);
				if(leng <= 0) {
					int start = Integer.parseInt(
							branch.getNearestNode(v.getLocation().getLatitude(), v.getLocation().getLongitude()).getNodeId());
					int end = Integer.parseInt(n.getNodeId());
					wayPoint.put(n, branch.getShortestStreet(start, end ));
				}
			}
			wayPoint = BestRoute.sortedByValue(wayPoint);
			List<Node> nodes2 = new ArrayList<Node>();
			for(Node n : wayPoint.keySet()) {
				nodes2.add(n);
			}
			Route r = new Route(v.getVehicleId(), nodes2);
			routes.add(r);
		}
		
		return routes;
		
	}
	
	/**
	 * The parallel version of the algorithm to determinate the shortest path for all vehicle
	 * The algorithm execute a clusterization of the waiting booking to the available vehicle.
	 * @throws InterruptedException
	 */
	public List<Route> parallelAlgo2() throws InterruptedException {
		/*
		 * Request all resuces from Booking service e Vehicle service
		 */
		
		if(!this.getAllData())return null;
		System.out.println("Data retrived from servicies");
		List<Node> wn = getWaitingDeparture();
		ArrayList<Record> records = new ArrayList<>();
		ThreadGroup tg = new ThreadGroup("records");
		/*ArrayList<ParallelPath> threads = new ArrayList<>();
		System.out.println("Starting clusterization");
		int size = wn.size();
		System.out.println(wn.size());
		List<Node> sub1 = wn.subList(0, (size/2));
		List<Node> sub2 = wn.subList(size/2, size);
		size = sub1.size();
		List<Node> sub1_1 = sub1.subList(0, (size/2));
		List<Node> sub1_2 = sub1.subList(size/2, size);
		size = sub2.size();
		List<Node> sub2_1 = sub2.subList(0, (size/2));
		List<Node> sub2_2 = sub2.subList(size/2, size);
		
		threads.add(new ParallelPath(tg, vehicles, sub1_1));
		threads.add(new ParallelPath(tg, vehicles, sub1_2));
		threads.add(new ParallelPath(tg, vehicles, sub2_1));
		threads.add(new ParallelPath(tg, vehicles, sub2_2));*/
		
		ParallelPath p = new ParallelPath(tg, vehicles, wn);
		p.start();
		p.join();
		records.addAll(p.getRecords());
		tg.destroy();
		
		// Print the recods for the KMeans algorithm
		for (Record r  : records) {
			System.out.println("Node: "+r.getDescription());
			for(String s : r.getFeatures().keySet()) {
				System.out.println("\t"+s+": "+r.getFeatures().get(s));
			}
		}
		//execute the clusterization
		if(records.isEmpty())return null;
		Map<Centroid, List<Record>> clusters = KMeans.fit(records, vehicles.size(), new EuclideanDistance(), 20);
		KMeans.printCluster(clusters);
		/*
		 * With the result of the clusterization now the new route for the vehicle will be calculated
		 * First a map containing the vehicle and all the nodes assigned to it is constructed.
		 * The map (data) contains all the node assigned to the vehicle from the clusterization and all the node
		 * present in the old route of the vehicle that the vehicle hasn't visited
		 */
		System.out.println("End of clusterization");
		this.clusterResult = BsonArray.parse(KMeans.returnCluster(clusters).toJSONString());
		System.out.println(this.clusterResult.toString());
		Iterator<BsonValue> i = this.clusterResult.iterator();
		Map<Vehicle, List<Node>> data = new HashMap<>();
		System.out.println("Starting shortest path");
		while(i.hasNext()) {
			Document d = Document.parse(i.next().toString());
			Vehicle v = this.getVehicle(d.getString("vehicleID"));
			System.out.println(d.get("departure"));
			List<String> nodesId = (List<String>) d.get("departure");
			List<Node> nodes = new ArrayList<Node>();
			for(String id : nodesId) {
				nodes.add(this.getNode(id));
			}
			nodes.addAll(this.getWaitingDestination(nodes));
			if(v.getRoute()!=null)nodes.addAll(v.getRoute().getRoute());
		}
		Branch branch = new Branch();
		List<Route> routes = new ArrayList<>();
		for(Vehicle v : data.keySet()) {
			List<Node> nodes = data.get(v);
			Map<Node, Double> wayPoint = new HashMap<>();
			for(Node n : nodes) {
				for(Record r : records) {
					if(r.getDescription().equals(n.getNodeId())) {
						wayPoint.put(n, - r.getFeatures().get(v.getVehicleId()));
					}else {
						wayPoint.put(n, -1.0);
					}
				}
			}
			for(Node n : wayPoint.keySet()) {
				double leng = wayPoint.get(n);
				if(leng <= 0) {
					System.out.println("Requesting path");
					int start = Integer.parseInt(
							branch.getNearestNode(v.getLocation().getLatitude(), v.getLocation().getLongitude()).getNodeId());
					int end = Integer.parseInt(n.getNodeId());
					wayPoint.put(n, branch.getShortestStreet(start, end ));
				}
			}
			wayPoint = BestRoute.sortedByValue(wayPoint);
			List<Node> nodes2 = new ArrayList<Node>();
			for(Node n : wayPoint.keySet()) {
				nodes2.add(n);
			}
			List<Node> path = new ArrayList<Node>();
			for(int j = 0; j < nodes2.size()-1; j++) {
				int start = Integer.parseInt(nodes2.get(j).getNodeId());
				int end = Integer.parseInt(nodes2.get(j+1).getNodeId());
				path.addAll(branch.getShortestPath(start, end));
			}
			Route r = new Route(v.getVehicleId(), path);
			routes.add(r);
		}
		//branch.saveAllRoute(routes);
		System.out.println("END");
		for(Route r : routes) {
			r.printRoute();
		}
		return routes;	
	}
	
	/**
	 * reqeust all data from other service
	 */
	private boolean getAllData() {
		Branch branch = new Branch();
		vehicles = branch.getAllVehicles();
		if(vehicles == null)return false;
		bookings = branch.getAllWaitingBookings();
		if(bookings == null)return false;
		for(Vehicle v : vehicles) {
			onboards.put(v.getVehicleId(), branch.getAllOnBoardBookings(v.getVehicleId()));
		}
		pickups = branch.getAllPickups();
		if(pickups == null)return false;
		return true;
	}

	/**
	 * Method to extract the node from Booking object
	 * @return all the Departure Node for Bookings with status WAITING
	 */
	public ArrayList<Node> getWaitingDeparture(){
		ArrayList<Node> waitpick = new ArrayList<Node>();
		for(Booking b : bookings) {
			System.out.println(Booking.encodeBooking(b).toJson());
			if(b.getStatus().equals(Booking.Status.WAITING)){
				waitpick.add(b.getDeparture());
			}
		}
		return waitpick;
	}
	
	/**
	 * Method to extract the node form Booking object
	 * @return all the Destination Node for Bookings with status WAITING
	 */
	public ArrayList<Node> getWaitingDestination(){
		ArrayList<Node> waitpick = new ArrayList<Node>();
		for(Booking b : bookings) {
			if(b.getStatus().equals(Booking.Status.WAITING)){
				waitpick.add(b.getDestination());
			}
		}
		return waitpick;
	}
	
	public ArrayList<Node> getWaitingDestination(List<Node> nodes){
		ArrayList<Node> waitpick = new ArrayList<Node>();
		for(Node n : nodes) {
			for(Booking b : bookings) {
				if(b.getDeparture().getNodeId().equals(n.getNodeId())){
					if(!waitpick.contains(b.getDestination()))waitpick.add(b.getDestination());
				}
			}
		}
		return waitpick;
	}
	
	/**
	 * Method to extract the node from Booking object
	 * @param v the Vehicle assigned to the Booking
	 * @return all the Destination Node for Booking with status ONBOARD assigned to the specified vehicle
	 */
	public ArrayList<Node> getOnBoardDestination(List<Booking> ba){
		ArrayList<Node> onpick = new ArrayList<Node>();
		for(Booking b : ba) {
			onpick.add(b.getDestination());
		}
		return onpick;
	}
	
	/**
	 * Method to remove all the node already visited from the vehicle
	 * @param nodes last route of the vehicle
	 * @param v the vehicle releted to the route
	 * @return the filtered list of node
	 */
	public List<Node> filterAlreadyVisited(List<Node> nodes, Vehicle v){
		List<Booking> ba = onboards.get(v.getVehicleId());
		List<Node> dests = this.getOnBoardDestination(ba);
		for(Node n : dests) {		
			if(!nodes.contains(n))nodes.remove(n);
		}
		return nodes;
	}

	/**
	 * Method to extract the Vehicle from vehicles
	 * @param id of the vehicle to search
	 * @return the Vehicle requested or null
	 */
	public Vehicle getVehicle(String id) {
		for(Vehicle v : vehicles) {
			if(id.equals(v.getVehicleId()))return v;
		}
		return null;
	}
	
	/**
	 * Method to extract the Node from pickups
	 * @param id of the node to search
	 * @return the Node requested or null
	 */
	public Node getNode(String id) {
		for(Node n : pickups) {
			if(id.equals(n.getNodeId()))return n;
		}
		return null;
	}
	
	public static Map<Node, Double> sortedByValue(Map<Node, Double> map){
		 return map.entrySet()
	                .stream()
	                .sorted((Map.Entry.<Node, Double>comparingByValue().reversed()))
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	private List<Node> pickups = new ArrayList<Node>();
	private List<Node> standings = new ArrayList<Node>();
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	private List<Booking> bookings = new ArrayList<Booking>();
	private Map<String, List<Booking>> onboards = new HashMap<>();
	private BsonArray clusterResult;
	
	/**
	 * This class implements the thread for the parallel request for the shortestPath value
	 * Each thread work on a data subset and execute the shortestPath from a pickup node to each
	 * vehicle. The distance to the vehicle is then used as a parameter for the clusterization process
	 * @class ParallelPath
	 */
	private class ParallelPath extends Thread{
		/**
		 * To create the ParallelPath thread is necessary a thread group, the list of vehicles and a subset of the nodes
		 * @param tg ThreadGroup for the threads
		 * @param vehicles list of vehicles
		 * @param nodes subset of Nodes
		 */
		public ParallelPath(ThreadGroup tg, List<Vehicle> vehicles, List<Node> nodes) {
			super(tg, "SubThread");
			this.vehicles = vehicles;
			this.nodes = nodes;
		}
		
		public List<Vehicle> getVehicle(){
			return this.vehicles;
		}
		
		public List<Node> getNodes() {
			return this.nodes;
		}
		
		/**
		 * Rreturn the subset of Records generating from the thread
		 * @return an ArrayList<Record>
		 */
		public ArrayList<Record> getRecords() {
			return this.records;
		}
		
		/**
		 * nel metodo run per ogni nodo si esegue un'iterazione.
		 * Ogni thread ha il proprio oggetto branch per eseguire le richieste ai servizi remoti necessari.
		 * Le richieste per i nearest node vengono eseguiti una sola volta prima di ripetere il calcolo dello 
		 * shortest path per ogni nodo (evitiamo di ripetere sempre le stesse richieste)
		 * Si crea una mappa VehicleId - Nodo che verrà utilizzata per conoscere l'appartenenza del nodo ottenuto dalle richieste
		 * nearestnode al rispettivo veicolo 
		 * Viene costruita la mappa di appoggio per conservare i parametri (distanza veicolo nodo per ogni veicolo)
		 * Si itera poi per ogni veicolo:
		 * 			1. si richiede il valore dello shortest path (ottenuto tramite la lista di street)
		 * 			2. si inserisce il paramentro ottenuto nella mappa come coppia VehicleId e valore della distanza del path
		 * al termine dell'iterazione dei veicoli per quel nodo si costruisce un record e lo si inserisce nella sublist di record 
		 * del thread
		 */
		public void run() {
			System.out.println("Starting thread");
			Branch branch = new Branch();
			Map<String, Node> vehNodes = new HashMap<>();
			for (Vehicle v : vehicles) {
				System.out.println("Requesting vehicle node");
				Node nv = branch.getNearestNode(v.getLocation().getLatitude(), v.getLocation().getLongitude());
				vehNodes.put(v.getVehicleId(), nv);
			}
			System.out.println("Terminata richiesta veicoli");
			for(Node n : nodes) {
				System.out.println("Nodes");
				Map<String, Double> param = new HashMap<>();
				for(String s : vehNodes.keySet()) {
					Node nv = vehNodes.get(s);
					int start = Integer.parseInt(nv.getNodeId());
					int to = Integer.parseInt(n.getNodeId());
					double r = branch.getShortestStreet(start, to);
					System.out.print("Value :" + r);
					param.put(s, -r);
				}
				records.add(new Record(n.getNodeId(), param));
			}
		}
		
		private List<Node> nodes;
		private List<Vehicle> vehicles;
		private ArrayList<Record> records = new ArrayList<>();
	}
}
