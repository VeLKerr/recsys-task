
package task.estimations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Metrics {
    private static final double limit = (Consts.highest + Consts.lowest) / 2;
    /**
     * 1. TP,
     * 2. FN,
     * 3. FP,
     * 4. TN.
     */
    private final int[] cnts;
    private int precision;
    private int recall;

    public Metrics() {
        this.cnts = new int[4];
        Arrays.fill(cnts, 0); //TODO: Проверить без этой строки.
    }
    
    public void takeIntoAcc(double userEst, double algorithmRes){
        int type = 0;
        if(userEst >= limit && algorithmRes < limit){
            type = 1;
        }
        else if(userEst < limit && algorithmRes >= limit){
            type = 2;
        }
        else if(userEst < limit && algorithmRes < limit){
            type = 3;
        }
        cnts[type]++;
    }
    
    private static double sum(int[] mas){
        int res = 0;
        for(int el: mas){
            res += el;
        }
        return res;
    }
    
    public double getAccuracy(){
        return (double)(cnts[0] + cnts[3]) / sum(cnts);
    }
    
    private double getMetric(int number){
        return ((double) cnts[0]) / (cnts[0] + cnts[number]);
    }
    
    public double getPrecision(){
        return getMetric(2);
    }
    
    public double getRecall(){
        return getMetric(1);
    }
}
