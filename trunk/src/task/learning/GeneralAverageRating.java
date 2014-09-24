
package task.learning;

import java.util.ArrayList;
import java.util.List;
import task.Score;
import task.utils.MathUtils;

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
            ious.add(it);

        }
        if(!userFlag){
            ItemOrUser us = new ItemOrUser(score.getUserId(), true);
            us.add(score.getRating());
            ious.add(us);
        }
    }
    
    /**
     * Рассчитать среднюю оценку.
     * @param id пользователя или item'a.
     * @param isUser флаг, указывающий на то, пользователь это или item.
     * @return среднюю оценку.
     */
    public double avgOn(int id, boolean isUser){
        for(ItemOrUser iou: ious){
            if(iou.getId() == id && iou.isUser() == isUser){
                return iou.avg();
            }
        }
        return MathUtils.randomRating();
    }
    
    /**
     * Рассчитать среднюю оценку.
     * @param id пользователя или item'a.
     * @param isUser флаг, указывающий на то, пользователь это или item.
     * @param beta коэффициент затухания Бета.
     * @return среднюю оценку.
     */
    private double avgOn(int id, boolean isUser, double beta){
        for(ItemOrUser iou: ious){
            if(iou.getId() == id && iou.isUser() == isUser){
                return iou.avg(beta);
            }
        }
        return MathUtils.randomRating();
    }
    
    private double countGeneralPart(double beta){
        double UserSum = 0.0;
        int userCnt = 0;
        for(ItemOrUser iou: ious){
            if(iou.isUser()){
                UserSum += iou.avg(beta);
                userCnt++;
            }
        }
        return UserSum / userCnt;
    }
    
    public double countBaselinePredictor(int userId, int itemId){
        return avgOn(userId, true) + avgOn(itemId, false) - 
               countGeneralPart(0.0);
    }
    
    public double countBaselinePredictor(int userId, int itemId, double beta){
        double predictor = countGeneralPart(beta);
        int ui = 0;
        int iu = 0;
        //Флаги указывают на то, нашёлся ли в обучающей выборке такой 
        //пользователь или такой item. Если не нашёлся, то соотв. слагаемое
        //генерируем рандомно в диапазоне от 1 до 5.
        boolean itemFlag = false;
        boolean userFlag = false;
        for(ItemOrUser iou: ious){
            if(iou.getId() == userId && iou.isUser()){
                predictor += iou.avg(beta);
                ui = iou.cnt;
                userFlag = true;
            }
            else if(iou.getId() == itemId && !iou.isUser()){
                predictor += iou.avg(beta);
                iu = iou.cnt;
                itemFlag = true;
            }
        }
        if(!userFlag){
            predictor += MathUtils.randomRating();
            ui = 1;
        }
        if(!itemFlag){
            predictor += MathUtils.randomRating();
            iu = 1;
        }
        predictor += muMultiplier(ui, iu, beta) * avg();
        return predictor;
    }
    
    private double muMultiplier(int ui, int iu, double beta){
        double uTerm = muTerm(ui, beta);
        double iTerm = muTerm(iu, beta);
        return uTerm * iTerm + 1 - iTerm - uTerm;
    }
    
    private double muTerm(int number, double beta){
        return (double) number / (number + beta);
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
    
    /**
     * Для тестирования. 
     * @return 
     */
    public int _size(){
        return ious.size();
    }
    
    /**
     * Для тестирования. 
     * @return 
     */
    public boolean _check(){
        for(int i=0; i<ious.size(); i++){
            for(int j=0; j<ious.size(); j++){
                if(i != j && 
                   ious.get(i).getId() == ious.get(j).getId() &&
                   ious.get(i).isUser() == ious.get(j).isUser()){
                    System.out.println(i + " " + j);
                    return false;
                }
            }
        }
        return true;
    }
}
