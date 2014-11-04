
package task.learning;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class WeightedAverageRating extends AverageRating{

    public WeightedAverageRating() {
        this.cnt = 0;
        this.sum = 0;
    }
    
    public void add(double rating, int weight){
        cnt += weight;
        sum += rating * weight;
    }
}
