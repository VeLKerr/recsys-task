package task;

import task.utils.ConsoleUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import task.estimations.EstimationPool;
import task.estimations.Metrics;
import task.estimations.Predictor;
import task.learning.GeneralAverageRating;
import task.learning.Users;
import task.utils.FileNameBuilder;
import task.utils.MathUtils;

/**
 * Главный класс проограммы
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Task {
    /**
     * Путь к файлам с выборками.
     */
    public static final String path = "ml-100k/";
    /**
     * Кол-во прогонов обучения-тестирования.
     */
    private static final int testCnt = 5;
    
    /**
     * Коэффициент затухания Бета.
     */
    public static final double beta = Math.pow(10, 6);
    
    private static void fillUsers(String filename) throws FileNotFoundException, IOException{
        Users users = Users.getInstance();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path + filename)));
        String line = null;
        while((line = br.readLine()) != null){
            users.add(line);
        }
    }

    /**
     * Главная функция программы
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        fillUsers("u.user");
        List<EstimationPool> estimationPools = new ArrayList<>();
        FileNameBuilder fnb = FileNameBuilder.getBuilder();
        String line = null;
        Score sc  = null;
        for(int i=1; i<=testCnt; i++){//change to testCnt
            //Обучение
            GeneralAverageRating gav = new GeneralAverageRating();
            fnb.setParameters(i, false);
            File data = new File(path + fnb.buildFName());
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            while((line = br.readLine()) != null){
                sc = new Score(line);
                gav.add(sc);
            }
            //Тестировка и дообучение
            EstimationPool est = new EstimationPool();
            fnb.setIsTest(true);
            data = new File(path + fnb.buildFName());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            while((line = br.readLine()) != null){
                sc = new Score(line);
                est.createNewEmptyEstimationsRow();
                est.setAverage(gav.avg()); //эта оценка практически не меняется, 
                //но я всё равно рассчитываю её каждый раз т.к. теоретически она
                //измениться может.
                est.setAverageOverItems(gav.avgOn(sc.getItemId(), false)); //среднее по всем
                //item'ам для данного пользователя.
                est.setAverageOverUsers(gav.avgOn(sc.getUserId(), true));
                est.setAverageRandom(MathUtils.randomRating());
                est.setBaselinePredictor(gav.countBaselinePredictor(
                        sc.getUserId(), sc.getItemId()));
                est.setBaselinePredictorWithBeta(gav.countBaselinePredictor(
                        sc.getUserId(), sc.getItemId(), beta));
                est.setAvgOnGender(gav.avgOn(sc.getGender()));
                est.setAvgOnUsersWithGender(gav.avgOnUsersWithGender(sc.getUserId(), sc.getGender()));
                est.setTrueRating(sc.getRating());
                est.takeIntoAccMetrics();
//                gav.add(sc); //дообучение системы
            }
            ConsoleUtils.outputResults(i);
            //вывод матрицы оценок погрешностей алгоритмов.
            System.out.println(est.estimatesToString());
//            est.countEstimates();
//            System.out.println(EstimationPool.listPredToString(est.getPredictors()));
            estimationPools.add(est);
        }
        ConsoleUtils.outputAverages();
        List<Predictor> preds = EstimationPool.avgPredictors(estimationPools);
        List<Metrics> metricsList = EstimationPool.avgMetrics(estimationPools);
        System.out.println(EstimationPool.listPredMetrToString(preds, metricsList));
//        System.out.println(EstimationPool.listPredToString(preds));
        ConsoleUtils.outputGaining();
        ConsoleUtils.outputPercentageMap(EstimationPool.gainingPercentage(preds));
    }
}
