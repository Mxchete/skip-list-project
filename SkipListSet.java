import java.util.*;

// https://docs.oracle.com/javase/8/docs/api/java/util/SortedSet.html
public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> {

    private static int height = -1;
    private static int size = -1;
    private SkipListSetPayloadWrapper<T> root = null;

    public SkipListSet() {
        root = new SkipListSetPayloadWrapper<T>(null);
        height = 4;
        size = 0;
    }

    public boolean add(T object) {
        if (root == null || root.payload == null) {
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
        // root should hold all references, nullifying it and calling gc should free up
        // all memory
        System.gc();
        root = new SkipListSetPayloadWrapper<T>(null);
    }

    // TODO
    public Comparator<T> comparator() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(Object object) {
        if (search((T) object, false).payload.compareTo((T) object) == 0)
            return true;
        return false;
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

    // TODO
    public boolean equals(Collection<T> object) {
        if (object.size() == size && containsAll(object))
            return true;
        return false;
    }

    public T first() {
        if (root != null)
            return root.payload;
        return null;
    }

    // TODO
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
        if (root == null || root.payload == null)
            return true;
        return false;
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

    // TODO
    public boolean retainAll(Collection<?> objectCollection) {
        return false;
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

    // TODO
    public Object[] toArray() {
        return new Object[0];
    }

    // TODO
    @SuppressWarnings("hiding")
    public <T> T[] toArray(T[] array) {
        return null;
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
        int i = height - 1;

        while (true) {
            SkipListSetPayloadWrapper<T> right = temp.links.get(i).right;
            if (right != null && right.payload.compareTo(obj) <= 0) {
                temp = right;
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
            if (i > 0) {
                i--;
            } else
                break;
        }

        if (add)
            return itemWrapper;
        else
            return temp;
    }

    // https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html
    @SuppressWarnings("unchecked")
    private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T> {

        SkipListSetPayloadWrapper<T> iterItem;

        public SkipListSetIterator() {
            iterItem = (SkipListSetPayloadWrapper<T>) root;
        }

        public boolean hasNext() {
            if (iterItem == null)
                return false;

            return true;
        }

        public T next() {

            T returner = (T) iterItem.payload;
            iterItem = iterItem.links.get(0).right;
            return returner;
        }

        public void remove() {
            if (iterItem.payload == null)
                return;
            else {
                SkipListSet.this.remove((Object) iterItem.payload);
                iterItem.payload = null;
            }
        }

    }

    @SuppressWarnings("hiding")
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
