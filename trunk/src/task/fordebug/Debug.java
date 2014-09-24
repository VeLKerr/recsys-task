
package task.fordebug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import task.Score;
import static task.Task.path;
import task.learning.GeneralAverageRating;
import task.utils.FileNameBuilder;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public class Debug {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        UniqueSet us = new UniqueSet();
        GeneralAverageRating gav = new GeneralAverageRating();
        FileNameBuilder fnb = FileNameBuilder.getBuilder();
        fnb.setParameters(6, true);
        File f = new File(path + fnb.buildFName());
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String line = null;
        int lineCnt = 0;
        while((line = br.readLine()) != null){
            Score score = new Score(line);
            us.add(score);
            lineCnt++;
//            System.out.println(us.add(score));
            gav.add(score);
        }
        System.out.println(lineCnt);
        System.out.println(gav._size());
        System.out.println(gav._check());
        gav._check();
        System.out.println(us.size());
    }
    
}
