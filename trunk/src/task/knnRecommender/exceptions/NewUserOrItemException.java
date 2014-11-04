
package task.knnRecommender.exceptions;

/**
 * Исключение, описывающее тот факт, кто в систему пришёл НОВЫЙ пользователь либо 
 * item, для которого мы не можем найти похожих.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class NewUserOrItemException extends Exception{
    private static final String mess = "There is no such user or item in training dataset!";

    public NewUserOrItemException() {
        super(mess);
    }
}
