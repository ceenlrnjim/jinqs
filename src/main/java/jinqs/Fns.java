package jinqs;

import java.util.Map;

public class Fns {
    public static class Predicates {
        public static <T> Fn1<T[], Boolean> indexEquals(final int index, final Object value) {
            return new Fn1<T[],Boolean>() {
                public Boolean apply(T[] t) {
                    return t[index].equals(value);
                }
            };
        }

        public static <K,V> Fn1<Map<K,V>, Boolean> valueEquals(final K key, final V value) {
            return new Fn1<Map<K,V>,Boolean>() {
                public Boolean apply(Map<K,V> t) {
                    return t.get(key).equals(value);
                }
            };
        }

    }

    public static class Accessors {
        public static <T> Fn1<T[], T> valueAtIndex(final int index) {
            return new Fn1<T[],T>() {
                public T apply(T[] t) {
                    return t[index];
                }
            };
        }

        public static <K,V> Fn1<Map<K,V>, V> valueAtKey(final K key) {
            return new Fn1<Map<K,V>,V>() {
                public V apply(Map<K,V> t) {
                    return t.get(key);
                }
            };
        }

    }

    public final static <T> Fn1<T,T> identity() {
        return new Fn1<T,T>() {
            public T apply(T t) {
                return t;
            }
        };
    };
}
