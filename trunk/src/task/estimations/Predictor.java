
package task.estimations;

import java.util.ArrayList;
import java.util.List;
import task.utils.MathUtils;

/**
 * Класс, описывающий оценки работы предиктора.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Predictor implements Comparable<Predictor>, Cloneable{
    /**
     * Оценки работы предиктора:
     * <ul>
     *  <li>MAE,</li>
     *  <li>NMAE,</li>
     *  <li>RMSE,</li>
     *  <li>NRMSE.</li>
     * </ul>
     */
    private List<Double> estimations;
    /**
     * Название предиктора.
     */
    private String name;
    private int id;
    
    public Predictor(int algoId){
        this.estimations = new ArrayList<>();
        this.name = Consts.algoNames[algoId];
        this.id = algoId;
    }
    
    private Predictor(String name, List<Double> estimations){
        this.estimations = estimations;
        this.name = name;
    }
    
    /**
     * Добавить оценку.
     * @param estimation значение оценки.
     */
    public void addEstim(double estimation){
        estimations.add(estimation);
    }
    
    public String toString(boolean withName){
        StringBuilder sb = new StringBuilder();
        for(double est: estimations){
            sb.append(MathUtils.roundDouble(est)).append("\t");
        }
        if(withName){
            sb.append("- ").append(name);
        }
        return sb.toString();
    }
    
    @Override
    public String toString(){
        return toString(true);
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

    public List<Double> getEstimations() {
        return estimations;
    }

    public String getName() {
        return name;
    }
    
    public int getAlgoId(){
        return id;
    }
    
    protected void addPredictor(Predictor pred){
        if(!this.name.equals(pred.name)){
            System.err.println("ERROR! Unable to calculate sum of the different types of predictors!");
        }
        else{
            for(int i=0; i<estimations.size(); i++){
                estimations.set(i, estimations.get(i) + pred.estimations.get(i));
            }
        }
    }
    
    protected void divide(int number){
        for(int i=0; i<estimations.size(); i++){
            estimations.set(i, estimations.get(i) / number);
        }
    }
    
    @Override
    public Predictor clone() throws CloneNotSupportedException{
        List<Double> newEstimations = new ArrayList<>(estimations.size());
        for(double est: estimations){
            newEstimations.add(est);
        }
        Predictor res = (Predictor)super.clone();
        res.name = new String(this.name);
        res.estimations = newEstimations;
        return res;
    }
}
