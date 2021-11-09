package kmeans;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Encapsulates all coordinates for a particular cluster centroid.
 */
public class Centroid {

    /**
     * The centroid coordinates.
     */
    private final Map<String, Double> coordinates;

    public Centroid(Map<String, Double> coordinates) {
        this.coordinates = coordinates;
    }

    public Map<String, Double> getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Centroid centroid = (Centroid) o;
        return Objects.equals(getCoordinates(), centroid.getCoordinates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCoordinates());
    }

    @Override
    public String toString() {
        return "Centroid " + coordinates;
    }
    
    public String getBestValue() {
    	String best = null;
    	double max = -9999999999999999.0;
    	for(String key : this.coordinates.keySet()) {
    		Double value = this.coordinates.get(key);
    		if(value > max) {
    			best = key;
    			max = value;
    		}
    	}
    	return best;	
    }
    
    public static Centroid sortedCentroid(Centroid key) {
        List<Map.Entry<String, Double>> entries = new ArrayList<>(key
          .getCoordinates()
          .entrySet());
        entries.sort((e1, e2) -> e2
          .getValue()
          .compareTo(e1.getValue()));

        Map<String, Double> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return new Centroid(sorted);
    }
    
}

