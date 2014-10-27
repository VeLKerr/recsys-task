
package task.knnRecommender;

import task.estimations.EstimationPool;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class UserItemBased {
    private final EstimationPool estimationPool;

    public UserItemBased(double step) {
        this.estimationPool = new EstimationPool(step);
    }
    
    
}
