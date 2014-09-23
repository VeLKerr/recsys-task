
package task.utils;

/**
 * Singleton-класс, с помощью которого строится название файла, 
 * открывающегося на чтение.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class FileNameBuilder {
    private static FileNameBuilder instance;
    private static final String bs = ".base";
    private static final String ts = ".test";
    private int number;
    private boolean isTest;
    
    private FileNameBuilder(){
    }
    
    public static FileNameBuilder getBuilder(){
        if(instance == null){
            instance = new FileNameBuilder();
        }
        return instance;
    }
    
    /**
     * Установка параметров названия файла.
     * @param number № выборки (от 1 до 5).
     * @param isTest является ли выборка проверяющей.
     */
    public void setParameters(int number, boolean isTest){
        this.number = number;
        this.isTest = isTest;
    }
    
    /**
     * Установка типа выборки.
     * @param isTest является ли выборка проверяющей.
     */
    public void setIsTest(boolean isTest){
        this.isTest = isTest;
    }
    
    /**
     * На основании введённых параметров, построить название файла.
     * @return строка с названием файла.
     */
    public String buildFName(){
        StringBuilder sb = new StringBuilder("u");
        if(number != 0){
            sb.append(number);
        }
        else{
            return sb.append(".data").toString();
        }
        if(isTest){
            sb.append(ts);
        }
        else{
            sb.append(bs);
        }
        return sb.toString();
    }
}
