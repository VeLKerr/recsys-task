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
import task.estimations.Consts;
import task.estimations.EstimationPool;
import task.estimations.Metrics;
import task.estimations.Predictor;
import task.estimations.TimeChecker;
import task.knnRecommender.AlgoType;
import task.knnRecommender.ScoreSupervisor;
import task.learning.GeneralAverageRating;
import task.learning.Users;
import task.svdRecommender.SVDBuilder;
import task.utils.FileNameBuilder;
import task.utils.MathUtils;

/**
 * Главный класс проограммы
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Task {
    private static final TimeChecker tc = new TimeChecker(1);
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
    private static final boolean isFirstWay = false;
    
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
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //System.out.print("Input double step for computing metrics: ");
        //double step = Double.parseDouble(br.readLine());
        double step = 1; //УБРАТЬ!!!!!
        fillUsers("u.user");
        List<EstimationPool> estimationPools = new ArrayList<>();
        FileNameBuilder fnb = FileNameBuilder.getBuilder();
        String line = null;
        Score sc  = null;
        for(int i=1; i<=1; i++){//change to testCnt
            //Обучение
            GeneralAverageRating gav = new GeneralAverageRating();
            ScoreSupervisor scoreSupervisor = new ScoreSupervisor(Consts.KNN.k);
            fnb.setParameters(i, false);
            File data = new File(path + fnb.buildFName());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            while((line = br.readLine()) != null){
                sc = new Score(line);
                gav.add(sc);
                scoreSupervisor.add(sc);
            }
            //Тестировка
            SVDBuilder svdb = new SVDBuilder(scoreSupervisor);
            EstimationPool est = new EstimationPool(step);
            fnb.setIsTest(true);
            data = new File(path + fnb.buildFName());
            br = new BufferedReader(new InputStreamReader(new FileInputStream(data)));
            int __cnter = 0;
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
//                scoreSupervisor.setAlgoType(AlgoType.USER_BASED);
//                est.setKNN(scoreSupervisor.getRating(sc), AlgoType.USER_BASED);
//                scoreSupervisor.setAlgoType(AlgoType.ITEM_BASED);
//                est.setKNN(scoreSupervisor.getRating(sc), AlgoType.ITEM_BASED);
                est.setKNN(gav.avg(), AlgoType.USER_BASED);
                est.setKNN(gav.avg(), AlgoType.ITEM_BASED);
                est.setSimpleSVD(svdb.getSimplePrediciton(sc));
                est.setTrueRating(sc.getRating());
                est.takeIntoAccMetrics();
                System.out.println(__cnter);
                __cnter++;
//                gav.add(sc); //дообучение системы
            }
            ConsoleUtils.outputResults(i);
            //вывод матрицы оценок погрешностей алгоритмов.
            System.out.println(est.estimatesToString(isFirstWay));
//            est.countEstimates();
//            System.out.println(EstimationPool.listPredToString(est.getPredictors()));
            estimationPools.add(est);
        }
        ConsoleUtils.outputAverages();
        List<Predictor> preds = EstimationPool.avgPredictors(estimationPools);
        List<Metrics> metrics1List = EstimationPool.avgMetrics(estimationPools, true);
        List<Metrics> metrics2List = EstimationPool.avgMetrics(estimationPools, false);
        double[] times = EstimationPool.avgTimes(estimationPools);
        System.out.print(EstimationPool.listPredToString(preds));
        System.out.println(ConsoleUtils.strOutput('-'));
        System.out.print(EstimationPool.listAllMetrToString(preds, metrics1List, metrics2List));
        System.out.println(EstimationPool.timesToString(times));
//        System.out.println(EstimationPool.listPredToString(preds));
        ConsoleUtils.outputGaining(true);
        ConsoleUtils.outputPercentageMap(EstimationPool.gainingPercentage(preds), 2);
    }
}
