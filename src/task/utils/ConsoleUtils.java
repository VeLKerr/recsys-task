
package task.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ConsoleUtils {
    private static final int symbCnt = 80;
    private ConsoleUtils(){
    }
    
    public static String strOutput(char symb){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<symbCnt; i++){
            sb.append(symb);
        }
        return sb.toString();
    }
    
    public static void outputResults(int i){
        System.out.println(strOutput('='));
        System.out.println("\t\t\tTest #" + i);
        System.out.println(strOutput('='));
    }
    
    public static void outputAverages(){
        System.out.println(strOutput('+'));
        System.out.println("\t\tAVERAGE VALUES");
        System.out.println(strOutput('+'));
    }
    
    public static void outputGaining(){
        System.out.println(strOutput('*'));
        System.out.println("\t\tGAINING PERCENTAGE");
        System.out.println(strOutput('*'));
    }
    
    public static void outputPercentageMap(Map<String, Double> percentage){
        for(Entry<String, Double> entry: percentage.entrySet()){
            System.out.println(MathUtils.roundDouble(entry.getValue()) + "%\t- " + entry.getKey());
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
