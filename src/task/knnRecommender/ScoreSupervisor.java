
package task.knnRecommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.collections4.map.MultiValueMap;
import task.Score;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ScoreSupervisor {
    private final List<MultiValueMap<Integer, Double>> scores;

    public ScoreSupervisor() {
        scores = new ArrayList<>();
        for(int i=0; i<2; i++){
            scores.add(new MultiValueMap<>());
        }
    }
    
    public void add(Score sc){
        scores.get(0).put(sc.getUserId(), sc.getRating());
        scores.get(1).put(sc.getItemId(), sc.getRating());
    }
    
    public Collection<Double> getEstimates(AlgoType at, int iuNumber){
        return scores.get(at.ordinal()).getCollection(iuNumber);
    }
}
