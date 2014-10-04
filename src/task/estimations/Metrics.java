
package task.estimations;

import java.util.Arrays;

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

    public Metrics() {
        this.cnts = new int[4];
        Arrays.fill(cnts, 0); //TODO: Проверить без этой строки.
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
    
    public double getAccuracy(){
        accuracy = (double)(cnts[0] + cnts[3]) / sum(cnts);
        return accuracy;
    }
    
    private double getMetric(int number){
        return ((double) cnts[0]) / (cnts[0] + cnts[number]);
    }
    
    public double getPrecision(){
        precision = getMetric(2);
        return precision;
    }
    
    public double getRecall(){
        recall = getMetric(1);
        return recall;
    }
    
    public double getFMeasure(double beta){
        double betaSqr = Math.pow(beta, 2.0);
        return (betaSqr + 1) * precision * recall / (betaSqr * precision + recall);
    }
}
