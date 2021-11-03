package db;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.bulk.InsertRequest;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;

import model.Route;


public class MongoConnector {
	
	public Document getRouteByVehicleId(String vehicleId) {
		MongoDatabase db=m.getDatabase("RoutesDB");
		MongoCollection<Document> coll=db.getCollection("routes");
		return coll.find(Filters.eq("vehicleId", vehicleId)).first();
	}
	
	public void saveRoute(Route r) {
		MongoDatabase db=m.getDatabase("RoutesDB");
		MongoCollection<Document> coll=db.getCollection("routes");
		coll.insertOne(Route.encodeRoute(r));
	}
	
	public boolean removeRoute(String vehicleId) {
		MongoDatabase db=m.getDatabase("RoutesDB");
		MongoCollection<Document> coll=db.getCollection("routes");
		if(coll.find(Filters.eq("vehicleId", vehicleId)).first() != null) {
			DeleteResult result = coll.deleteOne(Filters.eq("vehicleId", vehicleId));
			return result.wasAcknowledged();
		}
		return false;
	}
	
	private static final MongoClient m = new MongoClient("132.121.170.248",31183);
}
