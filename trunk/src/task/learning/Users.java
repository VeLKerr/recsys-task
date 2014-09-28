
package task.learning;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import task.utils.ConsoleUtils;

/**
 * Singleton-класс, описывающий всех пользователей, которые присутствуют в выборке.
 * При увеличении кол-ва учитываемых параметров (если будет не только пол) - переделать класс на MAP!
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Users {
    private static final String delim = "|";
    private static Users instance;
    /**
     * Объект для парсинга строк
     */
    private static StringTokenizer st;
    private final List<Integer> women; //женщин в выборке меньше, с ними и работаем
    
    private Users(){
        this.women = new ArrayList<>();
    }
    
    public static Users getInstance(){
        if(instance == null){
            instance = new Users();
        }
        return instance;
    }
    
    public void add(String line){
        st = new StringTokenizer(line, delim);
        int userId = Integer.parseInt(st.nextToken());
        st.nextToken();
        if(st.nextToken().contains("F")){
            women.add(userId);
        }
    }
    
    public boolean getGender(int userId){
        return !women.contains(userId);
    }

    public List<Integer> getWomen() {
        return women;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Women: ");
        sb.append(ConsoleUtils.listIntegersToString(women));
        return sb.toString();
    }
}
