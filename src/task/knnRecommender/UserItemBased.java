
package task.knnRecommender;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class UserItemBased {
    private static final PearsonsCorrelation pc = new PearsonsCorrelation();
    private final ScoreSupervisor scoreSupervisor;
    private final int k;

    public UserItemBased(ScoreSupervisor scoreSupervisor, int k) {
        this.scoreSupervisor = scoreSupervisor;
        this.k = k;
    }
    
    public double getCorrelation(AlgoType at, int number1, int number2){
        return pc.correlation(MathUtils.Collections.collectAsArray(scoreSupervisor.getEstimates(at, number1)),
                MathUtils.Collections.collectAsArray(scoreSupervisor.getEstimates(at, number2)));
    }
}
