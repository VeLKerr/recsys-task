package task;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Класс пользовательских оценок (объект - распознанная строка файла u.data 
 * или другого файлам с идентичной структурой.)
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Score {
    /**
     * Объект для парсинга строк
     */
    private static StringTokenizer st;
    
    private final int userId;
    private final int itemId;
    private final int rating;
    private final long timestamp;
    
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
    }
    
    public Score(int user_id, int item_id, int estimate, long timestamp) {
        this.userId = user_id;
        this.itemId = item_id;
        this.rating = estimate;
        this.timestamp = timestamp;
    }
    
    /**
     * Являются ли 2 объекта похожими?
     * Имеется в виду, что в обоих случаях один и тот же пользователь
     * оценивал один и тот же item только в разное время.
     * @param score1
     * @param score2
     * @return 
     */
    public static boolean isSimilar(Score score1, Score score2){
        return score1.userId == score2.userId && 
               score1.itemId == score2.itemId;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.userId;
        hash = 97 * hash + this.itemId;
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
        return this.itemId == other.itemId;
    }
}
