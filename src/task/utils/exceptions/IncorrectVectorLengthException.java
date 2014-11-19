
package task.utils.exceptions;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class IncorrectVectorLengthException extends RuntimeException{
    private static final String mess = "Lengths are not equals (";
    
    public IncorrectVectorLengthException(int len1, int len2){
        super(new StringBuilder(mess).append(len1).append(" != ").append(len2).append(")!").toString());
    }
}
