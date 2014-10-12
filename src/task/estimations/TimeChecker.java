
package task.estimations;

/**
 * Класс для автоматизированной замерки врмени рассчёта
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class TimeChecker {
    private static final long UNDEFINED = -1L;
    
    public enum TimePrecision{
        NANOSEC{
            @Override
            public long getDenominator(){
                return 1;
            }
        },
        MICROSEC{
            @Override
            public long getDenominator(){
                return 1000;
            }
        },
        MILLISEC{
            @Override
            public long getDenominator(){
                return 1000000;
            }
        },
        SEC{
            @Override
            public long getDenominator(){
                return (long)Math.pow(10, 9);
            }
        },
        MINUTES{
            @Override
            public long getDenominator(){
                return SEC.getDenominator() * 60;
            }
        },
        HOURS{
            @Override
            public long getDenominator(){
                return MINUTES.getDenominator() * 60;
            }
        },
        DAYS{
            @Override
            public long getDenominator(){
                return HOURS.getDenominator() * 24;
            }
        };
        
        public abstract long getDenominator();
    }
    
    private long[] times;
    private long lastChecking;
    private int currentCheckingCnt;

    public TimeChecker(int checkCnt) {
        this.times = new long[checkCnt];
        clearCounters();
    }
    
    public final void clearCounters(){
        lastChecking = UNDEFINED;
        currentCheckingCnt = 0;
    }
    
    public void check(){
        if(lastChecking == UNDEFINED){
            lastChecking = System.nanoTime();
        }
        else{
            long check = System.nanoTime();
            times[currentCheckingCnt] = check - lastChecking;
            currentCheckingCnt++;
            lastChecking = check;
        }
    }

    public long[] getTimes() {
        return times;
    }
    
    public double[] getTimes(TimePrecision tp){
        double[] res = new double[times.length];
        long denom = tp.getDenominator();
        for(int i=0; i<times.length; i++){
            res[i] = times[i] / denom;
        }
        return res;
    }
}
