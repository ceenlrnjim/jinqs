package jinqs;

import java.util.*;

public interface IterableMap<K,V> extends Map<K,V>, Iterable<Map.Entry<K,V>> {
    Iterator<Map.Entry<K,V>> iterator();
}
