package algorithm;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

//@Singleton
public class ChronoJob {

	/**
	 * Execute the Best Route algorithm every 10 minute
	 */
     //@Schedule(hour = "*", minute = "*/10", second = "*", persistent = false)
     private void calcAlgo() {
        try {
        	ChronoThread t = new ChronoThread();
        	t.start();
        	t.join();
		} catch (InterruptedException e) {
			System.out.println("Error while executing the scheduled job");
			e.printStackTrace();
		}
     }
     
     private class ChronoThread extends Thread {
    	 
    	 private BestRoute r = new BestRoute();
    	 public void run() {
    		 try {
				r.algo();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
     }
}
