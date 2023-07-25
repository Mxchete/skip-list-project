import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static int height = -1;
    private static int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;

    public SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 8;
        size = 0;
    }

    public boolean add(T object) {
        if (isEmpty()) {
            root = new SkipListSetPayloadWrapper<T>(object);
            for (int i = 0; i < height; i++) {
                root.setLinks(null, null);
            }
            size++;
            return true;
        }
        if (root.payload.compareTo(object) >= 0) {
            T oldRoot = root.payload;
            root.payload = object;
            add(oldRoot);
        }
        if (contains(object)) {
            return false;
        }
        int oldSize = size;
        search(object, true);
        if (oldSize == size) {
            return false;
        }
        if (size > Math.pow(2, height)) {
            raiseHeight();
        }
        return true;

    }

    public boolean addAll(Collection<? extends T> objectCollection) {
        boolean returnValue = false;
        for (T object : objectCollection) {
            returnValue = returnValue | add(object);
        }
        return returnValue;
    }

    public void clear() {
        root = null;
        // root should hold all references, nullifying it and calling gc should free up
        // all memory
        System.gc();
        root = new SkipListSetPayloadWrapper<T>(null);
    }

    public Comparator<T> comparator() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(Object object) {
        return search((T) object, false).payload.compareTo((T) object) == 0;
    }

    public boolean containsAll(Collection<?> objectCollection) {
        boolean returnValue = true;
        for (Object object : objectCollection) {
            if (!returnValue)
                break;
            returnValue = returnValue & contains(object);
        }
        return returnValue;
    }

    public boolean equals(Collection<T> object) {
        return object.size() == size && containsAll(object);
    }

    public T first() {
        return (root != null) ? root.payload : null;
    }

    public int hashCode() {
        Iterator<T> hashIterator = iterator();
        int result = 0;
        while (hashIterator.hasNext()) {
            result = result + Objects.hash(hashIterator.next());
        }
        return result;
    }

    public SkipListSet<T> headSet(T object) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return (root == null || root.payload == null);
    }

    public SkipListSetIterator<T> iterator() {
        return new SkipListSetIterator<T>();
    }

    public T last() {
        SkipListSetPayloadWrapper<T> current = root;
        int srchHeight = height - 1;
        while (current.links.get(srchHeight).right != null || srchHeight != 0) {
            if (current.links.get(srchHeight).right == null) {
                srchHeight--;
            } else {
                current = current.links.get(srchHeight).right;
            }
        }
        return current.payload;
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        SkipListSetPayloadWrapper<T> removeMe = search((T) object, false);
        if (removeMe.payload.compareTo((T) object) != 0)
            return false;
        if (removeMe.payload.compareTo(root.payload) == 0) {
            T save = removeMe.links.get(0).right.payload;
            remove(save);
            removeMe.payload = save;
            size--;
            return true;
        }
        for (int i = 0; i < removeMe.links.size(); i++) {
            SkipListSetPayloadWrapper<T> right = removeMe.links.get(i).right;
            SkipListSetPayloadWrapper<T> left = removeMe.links.get(i).left;
            if (right != null) {
                right.setLinksAtIdx(i, left, right.links.get(i).right);
            }
            if (left != null) {
                left.setLinksAtIdx(i, left.links.get(i).left, right);
            }
            removeMe.setLinksAtIdx(i, null, null);
        }
        size--;
        return true;
    }

    public int print_debug(T object) {
        SkipListSetPayloadWrapper<T> temp = root;
        int i = height - 1;
        System.out.println("obj: " + object);
        System.out.println("obj hash: " + object.hashCode());

        while (true) {
            SkipListSetPayloadWrapper<T> right = temp.links.get(i).right;
            System.out.println("lvl: " + i);
            System.out.println("nde: " + temp.payload);
            if (right != null)
                System.out.println("rht: " + right.payload);
            if (right != null && right.payload.compareTo((T) object) <= 0) {
                temp = right;
                continue;
            }
            if (temp.payload.compareTo((T) object) == 0) {
                System.out.println(temp.links.get(i).right);
                System.out.println(temp.links.get(i).left);
            }
            if (i > 0) {
                i--;
            } else
                break;
        }
        return 0;
    }

    public boolean removeAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        for (Object object : objectCollection)
            returnValue = returnValue & remove(object);
        return false;
    }

    public boolean retainAll(Collection<?> objectCollection) {
        boolean returnValue = false;
        Iterator<T> retainIterator = iterator();
        while (retainIterator.hasNext()) {
            T retain = retainIterator.next();
            if (!objectCollection.contains(retain)) {
                returnValue = returnValue | remove(retain);
            }
        }
        return returnValue;
    }

    public int size() {
        return size;
    }

    public SkipListSet<T> subSet(T fromObject, T toObject) {
        throw new UnsupportedOperationException();
    }

    public SkipListSet<T> tailSet(T object) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        SkipListSetPayloadWrapper<T> searcher = root;
        Object[] skipListAsArray = new Object[size];
        for (int i = 0; i < size; i++) {
            if (searcher != null) {
                skipListAsArray[i] = searcher.payload;
                searcher = searcher.links.get(0).right;
            }
        }
        return skipListAsArray;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] array) {
        SkipListSetPayloadWrapper<?> searcher = root;
        if (array.length < size) {
            array = Arrays.copyOf(array, size);
        }
        for (int i = 0; i < size; i++) {
            if (searcher != null) {
                array[i] = (T) searcher.payload;
                searcher = searcher.links.get(0).right;
            }
        }
        if (array.length > size) {
            array[size] = null;
        }
        return array;
    }

    public void reBalance() {
        SkipListSetPayloadWrapper<T> searcher = root;
        SkipListSet<T> newList = new SkipListSet<T>();
        while (searcher != null) {
            newList.add(searcher.payload);
            searcher = searcher.links.get(0).right;
        }
        root = newList.root;
        System.gc();
    }

    private void raiseHeight() {
        root.setLinks(null, null);
        height++;
    }

    private int heightRandomizer() {
        int randomHeight = 0;
        while (Math.round(Math.random()) == 1 && randomHeight != height - 1) {
            randomHeight++;
        }
        return randomHeight;
    }

    private SkipListSetPayloadWrapper<T> search(T obj, boolean add) {
        SkipListSetPayloadWrapper<T> temp = root;
        SkipListSetPayloadWrapper<T> itemWrapper = new SkipListSetPayloadWrapper<T>(obj);
        int addHeight = heightRandomizer();
        if (add) {
            for (int i = 0; i <= addHeight; i++) {
                itemWrapper.setLinks(null, null);
            }
            size++;
        }

        for (int i = height - 1; i >= 0; i--) {
            SkipListSetPayloadWrapper<T> right = temp.links.get(i).right;
            if (right != null && right.payload.compareTo(obj) <= 0) {
                temp = right;
                i++;
                continue;
            }
            if (add && i <= addHeight) {
                if (right != null && right.payload.compareTo(obj) == 0) {
                    return right;
                }
                itemWrapper.setLinksAtIdx(i, temp, right);
                if (right != null) {
                    right.links.get(i).left = itemWrapper;
                }
                temp.links.get(i).right = itemWrapper;
            }
        }

        if (add)
            return itemWrapper;
        else
            return temp;
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    @SuppressWarnings("unchecked")
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        private SkipListSetPayloadWrapper<T> iterItem;

        public boolean hasNext() {
            return iterItem != null;
        }

        public T next() {
            T returner = (T) iterItem.payload;
            iterItem = iterItem.links.get(0).right;
            return returner;
        }

        public void remove() {
            if (iterItem == null || iterItem.payload == null)
                return;
            else {
                SkipListSet.this.remove((Object) iterItem.payload);
            }
        }

        private SkipListSetIterator() {
            iterItem = (SkipListSetPayloadWrapper<T>) root;
        }

    }

    private class SkipListSetPayloadWrapper<T extends Comparable<T>> {

        T payload;
        ArrayList<Links> links;

        SkipListSetPayloadWrapper(T payload) {
            this.payload = payload;
            this.links = new ArrayList<>();
        }

        private void setLinks(SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.add(new Links(left, right));
        }

        private void setLinksAtIdx(int index, SkipListSetPayloadWrapper<T> left, SkipListSetPayloadWrapper<T> right) {
            links.set(index, new Links(left, right));
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
