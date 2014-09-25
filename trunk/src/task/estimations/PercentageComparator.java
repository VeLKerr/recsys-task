
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
        return - o1.getValue().compareTo(o2.getValue());
    }
    
}
