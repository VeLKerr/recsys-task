
package task.knnRecommender;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public enum AlgoType {
    USER_BASED(true){
        @Override
        public AlgoType invert() {
            return ITEM_BASED;
        }
    },
    ITEM_BASED(false){
        @Override
        public AlgoType invert(){
            return USER_BASED;
        }
    };
    
    private final boolean flag;

    private AlgoType(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }
    
    public abstract AlgoType invert();
}
