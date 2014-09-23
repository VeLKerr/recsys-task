
package task.fordebug;

import java.util.HashSet;
import java.util.Set;
import task.Score;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class UniqueSet {
    private Set<Integer> userId;
    private Set<Integer> itemId;
    
    public UniqueSet(){
        this.userId = new HashSet<>();
        this.itemId = new HashSet<>();
    }
    
    public int add(Score score){
        int usOld = userId.size();
        int isOld = itemId.size();
        userId.add(score.getUserId());
        itemId.add(score.getItemId());
        int usNew = userId.size();
        int isNew = itemId.size();
        if(usOld != usNew && isOld != isNew){
            return 3;
        }
        else if(isOld != isNew){
            return 2;
        }
        else if(usOld != usNew){
            return 1;
        }
        else{
            return 0;
        }
    }
    
    public int size(){
        return itemId.size() + userId.size();
    }
}
