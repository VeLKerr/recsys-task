
package task.estimations;

import task.Task;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public abstract class Consts {
    /**
     * Названия предикторов. Для корректной работы программы, название
     * эталонного предиктора должно иметь индекс <code>0</code>.
     */
    public static final String[] algoNames = {"average values", "average over the items", "average over the users", "random rating", "baseline predictor", "baseline predictor with BETA=" + Task.beta, "average on gender", "average over the users with the same gender"};
    /**
     * Границы шкалы пользовательских оценок.
     */
    public static final int highest = 5;
    public static final int lowest = 1;
}
