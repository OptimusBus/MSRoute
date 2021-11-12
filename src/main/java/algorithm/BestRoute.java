package algorithm;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@SuppressWarnings("deprecation")
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
	public void parallelAlgo() throws InterruptedException {
		List<Node> wn = getWaitingDeparture();
		ArrayList<Record> records = new ArrayList<>();
		ThreadGroup tg = new ThreadGroup("records");
		/*
		 * Per gestire l'esecuzione parallela bisogna dividere l'insieme dei nodi per cui si vuole eseguire la clasterizzazione in
		 * N parti, dove N è il numero di thread che si vuole utilizzare
		 */
		ParallelPath p1 = new ParallelPath(tg, vehicles, wn.subList(0, wn.size()%2-1));
		ParallelPath p2 = new ParallelPath(tg, vehicles, wn.subList(wn.size()%2, wn.size()-1));
		p1.start();
		p2.start();
		p1.join();
		p2.join();
		/*
		 * Al termine dell'esecuzione dei thread ognuno conterrà un sub set di records necessari a costruire 
		 * il sub set di record necessario ad eseguire la clusterizzazione
		 */
		records = p1.getRecords();
		records.addAll(p2.getRecords());
		tg.destroy();
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
	
	/**
	 * Questa classe implementa il thread per eseguire la costruzione in parallelo dei record utilizzati
	 * dall'algoritmo di classificazione K-means
	 * Ogni record è costituito dall'id del nodo e una mappa che contiene la distanza di quel nodo dal veicolo
	 * @class ParallelPath
	 */
	private class ParallelPath extends Thread{
		/**
		 * Per costruire il thread sono necessari, thread group di riferimento (utilizzato poi per cancellare tutti i thread insieme)
		 * La lista dei veicoli su cui si effettua la clusterizzazione e il subset di nodi per cui generare i record
		 * @param tg thread group di riferimento
		 * @param vehicles lista dei veicoli su cui si vuole effettuare la clusterizzazione
		 * @param nodes subset di nodi per cui generare i record
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
			Branch branch = new Branch();
			Map<String, Node> vehNodes = new HashMap<>();
			for (Vehicle v : vehicles) {
				Node nv = branch.getNearestNode(v.getLocation().getLatitude(), v.getLocation().getLongitude());
				vehNodes.put(v.getVehicleId(), nv);
			}
			for(Node n : nodes) {
				Map<String, Double> param = new HashMap<>();
				for(String s : vehNodes.keySet()) {
					Node nv = vehNodes.get(s);
					int start = Integer.parseInt(nv.getNodeId());
					int to = Integer.parseInt(n.getNodeId());
					double r = branch.getShortestStreet(start, to);
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
