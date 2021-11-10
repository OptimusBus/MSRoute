package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StatusLogger {
	
	public void createLog(){
		try {
			this.fh = new FileHandler("status.log", true);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}
	
	public void writeLog(String message) {
		Logger l = Logger.getLogger("StatusLog");
		l.addHandler(fh);
		l.setLevel(Level.ALL);
		SimpleFormatter f= new SimpleFormatter();
		fh.setFormatter(f);
		l.log(Level.ALL, message);
	}
	
	private FileHandler fh;
}
