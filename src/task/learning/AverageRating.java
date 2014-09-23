
package task.learning;

import task.utils.Utils;

/**
 * Класс для хранения данных для рассчёта средних оценок.
 * Среднее арифметическое рассчитывается как сумма чисел, делённая на их кол-во.
 * Поэтому, при рассчёте среднего нужно каждый раз проходить всю выборку, искать
 * необходимые строки, а затем считать суммарную оценку и кол-во. Это забирает 
 * дополнительное время. Чтобы избежать этого, был создан этот класс.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class AverageRating {
    /**
     * Суммарная оценка.
     */
    protected int sum;
    /**
     * Счётчик оценок.
     */
    protected int cnt;
    
    protected AverageRating(){
        this.sum = 0;
        this.cnt = 0;
    }
    
    /**
     * Добавить оценку. При этом возрастёт сумма оценок и инкрементируется
     * счётчик.
     * @param rating значение оценки.
     */
    protected void add(int rating){
        cnt++;
        sum += rating;
    }
    
    /**
     * Расчёт среднего значения оценок.
     * @return среднее значение оценок.
     */
    public double avg(){
        return (double) sum / (double)cnt;
    }
}
