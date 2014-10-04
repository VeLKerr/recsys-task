
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
     * Коллекция объектов, сдержащих суммарные оценки и их кол-во:
     * <ul>
     *  <li>по каждому item'у,</li>
     *  <li>по каждому пользователю.</li>
     * </ul>
     */
    private final List<ItemOrUser> ious;
    private final ItemOrUser[] general;

    public GeneralAverageRating() {
        super();
        this.ious = new ArrayList<>();
        this.general = new ItemOrUser[2];
        createGenderDependentIous();
    }
    
    private void createGenderDependentIous(){
        general[0] = new ItemOrUser(true);
        general[1] = new ItemOrUser(false);
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
        general[genderToIntLocal(score.getGender())].add(score.getRating());
        boolean itemFlag = false;
        boolean userFlag = false;
        int itemAddingCnt = 0;
        for(ItemOrUser iou: ious){
            if(score.getItemId() == iou.getId() && !iou.isUser()){
                iou.add(score.getRating());
                itemAddingCnt++;
                if(itemAddingCnt == 2){
                    itemFlag = true;
                }
            }
            if(score.getUserId() == iou.getId() && iou.isUser()){
                iou.add(score.getRating());
                userFlag = true;
            }
            if(itemFlag && userFlag){
                break;
            }
        }
        if(!itemFlag){ //TODO: РАСПАРАЛЛЕЛИТЬ!!!!
            ItemOrUser it = new ItemOrUser(score.getItemId(), false);
            ItemOrUser genderedIt = new ItemOrUser(score.getItemId(), false, score.getGender());
            int rating = score.getRating();
            it.add(rating);
            genderedIt.add(rating);
            ious.add(it);
            ious.add(genderedIt);
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
            if(iou.getId() == id && iou.isUser() == isUser && iou.isNoGender()){
                return iou.avg();
            }
        }
        return MathUtils.randomRating();
    }
    
    public double avgOnUsersWithGender(int itemId, boolean gender){
        for(ItemOrUser iou:ious){
            if(iou.getId() == itemId && !iou.isUser() && iou.getGender() == gender){
                return iou.avg();
            }
        }
        return MathUtils.randomRating();
    }
    
    public double avgOn(boolean gender){
        double avg = general[genderToIntLocal(gender)].avg();
        if(Double.isNaN(avg)){
            return MathUtils.randomRating();
        }
        return avg;
    }
    
    /**
     * Рассчитать общую часть базового предиктора.
     * @param beta коэффициент затухания.
     * @param genderVals переменные касающиеся пола пользователей:
     * <ol>
     *  <li>учитывать ли пол пользователя,</li>
     *  <li>значение пола (мужчина - <code>true</code>).</li>
     * </ol>
     * @return 
     */
    private double countGeneralPart(double beta, boolean... genderVals){
        double userSum = 0.0;
        int userCnt = 0;
        for(ItemOrUser iou: ious){
            if(iou.isUser()){
                userSum += iou.avg(beta);
                userCnt++;
            }
        }
        return userSum / userCnt;
    }
    
    public double countBaselinePredictor(int userId, int itemId){
        return avgOn(userId, true) + avgOn(itemId, false) - 
               countGeneralPart(0.0, false, false);
    }
    
    public double countBaselinePredictor(int userId, int itemId, double beta){
        double predictor = countGeneralPart(beta, false, false);
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
        return MathUtils.roundToInt(sum);
    }
    
    /**
     * Получить кол-во всех оценок, присутствующих в хранилище.
     * @return кол-во всех оценок, присутствующих в хранилище.
     */
    public int getCnt() {
        return cnt;
    }
    
    /**
     * Перевод булевкой переменной, обзначающей пол в числовую форму.
     * @param gender пол.
     * @return числовое представление пола.
     */
    private static int genderToIntLocal(boolean gender){
        if(gender){
            return 0;
        }
        return 1;
    }
    
    /**
     * Для тестирования. 
     * @return 
     */
    public int _size(){
        return ious.size();
    }
}
