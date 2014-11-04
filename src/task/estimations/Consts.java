
package task.estimations;

import task.Task;

/**
 * Класс глобальных констант для всего проекта.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public abstract class Consts {
    public static final int UNDEFINED = Integer.MAX_VALUE;
    /**
     * Названия предикторов. Для корректной работы программы, название
     * эталонного предиктора должно иметь индекс <code>0</code>.
     */
    public static final String[] algoNames = {
        "average over all", 
        "average over the items", 
        "average over the users", 
        "random rating", 
        "baseline predictor", 
        "baseline predictor with BETA=" + Task.beta, 
        "average on gender", 
        "avg over the users with the same gender",
        "user-based kNN",
        "item-based kNN"
    };
    /**
     * Границы шкалы пользовательских оценок.
     */
    public static final int highest = 5;
    public static final int lowest = 1;
    
    public static final double beta = 0.0;
    /**
     * Кол-во символов после запятой при округлении десятичных дробей.
     */
    public static final int symbolsAfterComma = 3;
    
    public static abstract class Delimiters{
        public static final int initialDelimiter = 2;
        public static final int finalDelimiter = 5;
    }
    
    public static abstract class Quantities{ //TODO: Реализовать чтение из файла!
        public static final int USERS_CNT = 943;
        public static final int ITEMS_CNT = 1682;
        public static final int RATINGS_CNT = 100000;
    }
}
