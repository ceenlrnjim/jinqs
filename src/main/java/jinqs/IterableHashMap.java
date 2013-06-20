package jinqs;

import java.util.*;

public class IterableHashMap<K,V> extends HashMap<K,V> implements IterableMap<K,V> {
    public Iterator<Map.Entry<K,V>> iterator() {
        return super.entrySet().iterator();
    }
}
