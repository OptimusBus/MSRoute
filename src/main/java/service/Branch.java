package service;

import org.bson.Document;

import db.MongoConnector;
import model.Route;

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
	
	private MongoConnector mdb = new MongoConnector();
}
