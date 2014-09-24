/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;
import task.estimations.EstimationPool;

/**
 * Класс утилит.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public abstract class MathUtils {
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
        return rand.nextInt(EstimationPool.Const.highest) + EstimationPool.Const.lowest;
    }
    
    /**
     * Округление дробного числа до целого.
     * @param value дробное число.
     * @return целое число.
     */
    public static int round(double value){
        return (int)roundDouble(value, 0);
    }
    
    /**
     * Округление числа.
     * @param value дробное число.
     * @param symbolsAfterComma кол-во знаков после запятой в округлённом числе.
     * @return округлённое дробное число.
     */
    public static double roundDouble(double value, int symbolsAfterComma){
        return new BigDecimal(value).setScale(symbolsAfterComma, RoundingMode.HALF_UP).doubleValue();
    }
}
