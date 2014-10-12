
package task.estimations;

import java.util.Arrays;
import java.util.List;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Metrics {
    private static double STEP;
    private double delimiter;
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
        this.delimiter = (Consts.highest + Consts.lowest) / 2;
    }
    
    public static void setStep(double step){
        STEP = step;
    }
    
    public Metrics setDelimiter(Metrics oldMetrics){
        this.delimiter = oldMetrics.delimiter + STEP;
        return this;
    }
    
    public Metrics setDelimiter(double delimiter){
        this.delimiter = delimiter;
        return this;
    }
    
    public void takeIntoAcc(double userEst, double algorithmRes){
        MetricType mt = MetricType.TruePositive;
        if(userEst >= delimiter && algorithmRes < delimiter){
            mt = MetricType.FalseNegative;
        }
        else if(userEst < delimiter && algorithmRes >= delimiter){
            mt = MetricType.FalsePositive;
        }
        else if(userEst < delimiter && algorithmRes < delimiter){
            mt = MetricType.TrueNegative;
        }
        cnts[mt.toInt()]++;
    }
      
    public void count(double beta){
        accuracy = (double)(cnts[0] + cnts[3]) / MathUtils.AvgCountMethods.sum(cnts);
        precision = getMetric(2);
        recall = getMetric(1);
        double betaSqr = Math.pow(beta, 2.0);
        fMesure = (betaSqr + 1) * precision * recall / (betaSqr * precision + recall);
    }
    
    private double getMetric(int number){//"1 + " - to avoid the NaN case
        return (1 + (double) cnts[0]) / (1 + cnts[0] + cnts[number]);
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
    
    public static Metrics avg(List<Metrics> metrs){
        Metrics res = new Metrics();
        for(Metrics mt: metrs){
            res.accuracy += mt.accuracy;
            res.precision += mt.precision;
            res.recall += mt.recall;
            res.fMesure += mt.fMesure;
        }
        res.divide(metrs.size());
        return res;
    }

    public double getDelimiter() {
        return delimiter;
    }
    
    public static double getStep(){
        return STEP;
    }
}
