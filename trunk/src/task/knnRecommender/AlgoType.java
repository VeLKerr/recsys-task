
package task.knnRecommender;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public enum AlgoType {
    USER_BASED(true),
    ITEM_BASED(false);
    
    private final boolean flag;

    private AlgoType(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }
}
