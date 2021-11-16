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
import org.bson.Document;
import org.json.simple.parser.ParseException;

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
	@Path("/bestVehicle")
	public Response nearestVehicle(String s) {
		Document d = Document.parse(s);
		Vehicle v = Vehicle.decodeVehicle(d);
		Vehicle best = branch.getNearestVehicle(v);
		if(best != null)return Response.ok().entity(best.getVehicleId()).build();
		return Response.status(404).entity("No vehicle found").build();
		//prendiamo i veicoli disattivati
		//poi valutiamo quelli che sono occupati
	}
	
	@POST
	@Path("/bestSP")
	public Response bestStandingPoint(String v) {
		Document d = Document.parse(v);
		Vehicle ve = Vehicle.decodeVehicle(d);
		String s = branch.getBestStandingPoint(ve);
		if(s != null)return Response.ok().entity(s).build();
		return Response.status(500).entity("Impossible to find a standig point").build();
	}
	
	@GET
	@Path("/exeBestRoute1")
	public Response executeAlgorithm() {
		try {
			List<Route> r = bestRoute.parallelAlgo2();
			if(r == null) return Response.status(500).entity("Error while executing the algoritm").build();
			//branch.saveAllRoute(r);
			return Response.ok().entity(r).build();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Response.status(500).entity("Error while executing the algoritm").build();
		} catch(ParseException e) {
			return Response.status(500).entity("Error while parsing").build();
		}
	}
	
	@GET
	@Path("/exeBestRoute")
	public Response executeAlgorithm1() {
		try {
			List<Route> r = bestRoute.algo();
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
		if(b != null)return Response.noContent().entity("No cluster found").build();
		return Response.ok().entity(b).build();
	}
	
	private BranchLocal branch = new Branch();
	private BestRoute bestRoute = new BestRoute();
	
}
