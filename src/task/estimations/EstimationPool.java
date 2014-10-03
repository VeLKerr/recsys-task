
package task.estimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static task.Task.beta;

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
     * Класс констант
     */
    public static abstract class Const{
        /**
         * Кол-во символов после запятой при округлении десятичных дробей.
         */
        public static final int symbolsAfterComma = 5;
    }
    /**
     * Матрица прогнозов оценок, рассчитанных с помощью разных алгоритмов.
     */
    private final List<List<Double>> estimations;
    /**
     * Оценки алгоритмов.
     */
    private final List<Predictor> predictors;
    
    public EstimationPool() {
        this.estimations = new ArrayList<>();
        this.predictors = new ArrayList<>();
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
    public void countEstimates(){
        for(int i=1; i<Consts.algoNames.length + 1; i++){
            Predictor pred = new Predictor(Consts.algoNames[i-1]);
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
    
    /**
     * Перевод матрицы оценок в строку.
     * @return строковое представление матрицы оценок.
     * @deprecated 
     */
    public String estimatesToString(){
        countEstimates();
        return listPredToString(predictors);
    }
    
    public static String listPredToString(List<Predictor> preds){
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
    public static List<Predictor> avg(List<EstimationPool> estimationPools){
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
                Predictor pr = new Predictor(preds.get(i).getName());
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
