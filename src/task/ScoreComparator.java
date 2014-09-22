/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package task;

import java.util.Comparator;

/**
 * Компаратор для объектов класса пользовательских оценок.
 * Оценки сравниваются по времени. Это нужно для того, чтобы отсеять устаревшие
 * оценки. Например, если пользователь оценил один и тот же item сначала оценкой
 * А, а потом В, то оценка А нам уже ни о чём не говорит.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 * @deprecated класс пока не используется.
 */
public class ScoreComparator implements Comparator<Score>{

    @Override
    public int compare(Score sc1, Score sc2) {
        if(sc1.getTimestamp() > sc2.getTimestamp()){
            return 1;
        }
        else if(sc1.getTimestamp() < sc2.getTimestamp()){
            return -1;
        }
        return 0;
    }
}
