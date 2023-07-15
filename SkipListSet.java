import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
class SkipListSet<T> implements SortedSet<T> {
    public boolean add(T object) {
        return false;
    }

    public boolean addAll(Collection<? extends T> objectCollection) {
        return false;
    }

    public void clear() {
    }

    public Comparator<T> comparator() {
    }

    public boolean contains(Object object) {
        return false;
    }

    public boolean containsAll(Collection<?> objectCollection) {
        return false;
    }

    public boolean equals(Object object) {
        return false;
    }

    public T first() {
    }

    public int hashCode() {
        return -1;
    }

    public SkipListSet<T> headSet(T object) {
    }

    public boolean isEmpty() {
        return false;
    }

    public SkipListSetIterator<T> iterator() {
        return new SkipListSetIterator<T>();
    }

    public T last() {
    }

    public boolean remove(Object object) {
        return false;
    }

    public boolean removeAll(Collection<?> objectCollection) {
        return false;
    }

    public boolean retainAll(Collection<?> objectCollection) {
        return false;
    }

    public int size() {
        return -1;
    }

    public SkipListSet<T> subSet(T fromObject, T toObject) {
    }

    public SkipListSet<T> tailSet(T object) {
    }

    public Object[] toArray() {
        return new Object[0];
    }

    public <T> T[] toArray(T[] array) {
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    private class SkipListSetIterator<T> implements Iterator {

        public boolean hasNext() {
            return false;
        }

        public T next() {
        }

        public void remove() {
        }

    }

    private class SkipListSetPayloadWrapper implements Comparable {
        T Payday;
        ArrayList<SkipListSetPayloadWrapper> links;

        public int compareTo(Object o) {
            return -1;
        }

        SkipListSetPayloadWrapper(T Payday) {
        }

    }
}
