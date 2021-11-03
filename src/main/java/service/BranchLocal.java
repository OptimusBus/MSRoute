package service;

import model.Route;

public interface BranchLocal {
	public Route getRoute(String vehicleId);
	public boolean saveRoute(Route r);
	public boolean deleteRoute(String vehicleId);
}
