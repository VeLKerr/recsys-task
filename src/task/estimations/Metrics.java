
package task.estimations;

import com.sun.deploy.uitoolkit.impl.fx.Utils;
import java.util.Arrays;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Metrics {
    private static final double limit = (Consts.highest + Consts.lowest) / 2;
    /**
     * 0. TP,
     * 1. FN,
     * 2. FP,
     * 3. TN.
     */
    private final int[] cnts;
    private double accuracy;
    private double precision;
    private double recall;
    private double fMesure;

    public Metrics() {
        this.cnts = new int[4];
        Arrays.fill(cnts, 0); //TODO: Проверить без этой строки.
        //Для корректной работы addMetrics()
        accuracy = 0;
        precision = 0;
        recall = 0;
        fMesure = 0;
    }
    
    public void takeIntoAcc(double userEst, double algorithmRes){
        MetricType mt = MetricType.TruePositive;
        if(userEst >= limit && algorithmRes < limit){
            mt = MetricType.FalseNegative;
        }
        else if(userEst < limit && algorithmRes >= limit){
            mt = MetricType.FalsePositive;
        }
        else if(userEst < limit && algorithmRes < limit){
            mt = MetricType.TrueNegative;
        }
        cnts[mt.toInt()]++;
    }
    
    private static double sum(int[] mas){
        int res = 0;
        for(int el: mas){
            res += el;
        }
        return res;
    }
    
    public void count(double beta){
        accuracy = (double)(cnts[0] + cnts[3]) / sum(cnts);
        precision = getMetric(2);
        recall = getMetric(1);
        double betaSqr = Math.pow(beta, 2.0);
        fMesure = (betaSqr + 1) * precision * recall / (betaSqr * precision + recall);
    }
    
    private double getMetric(int number){
        return ((double) cnts[0]) / (cnts[0] + cnts[number]);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(MathUtils.roundDouble(accuracy)).append("\t");
        sb.append(MathUtils.roundDouble(precision)).append("\t");
        sb.append(MathUtils.roundDouble(accuracy)).append("\t");
        sb.append(MathUtils.roundDouble(fMesure)).append("\t");
        return sb.toString();
    }
    
    public void divide(int number){
        accuracy /= number;
        precision /= number;
        recall /= number;
        fMesure /= number;
    }
    
    public void addMetrics(Metrics metrics){
        accuracy += metrics.accuracy;
        precision += metrics.precision;
        recall += metrics.recall;
        fMesure += metrics.fMesure;
    }
}
