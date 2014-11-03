
package task.knnRecommender;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import task.Score;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ScoreSupervisor {
    private static final PearsonsCorrelation pc = new PearsonsCorrelation();
    private final RealMatrix scoreMatrix;
    private final int k;
    
    public ScoreSupervisor() {
        scoreMatrix = new Array2DRowRealMatrix();
        k = 0;
    }
    
    public ScoreSupervisor(int k){
        this.scoreMatrix = new Array2DRowRealMatrix();
        this.k = k;
    }
    
    public void add(Score sc){
        scoreMatrix.setEntry(sc.getUserId(), sc.getItemId(), sc.getRating());
    }
    
    public double[] getEstimatesForCorrel(AlgoType at, int iuNumber){
        if(at == AlgoType.USER_BASED){
            return scoreMatrix.getRow(iuNumber);
        }
        else{
            return scoreMatrix.getColumn(iuNumber);
        }
    }
    
    public double getCorrelation(AlgoType at, int number1, int number2){
        if(at == AlgoType.USER_BASED){
            return pc.correlation(scoreMatrix.getRow(number1), scoreMatrix.getRow(number2));
        }
        else{
            return pc.correlation(scoreMatrix.getColumn(number1), scoreMatrix.getColumn(number2));
        }
    }
}
