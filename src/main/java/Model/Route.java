package Model;

import java.util.List;



public class Route {
	public Route() {}
	
	public List<Node> getRoute() {
		return route;
	}

	public void setRoute(List<Node> route) {
		this.route = route;
	}

	private List<Node> route;
	

}
