package algorithm;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;

@Dependent
@Singleton
public class ScheduledTask {
	
	@Schedule(hour = "*", minute = "*", second = "*/600")
	public void runTask() throws InterruptedException{
		r.parallelAlgo2();
	}
	
	private BestRoute r = new BestRoute();
}
