package service;

import model.Node;
import model.Route;

public interface BranchLocal {
	public Route getRoute(String vehicleId);
	public boolean saveRoute(Route r);
	public boolean deleteRoute(String vehicleId);
	public Route bestRoute(String vehicleId);
	public double getShortestRoute(int start, int dest);
	public Node getNearestNode(double lat, double lon);
}
