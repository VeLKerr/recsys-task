
package task.estimations;

import java.util.Comparator;
import java.util.Map.Entry;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class PercentageComparator implements Comparator<Entry<String, Double>>{

    @Override
    public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
        if(o1.getValue() > o2.getValue()){
            return 1;
        }
        else if(o1.getValue() < o2.getValue()){
            return -1;
        }
        return 0;
    }
    
}
