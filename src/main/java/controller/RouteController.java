package controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

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
		return Response.ok("Hello funziono").build();
	}
	
	@GET
	public Response bestRoute(@QueryParam("fromLat") double fromLat, @QueryParam("fromLong") double fromLong, 
			@QueryParam("toLat") double toLat, @QueryParam("toLong") double toLong) {
		return null;
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
	
	private BranchLocal branch = new Branch();
	
}
