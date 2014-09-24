
package task.estimations;

import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import task.utils.Utils;
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
         * Границы шкалы пользовательских оценок.
         */
        public static final int highest = 5;
        public static final int lowest = 1;
        
        /**
         * Кол-во символов после запятой при округлении десятичных дробей.
         */
        public static final int symbolsAfterComma = 5;
        /**
         * Кол-во использованных алгоритмов.
         */
        private static final int algoCnt = 6;
        private static final String[] algoNames = {
            "average values",
            "average over the items",
            "average over the users",
            "random rating",
            "baseline predictor",
            "baseline predictor with BETA=" + beta
        };
    }
    /**
     * Матрица прогнозов оценок, рассчитанных с помощью разных алгоритмов.
     */
    private final List<List<Integer>> estimations;
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
        this.estimations.add(new ArrayList<Integer>());
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение всех предыдущих.
     * @param rating значение оценки.
     */
    public void setAverage(int rating){
        setEstimation(0, rating);
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение оценок этого 
     * item'a.
     * @param rating значение оценки.
     */
    public void setAverageOverItems(int rating){
        setEstimation(1, rating);
    }
    
    /**
     * Сохранить оценку, рассчитанную как среднее значение оценок, проставленных
     * этим пользователем.
     * @param rating значение оценки.
     */
    public void setAverageOverUsers(int rating){
        setEstimation(2, rating);
    }
    
    /**
     * Сохранить оценку, в качестве которой взято случайное число в пределах от
     * <code>Const.lowest</code> до <code>Const.highest</code>.
     * @param rating значение оценки.
     */
    public void setAverageRandom(int rating){
        setEstimation(3, rating);
    }
    
    public void setBaselinePredictor(int rating){
        setEstimation(4, rating);
    }
    
    public void setBaselinePredictorWithBeta(int rating){
        setEstimation(5, rating);
    }
    
    /**
     * Сохранить эталонную оценку пользователем этого item'a.
     * @param rating значение оценки.
     */
    public void setTrueRating(int rating){
        setEstimation(Const.algoCnt, rating);
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
    private void setEstimation(int index, int rating){
        this.estimations.get(estimations.size() - 1).add(index, rating);
    }
    
    /**
     * Перевод матрицы в строку для отображения в консоли.
     * @return строка.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(List<Integer> est: estimations){
            for(int e: est){
                sb.append(e).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private double countDiff(List<Integer> est, int algoType){
        return est.get(algoType - 1) - est.get(Const.algoCnt);
    }
    
    private double countMAE(int algoType){
        int sum = 0;
        for(List<Integer> est: estimations){
            sum += Math.abs(countDiff(est, algoType));
        }
        return (double)sum / estimations.size();
    }
    
    private double countRMSE(int algoType){
        int sum = 0;
        for(List<Integer> est: estimations){
            sum += Math.pow(countDiff(est, algoType), 2.0);
        }
        return Math.sqrt((double)sum / estimations.size());
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
     * @return матрица оценок.
     */
    public void countEstimates(){
        for(int i=1; i<Const.algoCnt + 1; i++){
            Predictor pred = new Predictor(Const.algoNames[i-1]);
            double mae = countMAE(i);
            double rmse = countRMSE(i);
            int diff = Const.highest - Const.lowest;
            pred.addEstim(mae);
            pred.addEstim(mae / diff);
            pred.addEstim(rmse);
            pred.addEstim(rmse / diff);
            predictors.add(pred);
        }
        Collections.sort(predictors);
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
        StringBuilder sb = new StringBuilder("MAE \t NMAE \t RMSE \t NRMSE\n");
        for(Predictor pred: preds){
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
}
