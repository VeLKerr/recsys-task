
package task.estimations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Metrics {
    private static double STEP;
    private enum MetricNameTypes{
        ACCURACY {
            @Override
            protected String getShortName(){
                return "Accur.";
            }
        },
        PRECISION {
            @Override
            protected String getShortName(){
                return "Prec.";
            }
        },
        RECALL {
            @Override
            protected String getShortName() {
                return "Recall";
            }
            
        },
        F_MEASURE {
            @Override
            protected String getShortName() {
                return "F-meas(b = " + Consts.beta + ")";
            }
        };
        
        protected abstract String getShortName();
        
        public static String namesToString(){
            StringBuilder sb = new StringBuilder();
            for(MetricNameTypes mnt: MetricNameTypes.values()){
                sb.append(mnt.getShortName()).append("\t");
            }
            return sb.toString();
        }
    }
    private double delimiter;
    /**
     * 0. TP,
     * 1. FN,
     * 2. FP,
     * 3. TN.
     */
    private final int[] cnts;
    private List<Double> allMetrics;

    public Metrics() {
        this.cnts = new int[4];
        this.delimiter = (Consts.highest + Consts.lowest) / 2;
        allMetrics = new ArrayList<>();
        //Для корректной работы addMetrics()
        for(int i=0; i<MetricNameTypes.values().length; i++){
            allMetrics.add(0.0);
        }
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
        allMetrics.set(MetricNameTypes.ACCURACY.ordinal(), 
                (double)(cnts[0] + cnts[3]) / MathUtils.AvgCountMethods.sum(cnts));
        double precision = getMetric(2);
        double recall = getMetric(1);
        allMetrics.set(MetricNameTypes.PRECISION.ordinal(), precision);
        allMetrics.set(MetricNameTypes.RECALL.ordinal(), recall);
        double betaSqr = Math.pow(beta, 2.0);
        allMetrics.set(MetricNameTypes.F_MEASURE.ordinal(),
                (betaSqr + 1) * precision * recall / (betaSqr * precision + recall));
    }
    
    private double getMetric(int number){//"1 + " - to avoid the NaN case
        return (1 + (double) cnts[0]) / (1 + cnts[0] + cnts[number]);
    }
    
    public static String headersToString(){
        return MetricNameTypes.namesToString();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            sb.append(MathUtils.roundDouble(allMetrics.get(mnt.ordinal()))).append("\t");
        }
        return sb.toString();
    }
    
    public void divide(int number){
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            int ordinal = mnt.ordinal();
            allMetrics.set(ordinal, allMetrics.get(ordinal) / number);
        }
//        accuracy /= number;
//        precision /= number;
//        recall /= number;
//        fMesure /= number;
    }
    
    public void addMetrics(Metrics metrics){
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            int ordinal = mnt.ordinal();
            allMetrics.set(ordinal, allMetrics.get(ordinal) + metrics.allMetrics.get(ordinal));
        }
//        accuracy += metrics.accuracy;
//        precision += metrics.precision;
//        recall += metrics.recall;
//        fMesure += metrics.fMesure;
    }
    
    public static Metrics avg(List<Metrics> metrs){
        Metrics res = new Metrics();
        for(Metrics mt: metrs){
            res.addMetrics(mt);
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
