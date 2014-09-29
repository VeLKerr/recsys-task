package task.learning;

/**
 * Класс для хранения суммарных оценок по каждому item'у или
 * пользователю.
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class ItemOrUser extends AverageRating{
    /**
     * id item'а или пользователя.
     */
    private final int id;
    /**
     * Суммарная оценка по item'ам даного пользователя.
     */
    private final boolean isUser;
    /**
     * Пол. 1 - без распределения, 
     * 2 - мужской, 3 - женский.
     */
    private final int gender;
    
    protected ItemOrUser(boolean gender){
        super();
        if(gender){
            this.gender = 2;
        }
        else{
            this.gender = 3;
        }
        this.id = 0;
        this.isUser = false;
    }
    
    public ItemOrUser(int id, boolean isUser) {
        super();
        this.id = id;
        this.isUser = isUser;
        this.gender = 1;
    }

    protected ItemOrUser(int id, boolean isUser, boolean gender) {
        super();
        this.id = id;
        this.isUser = isUser;
        if(gender){
            this.gender = 2;
        }
        else{
            this.gender = 3;
        }
    }
    
    /**
     * Получить id item'а или пользователя.
     * @return id item'а или пользователя.
     */
    public int getId() {
        return id;
    }
    
    /**
     * Получить булевское значение, определяющее, к чему относится эта оценка.
     * @return <code>true</code>, если к пользователю и <code>false</code>, если
     * к item'у.
     */
    public boolean isUser() {
        return isUser;
    }
    
    public boolean getGender(){ //TODO: To throw the NoGenderException!
        if(isUser){
            System.err.println("This object hesn\'t gender!");
        }
        return gender == 2;
    }
    
    public boolean isNoGender(){
        return gender == 1;
    }
}
