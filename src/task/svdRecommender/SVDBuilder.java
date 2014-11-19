
package task.svdRecommender;

import java.util.List;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import task.Score;
import task.knnRecommender.ScoreSupervisor;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class SVDBuilder {
    private enum BaseLines{
        AVG,
        USERS,
        ITEMS
    }
    private final SingularValueDecomposition singularValueDecomposition;
    private final RealMatrix u;
    private final RealMatrix v;
    private double[] baseline;
    
    public SVDBuilder(ScoreSupervisor supervisor){
        this.singularValueDecomposition = new SingularValueDecomposition(supervisor.getScoreMtrix());
        this.u = singularValueDecomposition.getU().multiply(singularValueDecomposition.getS());//transpose?
        this.v = singularValueDecomposition.getV();
        this.baseline = new double[3];
    }
    
    public void setBaseline(double[] baseline){
        this.baseline = baseline;
    }
    
    public double getSimplePrediciton(Score sc){
        return MathUtils.scalarMultiply(u.getColumn(sc.getUserId()), v.getRow(sc.getItemId()));
    }
    
    public double getPrediction(Score sc){
        double res = getSimplePrediciton(sc);
        for(double el: baseline){
            res += el;
        }
        return res;
    }
}
