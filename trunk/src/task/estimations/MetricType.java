
package task.estimations;

/**
 *
 * @author VeLKerr
 */
public enum MetricType {
    TruePositive{
        public int toInt(){
            return 0;
        }
    },
    FalseNegative{
        public int toInt(){
            return 1;
        }
    },
    FalsePositive{
        public int toInt(){
            return 2;
        }
    },
    TrueNegative{
        public int toInt(){
            return 3;
        }
    };
    
    public int toInt(){
        return MetricType.this.toInt();
    }
}
