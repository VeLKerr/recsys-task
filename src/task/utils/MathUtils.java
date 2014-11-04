/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import task.estimations.Consts;

/**
 * Класс утилит.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public abstract class MathUtils {    
    public static abstract class AvgCountMethods{
        public static double[] avgListD(List<double[]> list){
            double[] res = new double[list.get(0).length];
            int size = list.size();
            for(double[] mas: list){
                for(int j=0; j<mas.length; j++){
                    res[j] += mas[j];
                }
            }
            for(int i=0; i<res.length; i++){
                res[i] /= size;
            }
            return res;
        }
        
        public static double[] avgListL(List<long[]> list){
            double[] res = new double[list.get(0).length];
            int size = list.size();
            for(long[] mas: list){
                for(int j=0; j<mas.length; j++){
                    res[j] += mas[j];
                }
            }
            for(int i=0; i<res.length; i++){
                res[i] /= size;
            }
            return res;
        }

        /**
         *
         * @param mas the value of mas
         * @return the double
         */
        public static double sum(int[] mas) {
            int res = 0;
            for (int el : mas) {
                res += el;
            }
            return res;
        }
    }
    /**
     * Экземпляр генератора случайных чисел.
     */
    private static final Random rand = new Random();
    
    /**
     * Генерация случайной пользовательской оценки в пределах
     * <code>Const.lowest</code> до <code>Const.highest</code>.
     * @return значение оценки.
     */
    public static int randomRating(){
        return rand.nextInt(Consts.highest) + Consts.lowest;
    }
    
    /**
     * Округление дробного числа до целого.
     * @param value дробное число.
     * @return целое число.
     */
    public static int roundToInt(double value){
        return (int)roundDouble(value, 0);
    }
    
    /**
     * Округление числа.
     * @param value дробное число.
     * @param symbolsAfterComma кол-во знаков после запятой в округлённом числе.
     * @return округлённое дробное число.
     */
    private static double roundDouble(double value, int symbolsAfterComma){
        return new BigDecimal(value).setScale(symbolsAfterComma, RoundingMode.HALF_UP).doubleValue();
    }
    
    public static double roundDouble(double value){
        return roundDouble(value, Consts.symbolsAfterComma);
    }
}
