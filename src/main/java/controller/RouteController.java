package controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.bson.BsonArray;

import algorithm.BestRoute;
import model.Route;
import model.Vehicle;
import service.*;

@Consumes({"application/json"})
@Produces("application/json")
@Path("/routes")
public class RouteController {
			
	public RouteController() {
		super();
	}
	
	@GET
	@Path("/ok")
	public Response ok() {
		return Response.ok("OK").build();
	}
	
	@GET
	@Path("/{id}")
	public Response getRoute(@PathParam("id") String id) {
		Route r = branch.getRoute(id);
		if(r != null)return Response.noContent().entity("No route found for specified id").build();
		return Response.ok().entity(r).build();
	}
	
	@POST
	@Path("/nearestVehicle")
	public Response nearestVehicle(Vehicle d) {
		return null;
	}
	
	@POST
	@Path("/bestSP")
	public Response bestStandingPoint(Vehicle s) {
		return null;
	}
	
	
	@GET
	@Path("/exeBestRoute")
	public Response executeAlgorithm() {
		try {
			List<Route> r = bestRoute.parallelAlgo();
			if(r == null) return Response.status(500).entity("Error while executing the algoritm").build();
			branch.saveAllRoute(r);
			return Response.ok().entity(r).build();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Response.status(500).entity("Error while executing the algoritm").build();
		}
	}
		
	@GET
	@Path("/cluster")
	public Response getClusterData() {
		BsonArray b = bestRoute.getClusterResult();
		if(b != null || b.isEmpty())return Response.noContent().entity("No cluster found").build();
		return Response.ok().entity(b).build();
	}
	
	private BranchLocal branch = new Branch();
	private BestRoute bestRoute = new BestRoute();
	
}
