
package task.estimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import task.utils.ConsoleUtils;

/**
 * Класс для храниения спрогнозированных оценок и эталонной оценки пользователя.
 * Оценки записываются в виде матрицы, в которой строкам соответствуют оценки
 * user-item, а столбцы соответствуют применённом алгоритму:
 * <ul>
 *  <li>средняя оценка,</li>
 *  <li>средняя оценка для каждого item,</li>
 *  <li>средняя оценка для каждого пользователя,</li>
 *  <li>random,</li>
 *  <li>эталонная пользовательская оценка.</li>
 * </ul>
 * Также используется для подсчёта метрик алгоритмов прогнозирования.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class EstimationPool {
    /**
     * Матрица прогнозов оценок, рассчитанных с помощью разных алгоритмов.
     */
    private final List<List<Double>> estimations;
    /**
     * Оценки алгоритмов.
     */
    private final List<Predictor> predictors;
    private final List<Metrics> metricses;
    private final List<List<Metrics>> metricsesFor2Way;
    private final List<Metrics> avgMetricsesFor2Way;
    
    public EstimationPool(double step) {
        this.estimations = new ArrayList<>();
        this.predictors = new ArrayList<>();
        this.metricses = new ArrayList<>();
        this.metricsesFor2Way = new ArrayList<>();
        this.avgMetricsesFor2Way = new ArrayList<>();
        Metrics.setStep(step);
        fillInitialMetrics();
    }
    
    private void fillInitialMetrics(){
        for (String algoName : Consts.algoNames) {
            metricses.add(new Metrics());
            List<Metrics> metrs = new ArrayList<>();
            metrs.add(new Metrics().setDelimiter(Consts.Delimiters.initialDelimiter));
            while(metrs.get(metrs.size() - 1).getDelimiter() < Consts.Delimiters.finalDelimiter){
                metrs.add(new Metrics().setDelimiter(metrs.get(metrs.size() - 1)));
            }
            metricsesFor2Way.add(metrs);
        }
    }
    
    /**
     * Создать новую строку в матрице оценок.
     */
    public void createNewEmptyEstimationsRow(){
        this.estimations.add(new ArrayList<Double>());
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение всех предыдущих.
     * @param rating значение оценки.
     */
    public void setAverage(double rating){
        setEstimation(0, rating);
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение оценок этого 
     * item'a.
     * @param rating значение оценки.
     */
    public void setAverageOverItems(double rating){
        setEstimation(1, rating);
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение оценок, проставленных
     * этим пользователем.
     * @param rating значение оценки.
     */
    public void setAverageOverUsers(double rating){
        setEstimation(2, rating);
    }
    
    /**
     * Сохранить оценку, в качестве которой взято случайное число в пределах от
     * <code>Const.lowest</code> до <code>Const.highest</code>.
     * @param rating значение оценки.
     */
    public void setAverageRandom(double rating){
        setEstimation(3, rating);
    }
    
    public void setBaselinePredictor(double rating){
        setEstimation(4, rating);
    }
    
    public void setBaselinePredictorWithBeta(double rating){
        setEstimation(5, rating);
    }
    
    public void setAvgOnGender(double rating){
        setEstimation(6, rating);
    }
    
    public void setAvgOnUsersWithGender(double rating){
        setEstimation(7, rating);
    }
    
    /**
     * Сохранить эталонную оценку пользователем этого item'a.
     * @param rating значение оценки.
     */
    public void setTrueRating(double rating){
        setEstimation(Consts.algoNames.length, rating);
    }
    
    /**
     * Сохранить значение оценки.
     * @param index номер алгоритма:
     * <ul>
     *  <li>средняя оценка,</li>
     *  <li>средняя оценка для каждого item,</li>
     *  <li>средняя оценка для каждого пользователя,</li>
     *  <li>random,</li>
     *  <li>baseline predictor,</li>
     * <li>baseline predictor с коэффициентом затухания Бета,</li>
     *  <li>эталонная пользовательская оценка.</li>
     * </ul>
     * @param rating значение оценки.
     */
    private void setEstimation(int index, double rating){
        this.estimations.get(estimations.size() - 1).add(index, rating);
    }
    
    public void takeIntoAccMetrics(){
        double trueRating = estimations.get(estimations.size() - 1).get(Consts.algoNames.length);
        for(int i=0; i<Consts.algoNames.length; i++){
            double algoRating = estimations.get(estimations.size() - 1).get(i);
            metricses.get(i).takeIntoAcc(trueRating, algoRating);
            for(int j=0; j<metricsesFor2Way.get(i).size(); j++){
                metricsesFor2Way.get(i).get(j).takeIntoAcc(trueRating, algoRating);
            }
        }
    }
    
    /**
     * Перевод матрицы в строку для отображения в консоли.
     * @return строка.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(List<Double> est: estimations){
            for(double e: est){
                sb.append(e).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private double countDiff(List<Double> est, int algoType){
        return est.get(algoType - 1) - est.get(Consts.algoNames.length);
    }
    
    private double countMAE(int algoType){
        double sum = 0;
        for(List<Double> est: estimations){
            sum += Math.abs(countDiff(est, algoType));
        }
        return sum / estimations.size();
    }
    
    private double countRMSE(int algoType){
        double sum = 0;
        for(List<Double> est: estimations){
            sum += Math.pow(countDiff(est, algoType), 2.0);
        }
        return Math.sqrt(sum / estimations.size());
    }
    
    /**
     * Рассчёт матрицы оценок. Строка - использованный алгоритм;
     * столбец - тип оценки:
     * <ul>
     *  <li>mean absolute error,</li>
     *  <li>normalized mean absolute error,</li>
     *  <li>root mean squared error,</li>
     *  <li>normalized root mean squared error.</li>
     * </ul>
     */
    private void countEstimates(){
        for(int i=1; i<Consts.algoNames.length + 1; i++){
            Predictor pred = new Predictor(i - 1);
            double mae = countMAE(i);
            double rmse = countRMSE(i);
            int diff = Consts.highest - Consts.lowest;
            pred.addEstim(mae);
            pred.addEstim(mae / diff);
            pred.addEstim(rmse);
            pred.addEstim(rmse / diff);
            predictors.add(pred);
        }
        //Collections.sort(predictors);
    }
    
    private void countMetrics(){
        for(int i=0; i<metricses.size(); i++){
            metricses.get(i).count(Consts.beta);
        }
        for(List<Metrics> metrs: metricsesFor2Way){
            for(int j=0; j<metrs.size(); j++){
                metrs.get(j).count(Consts.beta);
            }
            avgMetricsesFor2Way.add(Metrics.avg(metrs));
        }
    }
    
    /**
     * Перевод матрицы оценок в строку.
     * @return строковое представление матрицы оценок.
     * @deprecated 
     */
    public String estimatesToString(boolean isFirstWay){
        countEstimates();
        countMetrics();
        //return listPredToString(predictors);
        if(isFirstWay){
            return listPredMetrToString(predictors, metricses, isFirstWay);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(listPredToString(predictors));
        sb.append(ConsoleUtils.strOutput('-')).append("\n");
        sb.append(listAllMetrToString(predictors, metricses, avgMetricsesFor2Way));
        return sb.toString();
    }
    
    private static List<Predictor> sort(List<Predictor> preds){
        List<Predictor> prS = new ArrayList<>();
        for(Predictor pred: preds){
            try{
                prS.add(pred.clone());
            }
            catch(CloneNotSupportedException cnse){
                cnse.printStackTrace();
            }
        }
        Collections.sort(prS);
        return prS;
    }
    
    private static void appendMetricsHeader(StringBuilder sb){
        sb.append("Accur.\t Prec.\t Recall\t F-meas(beta = ");
        sb.append(Consts.beta).append(")");
    }
    
    private static void appendTabs(StringBuilder sb, int tabCnt){
        for(int i=0; i<tabCnt; i++){
            sb.append("\t");
        }
    }
    
    private static void appendWayNames(StringBuilder sb){
        int tabCnt = 2;
        sb.append("|");
        appendTabs(sb, tabCnt);
        sb.append("-= 1 =-");
        appendTabs(sb, tabCnt + 1);
        sb.append("|");
        appendTabs(sb, tabCnt + 1);
        sb.append("-= 2 =-");
        appendTabs(sb, tabCnt);
        sb.append("\n");
    }
    
    public static String listAllMetrToString(List<Predictor> preds, List<Metrics> metrics1Way, List<Metrics> metrics2Way){
        List<Predictor> prS = sort(preds);
        StringBuilder sb = new StringBuilder();
        appendMetricsHeader(sb);
        sb.append("|\t");
        appendMetricsHeader(sb);
        sb.append("\n");
        appendWayNames(sb);
        for(Predictor pred: prS){
            sb.append(metrics1Way.get(pred.getAlgoId()).toString());
            sb.append("\t|\t");
            sb.append(metrics2Way.get(pred.getAlgoId()).toString());
            sb.append("- ").append(pred.getName());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static String listPredMetrToString(List<Predictor> preds, List<Metrics> metrics, boolean withPreds){
        List<Predictor> prS = sort(preds);
        StringBuilder sb = new StringBuilder();
        if(withPreds){
            sb.append("MAE\t NMAE\t RMSE\t NRMSE\t ");
        }
        appendMetricsHeader(sb);
        sb.append("\n");
        for(Predictor pred: prS){
            if(withPreds){
                sb.append(pred.toString(false));
            }
//            Metrics metric = metrics.get(pred.getAlgoId());
//            metric.count(Consts.beta);
            sb.append(metrics.get(pred.getAlgoId()).toString());
            sb.append("- ").append(pred.getName());
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static String listPredToString(List<Predictor> preds){
        List<Predictor> prS = sort(preds);
        StringBuilder sb = new StringBuilder("MAE \t NMAE \t RMSE \t NRMSE\n");
        for(Predictor pred: prS){
            sb.append(pred.toString()).append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Получить список оценок предикторов.
     * @return список оценок предикторов.
     */
    public List<Predictor> getPredictors(){
        return predictors;
    }
    
    /**
     * Рассчёт средних значений по всем прогонам
     * @param estimationPools оценки по всем прогонам.
     * @return сводная таблица средних значений.
     */
    public static List<Predictor> avgPredictors(List<EstimationPool> estimationPools){
        List<Predictor> res = estimationPools.get(0).predictors;
        for(EstimationPool est: estimationPools.subList(1, estimationPools.size())){
            for(int j=0; j<res.size(); j++){
                res.get(j).addPredictor(est.predictors.get(j));
            }
        }
        for(Predictor pred: res){
            pred.divide(estimationPools.size());
        }
        return res;
    }
    
    public static List<Metrics> avgMetrics(List<EstimationPool> estimationPools, boolean way){
        List<Metrics> metricsList = new ArrayList<>();
        if(way){
            metricsList = estimationPools.get(0).metricses;
        }
        else{
            metricsList = estimationPools.get(0).avgMetricsesFor2Way;
        }
        for(EstimationPool est: estimationPools.subList(1, estimationPools.size())){
            for(int j=0; j<metricsList.size(); j++){
                if(way){
                    metricsList.get(j).addMetrics(est.metricses.get(j));
                }
                else{
                    metricsList.get(j).addMetrics(est.avgMetricsesFor2Way.get(j));
                }
            }
        }
        for(Metrics m: metricsList){
            m.divide(estimationPools.size());
        }
        return metricsList;
    }
    
    @Deprecated
    public static List<Predictor> gainingPercentageC(List<Predictor> preds){
        List<Predictor> res = new ArrayList<>();
        Predictor gauge = null;
        int gaugeIndex = 0;
        for(int i=0; i<preds.size(); i++){
            if(preds.get(i).getName().equals(Consts.algoNames[0])){
                gauge = preds.get(i);
                gaugeIndex = i;
            }
        }
        for(int i=0; i<preds.size(); i++){ 
            if(i != gaugeIndex){
                Predictor pr = new Predictor(preds.get(i).getAlgoId());
                for(int j=0; j<gauge.getEstimations().size(); j++){
                    pr.addEstim(countGaining(gauge.getEstimations().get(j),
                            preds.get(i).getEstimations().get(j)));
                }
                res.add(pr);
            }
        }
        return res;
    }
    
    public static Map<String, Double>gainingPercentage(List<Predictor> preds){
        final short estNumber = 0;
        Map<String, Double> res = new LinkedHashMap<>();
        double gauge = 0.0;
        int gaugeIndex = 0;
        for(int i=0; i<preds.size(); i++){
            if(preds.get(i).getName().equals(Consts.algoNames[0])){
                gauge = preds.get(i).getEstimations().get(estNumber);
                gaugeIndex = i;
            }
        }
        for(int i=0; i<preds.size(); i++){
            if(i != gaugeIndex){
                res.put(preds.get(i).getName(), 
                        countGaining(gauge, preds.get(i).getEstimations().get(estNumber)));
            }
        }
        List<Entry<String, Double>> entries = new LinkedList<>(res.entrySet());
        Collections.sort(entries, new PercentageComparator());
        res.clear();
        for(Entry<String, Double> entry: entries){
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }
    
    private static double countGaining(double gauge, double value){
        return 100 * (gauge - value) / gauge;
    }
}
