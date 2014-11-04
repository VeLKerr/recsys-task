
package task.knnRecommender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import task.Score;
import task.estimations.Consts;
import task.knnRecommender.exceptions.NewUserOrItemException;
import task.learning.WeightedAverageRating;
import task.utils.CollectionsUtils;
import task.utils.MathUtils;

/**
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ScoreSupervisor {
    private static final PearsonsCorrelation pc = new PearsonsCorrelation();
    private final RealMatrix scoreMatrix;
    private final int k;
    private AlgoType at;
    
    private static int getSize(AlgoType at){
        if(at == AlgoType.USER_BASED){
            return Consts.Quantities.USERS_CNT;
        }
        return Consts.Quantities.ITEMS_CNT;
    }
    
    private double[] get(int number){
        if(at == AlgoType.USER_BASED){
            return scoreMatrix.getRow(number);
        }
        return scoreMatrix.getColumn(number);
    }
    
    private boolean isReadyForAnalysis(double[] ethalon, int number, int mismatchesLimit) throws NewUserOrItemException{
        int mismatches = 0;
        boolean isEmpty = true;
        double[] forCompare = get(number);
        int matrSize = getSize(at.invert());
        for(int i=0; i<matrSize; i++){
            if(ethalon[i] != 0){
                isEmpty = false;
                if(forCompare[i] == 0){
                    mismatches++;
                }
            }
            if(mismatches > mismatchesLimit){
                return false;
            }
        }
        if(isEmpty){
            throw new NewUserOrItemException();
        }
        return mismatches >= mismatchesLimit;
    }
    
    private List<List<Integer>> kNN(Score sc) throws NewUserOrItemException{
        List<List<Integer>> res = new ArrayList<>();
        double[] ethalon = get(sc.getId(at) - 1);
        int cnt = 0;
        int mLimit = 0;
        while(cnt < k && mLimit < getSize(at) / Consts.Advaced.MAX_MISMATCHES_DENOMINATOR){
            List<Integer> indexes = new ArrayList<>();
            for(int i=0; i<getSize(at); i++){
                if(i != sc.getId(at) - 1 && 
                   isReadyForAnalysis(ethalon, i, mLimit) && 
                   get(i)[sc.getId(at.invert()) - 1] != 0){
                    indexes.add(i);
                    cnt++;
                }
            }
            res.add(indexes);
            mLimit++;
        }
        if(CollectionsUtils.deepIsEmpty(res)){
            throw new NewUserOrItemException();
        }
        return res;
    }
    
    public ScoreSupervisor(int k){
        this.scoreMatrix = new Array2DRowRealMatrix(Consts.Quantities.USERS_CNT, Consts.Quantities.ITEMS_CNT);
        this.k = k;
    }

    public void setAlgoType(AlgoType at) {
        this.at = at;
    }
    
    public void add(Score sc){
        scoreMatrix.setEntry(sc.getUserId() - 1, sc.getItemId() - 1, sc.getRating());
    }
    
    public double getRating(Score sc){
        double[] ethalon = get(sc.getId(at) - 1);
        try {
            List<List<Integer>> indexes = kNN(sc);
            if(CollectionsUtils.totalSize(indexes) <= k){
                WeightedAverageRating war = new WeightedAverageRating();
                for(int i=indexes.size(); i > 0; i--){
                    for(int elem: indexes.get(i - 1)){
                        war.add(get(elem)[sc.getId(at.invert()) - 1], i);
                    }
                }
                return war.avg();
            }
            else{
                class WeightedCorreledIorU implements Comparable<WeightedCorreledIorU>{
                    final int weight;
                    final int index;
                    final double correl;

                    public WeightedCorreledIorU(int index, int weight) {
                        this.weight = weight;
                        this.index = index;
                        this.correl = pc.correlation(get(index), ethalon);
                    }
                    
                    @Override
                    public int compareTo(WeightedCorreledIorU w) {
                        if(correl > w.correl){
                            return 1;
                        }
                        else if(correl < w.correl){
                            return -1;
                        }
                        return 0;
                    }
                }
                List<WeightedCorreledIorU> ious = new ArrayList<>();
                for(int i=indexes.size(); i>0; i--){
                    for(int elem: indexes.get(i - 1)){
                        ious.add(new WeightedCorreledIorU(elem, i));
                    }
                }
                Collections.sort(ious);
                WeightedAverageRating war = new WeightedAverageRating();
                for(WeightedCorreledIorU iou: ious.subList(0, k)){
                    war.add(get(iou.index)[sc.getId(at.invert()) - 1], iou.weight);
                }
                return war.avg();
            }
        } catch (NewUserOrItemException ex) {
            return MathUtils.randomRating();
        }
    }
}
