
package task.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Ivchenko Oleg (Kirius VeLKerr)
 */
public abstract class CollectionsUtils {
    public static <K, V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return -res;
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
    
    public static <K, V extends Collection<K>, W extends Collection<V>> int totalSize(W coll){
        int totalSize = 0;
        for(V nestedColl: coll){
            totalSize += nestedColl.size();
        }
        return totalSize;
    }
}
