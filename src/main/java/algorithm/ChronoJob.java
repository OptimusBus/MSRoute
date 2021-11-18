package algorithm;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@Singleton
public class ChronoJob {

	/**
	 * Execute the Best Route algorithm every 10 minute
	 */
     @Schedule(hour = "*", minute = "*/10", second = "*", persistent = false)
     private void calcAlgo() {
        try {
			r.algo();
		} catch (InterruptedException e) {
			System.out.println("Error while executing the scheduled job");
			e.printStackTrace();
		}
     }
     
     private BestRoute r = new BestRoute();
}
