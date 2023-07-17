import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static int height = -1;
    private static int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;

    SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 1;
        size = 0;
        root.setLinks(null, null);
    }

    // TODO
    public boolean add(T object) {
        if (root == null || root.payload == null) {
            root = new SkipListSetPayloadWrapper<T>(object);
            return true;
        }
        SkipListSetPayloadWrapper<T> nearestWrapper = root.search(object, height);
        if (nearestWrapper.payload.compareTo(object) != 0) {
            return true;
        }
        return false;
    }

    public boolean addAll(Collection<? extends T> objectCollection) {
        int oldSize = size;
        for (T object : objectCollection) {
            add(object);
        }
        if (oldSize != size)
            return true;
        return false;
    }

    public void clear() {
        root = null;
        System.gc();
        root = new SkipListSetPayloadWrapper<T>(null);
    }

    // TODO
    public Comparator<T> comparator() {
        return null;
    }

    // TODO
    public boolean contains(Object object) {
        if (root.search((T) object, height).compareTo((T) object) == 0)
            return true;
        return false;
    }

    public boolean containsAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        for (Object object : objectCollection)
            returnValue = returnValue & contains(object);
        return false;
    }

    // TODO
    public boolean equals(Object object) {
        return false;
    }

    public T first() {
        if (root != null)
            return root.payload;
        return null;
    }

    // TODO
    public int hashCode() {
        return -1;
    }

    // TODO
    public SkipListSet<T> headSet(T object) {
        return null;
    }

    public boolean isEmpty() {
        if (root == null || root.payload == null)
            return true;
        return false;
    }

    // TODO
    public SkipListSetIterator<T> iterator() {
        return new SkipListSetIterator<T>();
    }

    // TODO
    public T last() {
        return null;
    }

    // TODO
    public boolean remove(Object object) {
        return false;
    }

    // TODO
    public boolean removeAll(Collection<?> objectCollection) {
        return false;
    }

    // TODO
    public boolean retainAll(Collection<?> objectCollection) {
        return false;
    }

    public int size() {
        return size;
    }

    // TODO
    public SkipListSet<T> subSet(T fromObject, T toObject) {
        return null;
    }

    // TODO
    public SkipListSet<T> tailSet(T object) {
        return null;
    }

    // TODO
    public Object[] toArray() {
        return new Object[0];
    }

    // TODO
    public <T> T[] toArray(T[] array) {
        return null;
    }

    public void reBalance() {
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        private SkipListSetPayloadWrapper<T> currentItem = null;

        public boolean hasNext() {
            return currentItem.links.get(0).right == null;
        }

        public T next() {
            T previousPayload = currentItem.payload;
            currentItem = currentItem.links.get(0).right;
            return previousPayload;
        }

        // TODO
        public void remove() {
        }

        SkipListSetIterator() {
            // this is safe
            this.currentItem = (SkipListSetPayloadWrapper<T>) root;
        }

    }

    private class SkipListSetPayloadWrapper<T extends Comparable<T>> {
        T payload;
        ArrayList<Links> links;

        SkipListSetPayloadWrapper(T payload) {
            this.payload = payload;
            this.links = new ArrayList<>();
        }

        private SkipListSetPayloadWrapper<T> search(T objectToFind, int curHeight) {

            if (payload.compareTo(objectToFind) == 0)
                return this;

            if (payload.compareTo(objectToFind) < 0)
                return links.get(curHeight).right.search(objectToFind, curHeight);

            if (payload.compareTo(objectToFind) > 0)
                return links.get(curHeight - 1).left.search(objectToFind, curHeight - 1);

            else
                return this;

        }

        private void setLinks(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.add(new Links(left, right));
        }

        private class Links {
            SkipListSetPayloadWrapper<T> left;
            SkipListSetPayloadWrapper<T> right;

            Links(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
                this.left = left;
                this.right = right;
            }
        }

    }
}
