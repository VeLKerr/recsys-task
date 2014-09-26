
package task.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import task.estimations.EstimationPool;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ConsoleUtils {
    private static int symbCnt = 74;
    private ConsoleUtils(){
    }
    
    private static void strOutput(char symb){
        for(int i=0; i<symbCnt; i++){
            System.out.print(symb);
        }
        System.out.println("");
    }
    
    public static void outputResults(int i){
        strOutput('=');
        System.out.println("\t\t\tTest #" + i);
        strOutput('=');
    }
    
    public static void outputAverages(){
        strOutput('+');
        System.out.println("\t\tAVERAGE VALUES");
        strOutput('+');
    }
    
    public static void outputGaining(){
        strOutput('*');
        System.out.println("\t\tGAINING PERCENTAGE");
        strOutput('*');
    }
    
    public static void outputPercentageMap(Map<String, Double> percentage){
        for(Entry<String, Double> entry: percentage.entrySet()){
            System.out.println(MathUtils.roundDouble(entry.getValue(), 
                    EstimationPool.Const.symbolsAfterComma + 1) + "%\t- " + entry.getKey());
        }
    }
    
    public static StringBuilder listIntegersToString(List<Integer> list){
        StringBuilder sb = new StringBuilder();
        for(int value: list){
            sb.append(value).append(", ");
        }
        return sb;
    }
}
