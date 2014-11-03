
package task.knnRecommender;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import task.estimations.Consts;

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
        return pc.correlation(scoreSupervisor.getEstimatesForCorrel(at, number1),
                scoreSupervisor.getEstimatesForCorrel(at, number2));
    }
    
    public double[][] countCorrelationMatrix(AlgoType at){
        double[][] matrix = new double[scoreSupervisor.getSize(at)][scoreSupervisor.getSize(at)];
        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix.length; j++){
                if(j > i){
                    matrix[i][j] = getCorrelation(at, i, j);
                }
                else if(i == j){
                    matrix[i][j] = Consts.UNDEFINED;
                }
                else{
                    matrix[j][i] = matrix[i][j];
                }
            }
        }
        return matrix;
    }
}
