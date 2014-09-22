
package task.learning;

import java.util.ArrayList;
import java.util.List;
import task.Score;
import task.utils.Utils;

/**
 * Класс для хранения всевозможных суммарных оценок и кол-ва оценок.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class GeneralAverageRating extends AverageRating{
    /**
     * Instance для Singleton-класса.
     */
    private static GeneralAverageRating instance;
    /**
     * Коллекция объектов, сдержащих суммарные оценки и их кол-во:
     * <ul>
     *  <li>по каждому item'у,</li>
     *  <li>по каждому пользователю.</li>
     * </ul>
     */
    private final List<ItemOrUser> ious;

    public GeneralAverageRating() {
        super();
        this.ious = new ArrayList<>();
    }
    
    /**
     * Добавить пользовательскую оценку в хранилище. При этом изменится счётчик и сумма:
     * <ul>
     *  <li>всех оценок,</li>
     *  <li>оценок по пользователю, к которому относилась добавляемая оценка,</li>
     *  <li>оценок по item'у, к которому относилась добавляемая оценка.</li>
     * </ul>
     * @param score пользовательская оценка, прочитанная из файла u.data или
     * другого файла с идентичной структурой.
     */
    public void add(Score score){
        add(score.getRating());
        boolean itemFlag = false;
        boolean userFlag = false;
        for(ItemOrUser iou: ious){
            if(score.getItemId() == iou.getId() && !iou.isUser()){
                iou.add(score.getRating());
                itemFlag = true;
            }
            if(score.getUserId() == iou.getId() && iou.isUser()){
                iou.add(score.getRating());
                userFlag = true;
            }
            if(itemFlag && userFlag){
                break;
            }
        }
        if(!itemFlag){
            ItemOrUser it = new ItemOrUser(score.getItemId(), false);
            it.add(score.getRating());
            ItemOrUser us = new ItemOrUser(score.getUserId(), true);
            us.add(score.getRating());
            ious.add(it);
            ious.add(us);
        }
    }
    
    /**
     * Рассчитать среднюю оценку.
     * @param id пользователя или item'a.
     * @param isUser флаг, указывающий на то, пользователь это или item.
     * @return среднюю оценку.
     */
    public int avgOn(int id, boolean isUser){
        for(ItemOrUser iou: ious){
            if(iou.getId() == id && iou.isUser() == isUser){
                return iou.avg();
            }
        }
        return Utils.randomRating();
    }
    
    /**
     * Получить сумму всех оценок, присутствующих в хранилище.
     * @return сумма всех оценок, присутствующих в хранилище.
     */
    public int getSum() {
        return sum;
    }
    
    /**
     * Получить кол-во всех оценок, присутствующих в хранилище.
     * @return кол-во всех оценок, присутствующих в хранилище.
     */
    public int getCnt() {
        return cnt;
    }
}
