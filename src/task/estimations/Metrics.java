
package task.estimations;

import java.util.ArrayList;
import java.util.List;
import task.utils.MathUtils;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Metrics {
    private static double STEP;
    private enum ClassificationType {
        TruePositive, 
        FalseNegative, 
        FalsePositive,
        TrueNegative
    };
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
                return "F1(b=" + (int)Consts.beta + ")";
            }
        },
        NEGATIVE_PRECISION{
            @Override
            protected String getShortName() {
                return "N.Prec.";
            }
        },
        SPECIFYCITY{
            @Override
            protected String getShortName() {
                return "Spec.";
            }
        },
        SENSITIVITY{
            @Override
            protected String getShortName() {
                return "Sens.";
            }
        },
        FALSE_DISCOVERY_RATE{
            @Override
            protected String getShortName() {
                return "FDR.";
            }
        },
        FALSE_POSITIVE_RATE{
            @Override
            protected String getShortName() {
                return "FPR.";
            }
            @Override
            protected boolean isVisible(){
                return false;
            }
        },
        ALPHA_ERROR{
            @Override
            protected String getShortName() {
                return "a-err";
            }
        },
        BETA_ERROR{
            @Override
            protected String getShortName() {
                return "b-err";
            }
        },
        MATTHEW{
            @Override
            protected String getShortName() {
                return "Matthew";
            }
        };
        
        protected abstract String getShortName();
        
        protected boolean isVisible(){
            return true;
        }
        
        public static String namesToString(){
            StringBuilder sb = new StringBuilder();
            for(MetricNameTypes mnt: MetricNameTypes.values()){
                if(mnt.isVisible()){
                    sb.append(mnt.getShortName()).append("\t");
                }
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
        ClassificationType mt = ClassificationType.TruePositive;
        if(userEst >= delimiter && algorithmRes < delimiter){
            mt = ClassificationType.FalseNegative;
        }
        else if(userEst < delimiter && algorithmRes >= delimiter){
            mt = ClassificationType.FalsePositive;
        }
        else if(userEst < delimiter && algorithmRes < delimiter){
            mt = ClassificationType.TrueNegative;
        }
        cnts[mt.ordinal()]++;
    }
    
    private void deNaN(){
        for(int i=0; i<cnts.length; i++){
            if(cnts[i] == 0){
                cnts[i] = 1;
            }
        }
    }
    
    public void count(double beta){
        double sum = MathUtils.AvgCountMethods.sum(cnts);
        deNaN();
        allMetrics.set(MetricNameTypes.ACCURACY.ordinal(), 
                (double)(cnts[0] + cnts[3]) / sum);
        double precision = getMetric(2);
        double recall = getMetric(1);
        allMetrics.set(MetricNameTypes.PRECISION.ordinal(), precision);
        allMetrics.set(MetricNameTypes.RECALL.ordinal(), recall);
        double betaSqr = Math.pow(beta, 2.0);
        allMetrics.set(MetricNameTypes.F_MEASURE.ordinal(),
                (betaSqr + 1) * precision * recall / (betaSqr * precision + recall));
        allMetrics.set(MetricNameTypes.NEGATIVE_PRECISION.ordinal(), (double)cnts[3] / (cnts[3] + cnts[1]));
        allMetrics.set(MetricNameTypes.SPECIFYCITY.ordinal(), (double)cnts[3] / (cnts[3] + cnts[2]));
        allMetrics.set(MetricNameTypes.SENSITIVITY.ordinal(), (double)cnts[0] / (cnts[0] + cnts[1]));
        allMetrics.set(MetricNameTypes.FALSE_DISCOVERY_RATE.ordinal(), (double)cnts[2] / (cnts[2] + cnts[0]));
        allMetrics.set(MetricNameTypes.FALSE_POSITIVE_RATE.ordinal(), (double)cnts[2] / (cnts[3] + cnts[1]));
        allMetrics.set(MetricNameTypes.ALPHA_ERROR.ordinal(), cnts[2] / sum);
        allMetrics.set(MetricNameTypes.BETA_ERROR.ordinal(), cnts[1] / sum);
        allMetrics.set(MetricNameTypes.MATTHEW.ordinal(),
                   (cnts[0]*cnts[3] - cnts[1]*cnts[2]) /  
                   Math.sqrt(Math.exp(
                           countDenomTerm(0, 1) + countDenomTerm(2, 3) + countDenomTerm(0, 2) + countDenomTerm(1, 3))));
    }
    
    private double countDenomTerm(int numb1, int numb2){
        return Math.log(cnts[numb1] + cnts[numb2]);
    }
    
    private double getMetric(int number){//"1 + " - to avoid the NaN case
        return (double) cnts[0] / (cnts[0] + cnts[number]);
    }
    
    public static String headersToString(){
        return MetricNameTypes.namesToString();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            if(mnt.isVisible()){
                sb.append(MathUtils.roundDouble(allMetrics.get(mnt.ordinal()))).append("\t");
            }
        }
        return sb.toString();
    }
    
    public void divide(int number){
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            int ordinal = mnt.ordinal();
            allMetrics.set(ordinal, allMetrics.get(ordinal) / number);
        }
    }
    
    public void addMetrics(Metrics metrics){
        for(MetricNameTypes mnt: MetricNameTypes.values()){
            int ordinal = mnt.ordinal();
            allMetrics.set(ordinal, allMetrics.get(ordinal) + metrics.allMetrics.get(ordinal));
        }
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
