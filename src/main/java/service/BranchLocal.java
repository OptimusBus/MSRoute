package service;

import java.util.List;

import model.Node;
import model.Route;
import model.Street;

public interface BranchLocal {
	
	/**
	 * Return the assigned route for the vehicle
	 * @param vehicleId the id of the vehicle requesting the route
	 * @return	a Route object
	 */
	public Route getRoute(String vehicleId);
	
	/**
	 * Save the route on database
	 * @param r the Route object to be saved
	 * @return the result of the operation (true if succeded or false if failed)
	 */
	public boolean saveRoute(Route r);
	
	/**
	 * Delete a route from the database
	 * @param vehicleId the id of the vehicle assigned to the desired route
	 * @return the result of the operation (true if succeded or false if failed)
	 */
	public boolean deleteRoute(String vehicleId);
	
	/**
	 * Return the value of the shortestPath for two node
	 * @param start the id of the starting Node
	 * @param dest the id of the destination Node
	 * @return the value of the shortestpath (return 0 if there is a problem)
	 */
	public double getShortestStreet(int start, int dest);
	
	/**
	 * Get the nearest node from given coordinates
	 * @param lat latitude
	 * @param lon longitude
	 * @return the closest Node object for given coordinates (null if there is a problem)
	 */
	public Node getNearestNode(double lat, double lon);
	
	/**
	 * Get the shortestPath for two node as a list of node
	 * @param start the id of the starting node
	 * @param dest the id of the destination node
	 * @return a the shortestPath as a list of Node
	 */
	public List<Node> getShortestPath(int start, int dest);
}
