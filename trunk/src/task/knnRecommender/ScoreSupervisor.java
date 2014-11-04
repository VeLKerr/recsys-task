
package task.knnRecommender;

import com.sun.javafx.scene.traversal.WeightedClosestCorner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import task.Score;
import task.estimations.Consts;
import task.knnRecommender.exceptions.NewUserOrItemException;
import task.learning.AverageRating;
import task.learning.WeightedAverageRating;
import task.utils.CollectionsUtils;
import task.utils.ConsoleUtils;
import task.utils.MathUtils;

/**
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ScoreSupervisor {
    private static final PearsonsCorrelation pc = new PearsonsCorrelation();
    private final RealMatrix scoreMatrix;
    private final RealMatrix[] correlations;
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
        for(int i=0; i<getSize(at.invert()); i++){
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
        label: while(cnt < k || mLimit < getSize(at)){
            List<Integer> indexes = new ArrayList<>();
            for(int i=0; i<getSize(at); i++){
                if(isReadyForAnalysis(ethalon, i, mLimit) && get(i)[sc.getId(at.invert()) - 1] != 0){
                    indexes.add(i);
                    cnt++;
                }
            }
            if(cnt >= k){
                break;
            }
            res.add(indexes);
            mLimit++;
        }
        //System.err.println(ConsoleUtils.listlistToString(res));
        return res;
    }
    
    public ScoreSupervisor(int k){
        this.scoreMatrix = new Array2DRowRealMatrix(Consts.Quantities.USERS_CNT, Consts.Quantities.ITEMS_CNT);
        correlations = new RealMatrix[2];
        this.k = k;
    }

    public void setAlgoType(AlgoType at) {
        this.at = at;
    }
    
    public void add(Score sc){
        scoreMatrix.setEntry(sc.getUserId() - 1, sc.getItemId() - 1, sc.getRating());
    }
    
    protected void countCorrelations(){
        for(AlgoType at: AlgoType.values()){
            countCorrelations(at);
        }
    }
    
    protected void countCorrelations(AlgoType at){
        int ord = at.ordinal();
        if(at == AlgoType.USER_BASED){
            correlations[ord] = pc.computeCorrelationMatrix(scoreMatrix.transpose());
        }
        else{
            correlations[ord] = pc.computeCorrelationMatrix(scoreMatrix);
        }
    }
    
//    public List<Integer> kNN(Score sc){
//        List<Integer> res = new ArrayList<>();
//        double[] row = correlations[at.ordinal()].getRow(sc.getId(at));//COLLUMN?
//        TreeMap<Integer, Double> map = new TreeMap<>();
//        for(int i=0; i<row.length; i++){
//            map.put(i, row[i]);
//        }
//        SortedSet<Entry<Integer, Double>> set = CollectionsUtils.entriesSortedByValues(map);
//        Iterator<Entry<Integer, Double>> it = set.iterator();
//        while(it.hasNext()){
//            Entry<Integer, Double> entry = it.next();
//            System.err.print(entry.getValue() + " ");
//            res.add(entry.getKey() + 1);
//            //res.add(it.next().getKey() + 1);
//        }
//        return res.subList(1, k + 1);
//    }
    
    private List<List<Integer>> preprocessingWithCopy(List<List<Integer>> list, Score sc){
        List<List<Integer>> res = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            List<Integer> l = new ArrayList<>(list.get(i).size());
            for(int j=0; j<list.get(i).size(); j++){
                if(get(list.get(i).get(j))[sc.getId(at.invert()) - 1] != 0){
                    l.set(j, list.get(i).get(j));
                }
            }
            res.add(l);
        }
        return res;
    }
    
    private void preprocessing(List<List<Integer>> list, Score sc){
        List<int[]> indexesForRemoving = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            for(int j=0; j<list.get(i).size(); j++){
                if(get(list.get(i).get(j))[sc.getId(at.invert()) - 1] == 0){
                    indexesForRemoving.add(new int[]{i, j});
                }
            }
        }
    }
    
    public double getRating(Score sc){
        double[] ethalon = get(sc.getId(at) - 1);
        try {
            //List<List<Integer>> indexes = preprocessingWithCopy(kNN(sc), sc);
            List<List<Integer>> indexes = kNN(sc);
            if(CollectionsUtils.totalSize(indexes) <= k){
                WeightedAverageRating war = new WeightedAverageRating();
                for(int i=indexes.size(); i > 0; i--){
                    for(int elem: indexes.get(i - 1)){
                        war.add(get(elem)[sc.getId(at.invert()) - 1], i);
//                        double rating = get(elem)[sc.getId(at.invert()) - 1];
//                        if(rating != 0){
//                            war.add(rating, i);
//                        }
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
                        //Если preprocessing(indexes, sc) опять не будет работать,
                        //сделать проверку на 0-рейтинг!
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
