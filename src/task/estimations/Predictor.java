
package task.estimations;

import java.util.ArrayList;
import java.util.List;
import static task.estimations.EstimationPool.Const.symbolsAfterComma;
import task.utils.Utils;

/**
 * Класс, описывающий оценки работы предиктора.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Predictor implements Comparable<Predictor>{
    /**
     * Оценки работы предиктора:
     * <ul>
     *  <li>MAE,</li>
     *  <li>NMAE,</li>
     *  <li>RMSE,</li>
     *  <li>NRMSE.</li>
     * </ul>
     */
    private final List<Double> estimations;
    /**
     * Название предиктора.
     */
    private final String name;
    
    public Predictor(String name){
        this.estimations = new ArrayList<>();
        this.name = name;
    }
    
    /**
     * Добавить оценку.
     * @param estimation значение оценки.
     */
    public void addEstim(double estimation){
        estimations.add(estimation);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(double est: estimations){
            sb.append(Utils.roundDouble(est, symbolsAfterComma)).append("\t");
        }
        sb.append("- ").append(name);
        return sb.toString();
    }

    @Override
    public int compareTo(Predictor o) {
        if(estimations.get(0) > o.estimations.get(0)){
            return 1;
        }
        else if(estimations.get(0) < o.estimations.get(0)){
            return -1;
        }
        else{
            return 0;
        }
    }
    
    public void addPredictor(Predictor pred){
        if(!this.name.equals(pred.name)){
            System.err.println("ERROR! Unable to calculate sum of the different types of predictors!");
        }
        else{
            for(int i=0; i<estimations.size(); i++){
                estimations.set(i, estimations.get(i) + pred.estimations.get(i));
            }
        }
    }
    
    public void divide(int number){
        for(int i=0; i<estimations.size(); i++){
            estimations.set(i, estimations.get(i) / number);
        }
    }
}