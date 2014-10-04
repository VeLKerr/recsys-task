
package task.estimations;

/**
 *
 * @author VeLKerr
 */
public enum MetricType {
    TruePositive{
        @Override
        public int toInt(){
            return 0;
        }
    },
    FalseNegative{
        @Override
        public int toInt(){
            return 1;
        }
    },
    FalsePositive{
        @Override
        public int toInt(){
            return 2;
        }
    },
    TrueNegative{
        @Override
        public int toInt(){
            return 3;
        }
    };
    
    public abstract int toInt();
}
