package service;

import java.util.List;

import model.Node;
import model.Route;
import model.Street;

public interface BranchLocal {
	public Route getRoute(String vehicleId);
	public boolean saveRoute(Route r);
	public boolean deleteRoute(String vehicleId);
	public Route bestRoute(String vehicleId);
	public double getShortestStreet(int start, int dest);
	public Node getNearestNode(double lat, double lon);
}
