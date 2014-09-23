
package task.estimations;

import java.util.ArrayList;
import java.util.List;
import task.utils.Utils;
import static task.Task.beta;

/**
 * Singleton-класс для храниения спрогнозированных оценок и эталонной оценки пользователя.
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
        private static final int symbolsAfterComma = 3;
        /**
         * Кол-во использованных алгоритмов.
         */
        private static final int algoCnt = 6;
        /**
         * Кол-во использованных оценок.
         */
        private static final int estCnt = 4;
    }
    /**
     * Instance для Singleton-класса.
     */
    private static EstimationPool instance;
    /**
     * Матрица прогнозов оценок, рассчитанных с помощью разных алгоритмов.
     */
    private final List<List<Integer>> estimations;

    private EstimationPool() {
        this.estimations = new ArrayList<>();
    }
    
    public static EstimationPool getEstimationPool(){
        if(instance == null){
            instance = new EstimationPool();
        }
        return instance;
    }
    
    /**
     * Создать новую строку в матрице оценок.
     */
    public void createNewEmptyEstimation(){
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
    public List<List<Double>> countEstimations(){
        List<List<Double>> algoEst = new ArrayList<>();
        for(int i=1; i<Const.algoCnt + 1; i++){
            List<Double> estim = new ArrayList<>();
            double mae = countMAE(i);
            double rmse = countRMSE(i);
            int diff = Const.highest - Const.lowest;
            estim.add(mae);
            estim.add(mae / diff);
            estim.add(rmse);
            estim.add(rmse / diff);
            algoEst.add(estim);
        }
        return algoEst;
    }
    
    /**
     * Перевод матрицы оценок в строку.
     * @param estimates матрица оценок.
     * @return строковое представление матрицы оценок.
     */
    public String estimatesToString(List<List<Double>> estimates){
        StringBuilder sb = new StringBuilder("MAE \t NMAE \t RMSE \t NRMSE\n");
        for(int i=0; i<Const.algoCnt; i++){
            for(double est: estimates.get(i)){
                sb.append(Utils.roundDouble(est, Const.symbolsAfterComma));
                sb.append("\t");
            }
            switch(i){
                case 0:{
                    sb.append("- average values");
                    break;
                }
                case 1:{
                    sb.append("- average over the items");
                    break;
                }
                case 2:{
                    sb.append("- average over the users");
                    break;
                }
                case 3:{
                    sb.append("- baseline predictor");
                    break;
                }
                case 4:{
                    sb.append("- baseline predictor with BETA=");
                    sb.append(beta);
                    break;
                }
                case 5:{
                    sb.append("- random rating");
                    break;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
