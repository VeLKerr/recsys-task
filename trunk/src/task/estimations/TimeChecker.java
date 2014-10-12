
package task.estimations;

/**
 * Класс для автоматизированной замерки врмени работы.
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
            
            @Override
            public String toString(){
                return "ns";
            }
        },
        MICROSEC{
            @Override
            public long getDenominator(){
                return 1000;
            }
            
            @Override
            public String toString(){
                return "mcrs";
            }
        },
        MILLISEC{
            @Override
            public long getDenominator(){
                return 1000000;
            }
            
            @Override
            public String toString(){
                return "mls";
            }
        },
        SEC{
            @Override
            public long getDenominator(){
                return (long)Math.pow(10, 9);
            }
            
            @Override
            public String toString(){
                return "sec";
            }
        },
        MINUTES{
            @Override
            public long getDenominator(){
                return SEC.getDenominator() * 60;
            }
            
            @Override
            public String toString(){
                return "min";
            }
        },
        HOURS{
            @Override
            public long getDenominator(){
                return MINUTES.getDenominator() * 60;
            }
            
            @Override
            public String toString(){
                return "h";
            }
        },
        DAYS{
            @Override
            public long getDenominator(){
                return HOURS.getDenominator() * 24;
            }
            
            @Override
            public String toString(){
                return "d";
            }
        };
        
        public abstract long getDenominator();
    }
    
    private long[] times;
    private long lastChecking;
    private int currentCheckingCnt;

    public TimeChecker(int processCnt) {
        this.times = new long[processCnt];
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
    
    public double[] getTimes(TimePrecision tp){
        double[] res = new double[times.length];
        long denom = tp.getDenominator();
        if(tp.equals(TimePrecision.NANOSEC)){
            for(int i=0; i<times.length; i++){
                res[i] = times[i];
            }
        }
        else{
            for(int i=0; i<times.length; i++){
                res[i] = times[i] / denom;
            }
        }
        return res;
    }
}
