package task;

import java.util.List;
import java.util.StringTokenizer;
import task.learning.Users;

/**
 * Класс пользовательских оценок (объект - распознанная строка файла u.data 
 * или другого файлам с идентичной структурой.)
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Score implements Comparable<Score>{
    /**
     * Объект для парсинга строк
     */
    private static StringTokenizer st;
    
    private final int userId;
    private final int itemId;
    private final boolean gender;
    private int rating;
    private long timestamp;
    
    /**
     * Конструктор
     * @param line строка файла u.data или другого файла с идентичной структурой.
     */
    public Score(String line){
        st = new StringTokenizer(line, "\t");
        userId = Integer.parseInt(st.nextToken());
        itemId = Integer.parseInt(st.nextToken());
        rating = Integer.parseInt(st.nextToken());
        timestamp = Long.parseLong(st.nextToken());
        gender = Users.getInstance().getGender(userId);
    }

    public int getUserId() {
        return userId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRating() {
        return rating;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * ПРОВЕРЕНО!!! В выборкие нет случая, чтобы один и тот же пользователь
     * исправлял оценку одного и того же item'a (ставил другую оценку в другое
     * время).
     * @deprecated 
     */
    public static boolean addToList(List<Score> scores, Score score){
        if(scores.contains(score)){
            return false;
        }
        scores.add(score);
        return true;
    }
    
    /**
     * ПРОВЕРЕНО!!! В выборкие нет случая, чтобы один и тот же пользователь
     * исправлял оценку одного и того же item'a (ставил другую оценку в другое
     * время).
     * @deprecated 
     */
    public static boolean addToListComp(List<Score> scores, Score score){
        for(Score sc: scores){
            if(score.compareTo(sc) > 0){
                sc.timestamp = score.timestamp;
                sc.rating = score.rating;
                return true;
            }
            else if(score.compareTo(sc) < 0){
                return false;
            }
        }
        scores.add(score);
        return true;
    }
    
    /**
     * Метод для сравнения одинаковых оценок по времени. Сравниваются только те 
     * оценки, к которых userId и itemId равны.
     * @param o сравниваемая с текущей оценка.
     * @return результат сравнения.
     */
    @Override
    public int compareTo(Score o) {
       if(o.userId == userId && o.itemId == itemId){
           if(o.timestamp > timestamp){
               return 1;
           }
           else{
               return -1;
           }
       }
       return 0;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.userId;
        hash = 89 * hash + this.itemId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Score other = (Score) obj;
        if (this.userId != other.userId) {
            return false;
        }
        if (this.itemId != other.itemId) {
            return false;
        }
        return true;
    }

    public boolean getGender() {
        return gender;
    }
}
